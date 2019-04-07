package com.ericc.the.game.systems.logic;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.ericc.the.game.GameEngine;
import com.ericc.the.game.Mappers;
import com.ericc.the.game.components.ActiveComponent;
import com.ericc.the.game.components.AgencyComponent;
import com.ericc.the.game.components.StatsComponent;
import com.ericc.the.game.components.SyncComponent;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Activity System is based on passing an activity token (ActiveComponent).
 * From the point of view of game systems, there is always exactly one entity being active.
 * However, there exists a notion of "logical time", that is, actions can take shorter or longer amounts of time.
 * Actors accumulate time units (TU), each in their own separate pool. Each action costs
 * a certain amount of TU deducted from the pool. Player action replenish actors' pools
 * with TU amount equal to its cost. TU balance can be negative -- that means that actor cannot move until the balance
 * becomes positive again.
 * <p>
 * Actors are arranged in a priority queue {@link #pending}, sorted by amount of TU left.
 * If several actors have the same amount of TU left, the precise order of their actions is determined based
 * on their initiative.
 */

public class ActivitySystem extends EntitySystem implements EntityListener {

    private ImmutableArray<Entity> sapient;
    private ImmutableArray<Entity> entities;
    private ImmutableArray<Entity> active;
    private ImmutableArray<Entity> synchronizing;
    private ActiveComponent token = new ActiveComponent();

    private Comparator<Entity> timeLeftComparator = Comparator.comparingInt(e -> Mappers.agency.get(e).delay);
    private PriorityQueue<Entity> pending = new PriorityQueue<>(timeLeftComparator);
    private Array<Entity> actingInThisMoment = new Array<>(false, 512);
    private GameEngine gameEngine;

    public ActivitySystem(GameEngine gameEngine, int priority) {
        super(priority);
        this.gameEngine = gameEngine;
    }

    @Override
    public void addedToEngine(Engine engine) {
        sapient = engine.getEntitiesFor(Family.all(AgencyComponent.class, StatsComponent.class).get());
        entities = engine.getEntitiesFor(Family.all(AgencyComponent.class).get());
        active = engine.getEntitiesFor(Family.all(ActiveComponent.class).get());
        synchronizing = engine.getEntitiesFor(Family.all(SyncComponent.class).get());

        engine.addEntityListener(Family.all(AgencyComponent.class).get(), this);
        for (Entity entity : entities) {
            pending.add(entity);
        }
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : active) {
            if (!Mappers.agency.has(entity)) {
                entity.remove(ActiveComponent.class);
            } else if (Mappers.agency.get(entity).delay > 0) {
                entity.remove(ActiveComponent.class);
                pending.add(entity);
            }
        }

        if (synchronizing.size() > 0) {
            for (Entity entity : synchronizing) {
                entity.remove(SyncComponent.class);
            }
            gameEngine.stopSpinning();
            return;
        }

        if (actingInThisMoment.isEmpty()) {
            findActingInThisMoment();
            rollInitiative();
        }

        if (!actingInThisMoment.isEmpty()) {
            Entity entity = actingInThisMoment.pop();
            entity.add(token);
        }
    }

    private void findActingInThisMoment() {
        if (pending.isEmpty())
            return;

        Entity first = pending.peek();
        if (first == null) {
            return;
        }

        int dt = Mappers.agency.get(first).delay;
        for (Entity entity : pending) {
            Mappers.agency.get(entity).delay -= dt;
        }

        while (!pending.isEmpty() && Mappers.agency.get(pending.peek()).delay <= 0) {
            actingInThisMoment.add(pending.poll());
        }
    }

    private void rollInitiative() {
        for (Entity entity : sapient) {
            StatsComponent stats = Mappers.stats.get(entity);
            Mappers.agency.get(entity).initiative = ((stats.agility + stats.intelligence) / 4
                    + ThreadLocalRandom.current().nextInt(1, 20));
        }

        actingInThisMoment.sort(Comparator.comparingInt(a -> Mappers.agency.get(a).initiative));
    }

    @Override
    public void entityAdded(Entity entity) {
        pending.add(entity);
    }

    @Override
    public void entityRemoved(Entity entity) {
        pending.remove(entity);
        actingInThisMoment.removeValue(entity, true);
    }
}

package com.ericc.the.game.systems.logic;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ericc.the.game.Mappers;
import com.ericc.the.game.animations.DeathAnimation;
import com.ericc.the.game.components.*;

public class DamageSystem extends IteratingSystem {
    public DamageSystem(int priority) {
        super(Family.all(PositionComponent.class, AttackComponent.class).get(), priority);
    }

    @Override
    protected void processEntity(Entity attack, float deltaTime) {
        PositionComponent pos = Mappers.position.get(attack);
        int damage = Mappers.attack.get(attack).damage;
        Entity subject = pos.map.entityMap.get(pos.xy);

        // Target tile has a hittable entity standing on it (non-player and possessing statistics)
        if (subject != null && !Mappers.player.has(subject) && Mappers.stats.has(subject)) {
            StatsComponent stats = Mappers.stats.get(subject);

            takeDamage(stats, damage);

            if (stats.health <= 0) {
                die(subject);
            }

            // Fade out the attack animation
        }

        attack.add(new AnimationComponent(new DeathAnimation(1 / 0.3f, false, 1.0f)));
        attack.add(new DeathComponent());
    }

    private void die(Entity subject) {
        subject.add(new AnimationComponent(
                new DeathAnimation(1 / 0.8f, true, 0.5f)));
        subject.add(new DeathComponent());
    }

    // Wrapped in a method since the damage calculation will most likely become
    // more complicated when armor / damage types and other factors that change
    // the base damage value roll in.
    private void takeDamage(StatsComponent stats, int damage) {
        stats.health -= damage;
    }
}
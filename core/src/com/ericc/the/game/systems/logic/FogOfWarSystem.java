package com.ericc.the.game.systems.logic;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.ericc.the.game.Mappers;
import com.ericc.the.game.components.FieldOfViewComponent;
import com.ericc.the.game.components.ScreenBoundariesComponent;
import com.ericc.the.game.entities.Player;
import com.ericc.the.game.entities.Screen;
import com.ericc.the.game.map.CurrentMap;
import com.ericc.the.game.map.Map;

public class FogOfWarSystem extends EntitySystem {

    private Player player;
    private ScreenBoundariesComponent visibleMapArea;

    public FogOfWarSystem(Player player, Screen screen) {
        super(9998); // remember to change it so it is larger than FOV priority

        this.player = player;
        this.visibleMapArea = Mappers.screenBoundaries.get(screen);
    }

    @Override
    public void addedToEngine(Engine engine) {}

    @Override
    public void update(float deltaTime) {
        FieldOfViewComponent playersFov = Mappers.fov.get(player);

        for (int y = visibleMapArea.top; y >= visibleMapArea.bottom; --y) {
            for (int x = visibleMapArea.left; x <= visibleMapArea.right; ++x) {
                if (CurrentMap.map.inBoundaries(x, y) && playersFov.visibility.get(x, y)) {
                    CurrentMap.map.markAsSeenByPlayer(x, y);
                }
            }
        }
    }
}

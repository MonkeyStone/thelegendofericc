package com.ericc.the.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.ericc.the.game.Map.Map;

public class Player extends Entity {
    public PositionComponent pos;
    public RenderableComponent renderable;

    public Player(int x, int y) {
        pos = new PositionComponent(x, y);
        renderable = new RenderableComponent(new Sprite(Media.playerFront));
        renderable.sprite.setOrigin(0, -0.5f);
        add(pos);
        add(renderable);
    }

    public void update(KeyboardControls controls, Map map) {
        if (controls.left) {
            if (map.isPassable(pos.x -1, pos.y)) {
                pos.x -= 1;
            }
            pos.dir = Direction.W;
        } else if (controls.right) {
            if (map.isPassable(pos.x + 1, pos.y)) {
                pos.x += 1;
            }
            pos.dir = Direction.E;
        } else if (controls.up) {
            if (map.isPassable(pos.x, pos.y + 1)) {
                pos.y += 1;
            }
            pos.dir = Direction.N;
        } else if (controls.down) {
            if (map.isPassable(pos.x , pos.y - 1)) {
                pos.y -= 1;
            }
            pos.dir = Direction.S;
        }
        controls.clear();
    }
}

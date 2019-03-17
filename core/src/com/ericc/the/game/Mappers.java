package com.ericc.the.game;

import com.badlogic.ashley.core.ComponentMapper;
import com.ericc.the.game.components.*;


/**
 * ComponentMappers are the preferred (fastest) way of retrieving components from entities.
 * Instead of using Entity.getComponent() or creating a redundant ComponentMapper every time,
 * use the statics from here.
 */
public class Mappers {
    public static final ComponentMapper<PositionComponent> position = ComponentMapper.getFor(PositionComponent.class);
    public static final ComponentMapper<DirectionComponent> direction = ComponentMapper.getFor(DirectionComponent.class);
    public static final ComponentMapper<AffineAnimationComponent> affineAnimation = ComponentMapper.getFor(AffineAnimationComponent.class);
    public static final ComponentMapper<SpriteSheetComponent> spriteSheet = ComponentMapper.getFor(SpriteSheetComponent.class);
    public static final ComponentMapper<CurrentActionComponent> currentAction = ComponentMapper.getFor(CurrentActionComponent.class);
    public static final ComponentMapper<FieldOfViewComponent> fov = ComponentMapper.getFor(FieldOfViewComponent.class);
    public static final ComponentMapper<ScreenBoundariesComponent> boundaries = ComponentMapper.getFor(ScreenBoundariesComponent.class);
}

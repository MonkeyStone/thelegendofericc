package com.ericc.the.game.animations;

import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

import static java.lang.Float.min;

/**
 * A simple parabolic jump.
 */
public class JumpAnimation implements AffineAnimation {
    private Vector2 start;
    private float duration;
    private float height;

    /**
     * @param transition the vector from start to end position
     */
    public JumpAnimation(Vector2 transition, float height, float duration) {
        this.start = transition.scl(-1); // Start here, land at (0, 0)
        this.duration = duration;
        this.height = height;
    }

    @Override
    public Affine2 getTransform(float time) {
        Affine2 transform = new Affine2();
        float timeFraction = time / duration;

        if (timeFraction > 1) {
            transform.setToTranslation(0, 0);
            return transform;
        }

        // Horizontal velocity is constant.
        Vector2 currentPosition = start.cpy().interpolate(Vector2.Zero, timeFraction, Interpolation.linear);
        transform.setToTranslation(currentPosition);

        // Interpolation.pow2Out describes a half-parabola. We get the second half by symmetry.
        float heightTimeFraction = min(timeFraction, 1 - timeFraction);
        float currentHeight = height * Interpolation.pow2Out.apply(heightTimeFraction);
        transform.translate(0, currentHeight);

        return transform;
    }

    public boolean isOver(float time) {
        return time > duration;
    }
}

package controller;

import model.Light;
import transforms.Col;
import transforms.Vec3D;

import javax.swing.*;
import java.util.Random;

/**
 * Handles light animation.
 * Maintains:
 * <ul>
 *   <li>animation on/off state,</li>
 *   <li>timer for animation steps,</li>
 *   <li>orbital path parameters,</li>
 *   <li>light color changes.</li>
 * </ul>
 * Calls the redraw callback after each change.
 */
public class LightAnimator {

    /** Timer delay between animation steps (in milliseconds). */
    private static final int TIMER_DELAY = 16;

    /** Angle increment in radians for each animation step. */
    private static final double ANIM_STEP = 0.03;

    /** Radius of the circular orbital path. */
    private static final double ORBIT_RADIUS = 3.4;

    /** Default x-coordinate of the orbit center. */
    private static final double DEFAULT_CENTER_X = 1;

    /** Default y-coordinate of the orbit center. */
    private static final double DEFAULT_CENTER_Y = 0.5;

    /** Default base z-coordinate of the orbit. */
    private static final double DEFAULT_BASE_Z = -2.0;

    /** Amplitude of height changes in the Z axis. */
    private static final double Z_AMPLITUDE = 2;

    /** Current x-coordinate of the orbit center. */
    private double orbitCenterX = DEFAULT_CENTER_X;

    /** Current y-coordinate of the orbit center. */
    private double orbitCenterY = DEFAULT_CENTER_Y;

    /** Current base z-coordinate of the orbit. */
    private double orbitBaseZ = DEFAULT_BASE_Z;

    /** The light source to animate. */
    private final Light light;

    /** Callback to trigger a redraw after animation updates. */
    private final Runnable redrawCallback;

    /** Timer for animation steps. */
    private final Timer timer;

    /** Random number generator for color changes. */
    private final Random random = new Random();

    /** Whether the animation is currently enabled. */
    private boolean enabled = false;

    /** Current angle in the orbital path (in radians). */
    private double angle = 0.0;

    /**
     * Creates a new light animator.
     *
     * @param light the light source to animate
     * @param onChange callback to be invoked after each animation update
     */
    public LightAnimator(Light light, Runnable onChange) {
        this.light = light;
        this.redrawCallback = onChange;

        this.timer = new Timer(TIMER_DELAY, e -> {
            updateAnimatedLight();
            redrawCallback.run();
        });

        timer.start();
    }

    /**
     * Toggles the light animation on/off.
     */
    public void toggle() {
        enabled = !enabled;

        if (enabled) {
            syncOrbitToCurrentPosition();
            timer.start();
            redrawCallback.run();
        } else {
            timer.stop();
        }
    }

    /**
     * Randomizes the light color.
     * Generates a random color with RGB components between 80 and 255.
     */
    public void randomizeColor() {
        int r = 80 + random.nextInt(176);
        int g = 80 + random.nextInt(176);
        int b = 80 + random.nextInt(176);

        Col randomColor = new Col((r << 16) | (g << 8) | b);
        light.setColor(randomColor);

        redrawCallback.run();
    }

    /**
     * Computes an animated step of the light along its orbital path.
     * The light moves in a circle in the XY plane and simultaneously
     * changes its height along the Z axis.
     */
    private void updateAnimatedLight() {
        angle += ANIM_STEP;

        double x = orbitCenterX + Math.cos(angle) * ORBIT_RADIUS;
        double y = orbitCenterY + Math.sin(angle) * ORBIT_RADIUS;
        double z = orbitBaseZ + Math.sin(angle * 1.7) * Z_AMPLITUDE;

        light.setPosition(x, y, z);
    }

    /**
     * Recalculates the orbit center so that the animation smoothly continues
     * from the current manually set light position.
     */
    private void syncOrbitToCurrentPosition() {
        Vec3D currentPos = light.getPosition();

        orbitCenterX = currentPos.getX() - Math.cos(angle) * ORBIT_RADIUS;
        orbitCenterY = currentPos.getY() - Math.sin(angle) * ORBIT_RADIUS;
        orbitBaseZ = currentPos.getZ() - Math.sin(angle * 1.7) * Z_AMPLITUDE;
    }
}
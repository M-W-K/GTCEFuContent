package com.m_w_k.gtcefucontent.api.util;

import javax.vecmath.Vector3d;

public class InterpolatingPoint {

    protected static final int PATH_TIME = 200;

    protected float timeRandomMax = 0;
    protected float randomPathTime;

    protected float currentPathTime = 0;

    protected final InterpolationMode mode;

    protected final Vector3d origin = new Vector3d();
    protected final Vector3d destination = new Vector3d();
    protected final Vector3d position = new Vector3d();

    protected boolean finished;

    public InterpolatingPoint(InterpolationMode mode, float timeRandomMax) {
        this.mode = mode;
        this.timeRandomMax = timeRandomMax;
        this.finished = true;
    }

    public void setup(double x1, double y1, double z1, double x2, double y2, double z2) {
        this.origin.set(x1, y1, z1);
        this.destination.set(x2, y2, z2);
        this.currentPathTime = 0;
        this.randomPathTime = (float) ((Math.random() * 2 - 1) * this.timeRandomMax);
        this.finished = false;
    }

    public void progress(float progression) {
        currentPathTime += progression;
        if (currentPathTime > PATH_TIME + randomPathTime) currentPathTime = PATH_TIME + randomPathTime;

        position.interpolate(origin, destination, mode.getFrom(currentPathTime / (PATH_TIME + randomPathTime)));
        if (currentPathTime >= PATH_TIME + randomPathTime) finished = true;
    }

    public double x() {
        return position.getX();
    }

    public double y() {
        return position.getY();
    }

    public double z() {
        return position.getZ();
    }

    public boolean isFinished() {
        return finished;
    }

    public enum InterpolationMode {

        LINEAR,
        ACCELERATING,
        DECELERATING;

        double getFrom(double interpolation) {
            return switch (this) {
                case LINEAR -> interpolation;
                case ACCELERATING -> Math.pow(interpolation, 2);
                case DECELERATING -> Math.pow(interpolation, 0.5);
            };
        }
    }
}

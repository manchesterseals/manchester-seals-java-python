package com.manchesterseals.model;

public class VelocityResponse {

    private double velocity;

    private long minTime;

    private long maxTime;

    public VelocityResponse(double velocity, long minTime, long maxTime) {
        this.velocity = velocity;
        this.minTime = minTime;
        this.maxTime = maxTime;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public long getMinTime() {
        return minTime;
    }

    public void setMinTime(long minTime) {
        this.minTime = minTime;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }

    @Override
    public String toString() {
        return "VelocityResponse{" +
                "velocity=" + velocity +
                ", minTime=" + minTime +
                ", maxTime=" + maxTime +
                '}';
    }
}


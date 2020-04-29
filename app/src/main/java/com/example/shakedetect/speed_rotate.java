package com.example.shakedetect;

public class speed_rotate {

    public int speed_Rotate;
    private static final speed_rotate ourInstance = new speed_rotate();

    public static speed_rotate getInstance() {
        return ourInstance;
    }

    private speed_rotate() {
    }
}
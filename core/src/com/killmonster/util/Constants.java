package com.killmonster.util;

public final class Constants {
    
    public static boolean DEBUG = true;
    public static boolean PAUSE = false;
    public static boolean COMPLETED = false;
    // Graphics constants
    public static final float PPM = 100;
    public static final int V_WIDTH = 26*32;
    public static final int V_HEIGHT = 14*32;
    
    // Physics constants
    public static final int GRAVITY = -10;
    public static final int GROUND_FRICTION = 1;
    
    
    private Constants() {
        throw new AssertionError();
    }
    
}
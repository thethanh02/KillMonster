package com.killmonster.util;

import com.killmonster.map.GameMap;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class CameraUtils {
    
    private CameraUtils() {
        
    }

    
    /**
     * Bounds the Camera within the specified TiledMap.
     * @param camera The Camera which renders the TiledMap
     * @param map The TiledMap to which the camera is bound.
     */
    public static void boundCamera(Camera camera, GameMap map) {
        Vector3 position = camera.position;

        float startX = camera.viewportWidth / 2;
        float startY = camera.viewportHeight / 2;
        float endX =  (map.getMapWidth() * map.getMapTileSize()) / Constants.PPM - camera.viewportWidth / 2;
        float endY = (map.getMapHeight() * map.getMapTileSize()) / Constants.PPM - camera.viewportHeight / 2;
        
        if (position.x < startX) {
            position.x = startX;
        }
        if (position.y < startY) {
            position.y = startY;
        }
        
        if (position.x > endX) {
            position.x = endX;
        }
        if (position.y > endY) {
            position.y = endY;
        }
        
        camera.position.set(position);
        camera.update();
    }
    
    public static void lerpToTarget(Camera camera, Vector2 target) {
        Vector3 position = camera.position;
        position.x = camera.position.x + (target.x - camera.position.x) * .1f;
        position.y = camera.position.y + (target.y - camera.position.y) * .1f;
        camera.position.set(position);
        camera.update();
    }
    
}

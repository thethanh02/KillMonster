package com.killmonster.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Utils {
    
    private static Array<TextureRegion> frames = new Array<>();

    public static Texture getTexture(){

        Pixmap pixmap;
        try {
            pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        }catch (GdxRuntimeException e)
        {
            pixmap = new Pixmap(1,1, Pixmap.Format.RGB565);
        }
        pixmap.setColor(Color.BLACK);
        pixmap.drawRectangle(0,0,1,1);

        return new Texture(pixmap);
    }

    /**
     * Calculates the distance between position x1 and x2, always returning positive value.
     * @param x1 position x1.
     * @param x2 position x2.
     * @return positive distance value.
     */
    public static float getDistance(float x1, float x2) {
        float distance = x1 - x2;
        return (distance > 0) ? distance : -distance;
    }
    
    /**
     * Create animation by extracting a set of TextureRegion from the specified Texture.
     * Note that the all sprites must be on the same row.
     * @param texture The Texture from which to extract TextureRegion
     * @param frameDuration The time between frames in seconds.
     * @param firstFrameCount The starting frame count of the frames to extract,
     *        usually 0 if correct offsetX is given.
     * @param lastFrameCount The last frame count of the frames to extract.
     * @param offsetX The x offset to apply in order to reach the first frame.
     * @param offsetY The y offset to apply in order to reach the first frame.
     * @param width The width of the TextureRegion. May be negative to flip the sprite when drawn.
     * @param height The height of the TextureRegion. May be negative to flip the sprite when drawn.
     * @return Extracted animation.
     */
    public static Animation<TextureRegion> createAnimation(Texture texture, float frameDuration,
            int firstFrameCount, int lastFrameCount, int offsetX, int offsetY, int width, int height) {
        frames.clear();
        
        for (int i = firstFrameCount; i <= lastFrameCount; i++) {
        	TextureRegion textureRegion = new TextureRegion(texture, i * width + offsetX, offsetY, width, height);
//        	textureRegion.flip(true, false);
            frames.add(textureRegion);
        }
        
        return new Animation<>(frameDuration, frames);
    }
    
}
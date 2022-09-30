package com.killmonster;

import com.killmonster.screens.Screens;
import com.killmonster.util.Font;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface GameStateManager {

    public void showScreen(Screens s);
    public void clearScreen();
    
    public SpriteBatch getBatch();
    public AssetManager getAssets();
    public Font getFont();
    
}

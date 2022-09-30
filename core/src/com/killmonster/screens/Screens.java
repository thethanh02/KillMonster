package com.killmonster.screens;

import com.killmonster.KillMonster;

public enum Screens {
    
    MAIN_MENU {
        public AbstractScreen newScreen(KillMonster gameStateManager, Object... params) {
            return new MainMenuScreen(gameStateManager);
        }
    },
    GAME {
        public AbstractScreen newScreen(KillMonster gameStateManager, Object... params) {
            return new MainGameScreen(gameStateManager);
        }
    },
    GAME_OVER {
        public AbstractScreen newScreen(KillMonster gameStateManager, Object... params) {
            return new GameOverScreen(gameStateManager);
        }
    };
 
    public abstract AbstractScreen newScreen(KillMonster gameStateManager, Object... params);
}
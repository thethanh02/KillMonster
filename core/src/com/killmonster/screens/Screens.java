package com.killmonster.screens;

import com.killmonster.KillMonster;

public enum Screens {
    
	MAIN_MENU {
		public AbstractScreen newScreen(KillMonster gameStateManager) {
			return new MainMenuScreen(gameStateManager);
		}
	},
	TUTORIAL {
		public AbstractScreen newScreen(KillMonster gameStateManager) {
			return new TutorialScreen(gameStateManager);
		}
	},
	GAME {
		public AbstractScreen newScreen(KillMonster gameStateManager) {
			return new MainGameScreen(gameStateManager);
		}
	},
	LEVEL_COMPLETED {
		public AbstractScreen newScreen(KillMonster gameStateManager) {
			return new LevelCompletedScreen(gameStateManager);
		}
	},
	GAME_OVER {
		public AbstractScreen newScreen(KillMonster gameStateManager) {
			return new GameOverScreen(gameStateManager);
		}
	},
	GAME_COMPLETED {
		public AbstractScreen newScreen(KillMonster gameStateManager) {
			return new GameCompletedScreen(gameStateManager);
		}
	};
 
	public abstract AbstractScreen newScreen(KillMonster gameStateManager);
}
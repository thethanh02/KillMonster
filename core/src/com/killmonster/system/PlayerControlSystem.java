package com.killmonster.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.killmonster.character.Player;
import com.killmonster.component.Mappers;
import com.killmonster.component.PlayerComponent;
import com.killmonster.component.StateComponent;
import com.killmonster.util.Constants;

public class PlayerControlSystem extends IteratingSystem {

	private final PooledEngine engine;
	
	public PlayerControlSystem(PooledEngine engine) {
		super(Family.all(PlayerComponent.class).get());
		this.engine = engine;
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		handleInput((Player) entity, Mappers.STATE.get(entity));
	}

	private void handleInput(Player player, StateComponent state) {
		if (state.isSetToKill()) {
			return;
		}
        
//		if(isAllEnemiesKilled()) {
//			Constants.COMPLETED = true;
//			levelCompletedOverlay.handleInput();
//			return;
//		}
		
		if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
			Constants.PAUSE = !Constants.PAUSE;
		}   
//		if (Constants.PAUSE) {
//			pauseOverlay.handleInput();
//			return;
//		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) {
			Constants.DEBUG = !Constants.DEBUG;
		}
		
		
		if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
			player.swingWeapon();
		}
		
		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
			player.jump();
		} else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			player.moveRight();
		} else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			player.moveLeft();
		}
	}
}

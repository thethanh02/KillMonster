package com.killmonster.ui;

import com.killmonster.entity.*;
import com.killmonster.GameStateManager;
import com.killmonster.util.Constants;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class DamageIndicator extends Stage {

	private GameStateManager gsm;
	private Camera gameScreenCamera;
	private float damageTextLifetime;
	
	public DamageIndicator(GameStateManager gsm, Camera gameScreenCamera, float damageTextLifetime) {
		super(new FitViewport(Constants.V_WIDTH, Constants.V_HEIGHT), gsm.getBatch());
		
		this.gsm = gsm;
		this.gameScreenCamera = gameScreenCamera;
		this.damageTextLifetime = damageTextLifetime;
	}


	/**
	 * Shows damage indicator above the specified character's head.
	 * @param c target character.
	 * @param damage amount of damage.
	 */
	public void show(Entity c, int damage) {
		// Move previous text indicator up.
		for (Actor i : c.getDamageIndicators()) {
			i.addAction(Actions.moveBy(0, 10f, .2f));
		}
		
		// Display the new message.
		// Rename Message later! It can be reused for displaying on-screens texts, so
		// the name should be more generic.
		Message indicator = new Message(Integer.toString(damage), new Label.LabelStyle(gsm.getFont().getDefaultFont(), Color.RED), damageTextLifetime);
		
		// Convert the coordinate from world to screens.
		Vector3 worldCoordinates = new Vector3(c.getBody().getPosition().x, c.getBody().getPosition().y, 0);
		gameScreenCamera.project(worldCoordinates);
		
		indicator.setPosition(worldCoordinates.x * Constants.V_WIDTH / getViewport().getScreenWidth(), worldCoordinates.y * Constants.V_HEIGHT / getViewport().getScreenHeight());
		indicator.addAction(Actions.moveBy(0, 10f, .2f));
		c.getDamageIndicators().addLast(indicator);
		addActor(indicator);
	}


	public void update(float delta) {

		// Remove the actor from character's queue....
		// do this tomorrow i'm going to bed right now...
		for (Actor i : getActors()) {
			Message indicator = ((Message) i);
			indicator.update(delta);
			
			if (indicator.hasExpired()) {
			    indicator.addAction(Actions.sequence(Actions.fadeOut(.3f), Actions.removeActor()));
			}
		}

		act(delta);
	}
}

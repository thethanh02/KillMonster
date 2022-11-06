package com.killmonster.character;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import com.killmonster.component.CharacterAIComponent;

public abstract class Enemy extends Character {

	public Enemy(Texture texture, World world, float x, float y) {
		super(texture, world, x, y);
		add(new CharacterAIComponent());
	}

	@Override
	public void inflictDamage(Character c, int damage) {
		super.inflictDamage(c, damage);
		setInRangeTarget(null);
	}
    
	@Override
	public void receiveDamage(int damage) {
		super.receiveDamage(damage);
		state.isAlerted = true;
	}

}
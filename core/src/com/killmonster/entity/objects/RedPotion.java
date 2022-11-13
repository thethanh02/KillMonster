package com.killmonster.entity.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.physics.box2d.World;

public class RedPotion extends Potion {
	
	private final static String TEXTURE_FILE = "objects/red_potion.png";

	public RedPotion(AssetManager assets, World world, float x, float y) {
		super(assets.get(TEXTURE_FILE), world, x, y);
		
		name = "Red Potion";
		healthRegen = 15;
		
		bodyWidth = 9f;
		bodyHeight = 14f;
		offsetX = .065f;
		offsetY = .07f;
		super.createBodyandFixturePotion();
	}

}

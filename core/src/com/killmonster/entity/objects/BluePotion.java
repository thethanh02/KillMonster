package com.killmonster.entity.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.physics.box2d.World;

public class BluePotion extends Potion {
	
	private final static String TEXTURE_FILE = "objects/blue_potion.png";
	
	public BluePotion(AssetManager assets, World world, float x, float y) {
		super(assets.get(TEXTURE_FILE), world, x, y);

		name = "Blue Potion";
		healthRegen = 10;
		
		bodyWidth = 8f;
		bodyHeight = 14f;
		offsetX = .065f;
		offsetY = .067f;
		super.createBodyandFixturePotion();
	}
	
}

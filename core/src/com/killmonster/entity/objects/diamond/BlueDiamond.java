package com.killmonster.entity.objects.diamond;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.physics.box2d.World;

public class BlueDiamond extends Treasure {

	private final static String TEXTURE_FILE = "objects/treasure/blue_diamond.png";
	
	public BlueDiamond(AssetManager assets, World currentWorld, float x, float y) {
		super(assets.get(TEXTURE_FILE), currentWorld, x, y);
		
		name = "Blue Diamond";
		
		bodyWidth = 14f;
		bodyHeight = 12f;
		offsetX = .32f;
		offsetY = .32f;
		scorePoint = 150;
		
		super.createBodyandFixturePotion();
	}

}

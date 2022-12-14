package com.killmonster.entity.objects.diamond;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.physics.box2d.World;

public class GreenDiamond extends Treasure {

	private final static String TEXTURE_FILE = "objects/treasure/green_diamond.png";
	
	public GreenDiamond(AssetManager assets, World currentWorld, float x, float y) {
		super(assets.get(TEXTURE_FILE), currentWorld, x, y);
		
		name = "Green Diamond";
		
		bodyWidth = 12f;
		bodyHeight = 12f;
		offsetX = .32f;
		offsetY = .32f;
		scorePoint = 200;
		
		super.createBodyandFixturePotion();
	}
}

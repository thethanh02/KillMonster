package com.killmonster.entity.objects.diamond;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.physics.box2d.World;

public class RedDiamond extends Diamond {
	
	private final static String TEXTURE_FILE = "objects/diamond/green_diamond.png";
	
	public RedDiamond(AssetManager assets, World currentWorld, float x, float y) {
		super(assets.get(TEXTURE_FILE), currentWorld, x, y);
		
		name = "Red Diamond";
		
		bodyWidth = 12f;
		bodyHeight = 12f;
		offsetX = .32f;
		offsetY = .32f;
		scorePoint = 15;
		
		super.createBodyandFixturePotion();
	}
}

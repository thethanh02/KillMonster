package com.killmonster.entity.objects.diamond;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.physics.box2d.World;

public class SilverCoin extends Treasure {

	private final static String TEXTURE_FILE = "objects/treasure/silver_coin.png";
	
	public SilverCoin(AssetManager assets, World currentWorld, float x, float y) {
		super(assets.get(TEXTURE_FILE), currentWorld, x, y);
		
		name = "Silver Coin";
		
		bodyWidth = 11f;
		bodyHeight = 11f;
		offsetX = .32f;
		offsetY = .32f;
		scorePoint = 20;
		
		super.createBodyandFixturePotion();
	}

}

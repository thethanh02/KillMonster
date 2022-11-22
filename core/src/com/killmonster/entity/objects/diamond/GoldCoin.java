package com.killmonster.entity.objects.diamond;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.physics.box2d.World;

public class GoldCoin extends Treasure {

	private final static String TEXTURE_FILE = "objects/treasure/gold_coin.png";
	
	public GoldCoin(AssetManager assets, World currentWorld, float x, float y) {
		super(assets.get(TEXTURE_FILE), currentWorld, x, y);
		
		name = "Gold Coin";
		
		bodyWidth = 11f;
		bodyHeight = 11f;
		offsetX = .32f;
		offsetY = .32f;
		scorePoint = 40;
		
		super.createBodyandFixturePotion();
	}

}

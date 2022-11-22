package com.killmonster.entity.objects.container;

import com.killmonster.GameWorldManager;
import com.killmonster.util.Constants;

public class Barrel extends Container {

	private static final String TEXTURE_FILE = "objects/containers/barrel.png";
	
	public Barrel(GameWorldManager gameWorldManager, float x, float y) {
		super(gameWorldManager.getAssets().get(TEXTURE_FILE), gameWorldManager.getWorld(), x, y);
		
		name = "Barrel";
		bodyWidth = 23f;
		bodyHeight = 25f;
		offsetX = .195f;
		offsetY = .135f;
		
		health = 1;
		// Create body and fixtures.
		defineBody();
		
		setBounds(0, 0, 40 / Constants.PPM, 30 / Constants.PPM);
		setRegion(animation.get(State.IDLE).getKeyFrame(stateTimer, true));
	}
	
}


package com.killmonster.entity.objects.container;

import com.killmonster.GameWorldManager;
import com.killmonster.util.Constants;

public class Box extends Container {

	private static final String TEXTURE_FILE = "objects/containers/box.png";
	
	public Box(GameWorldManager gameWorldManager, float x, float y) {
		super(gameWorldManager.getAssets().get(TEXTURE_FILE), gameWorldManager.getWorld(), x, y);
		
		name = "Box";
		bodyWidth = 25f;
		bodyHeight = 18f;
		offsetX = .195f;
		offsetY = .1f;
		
		health = 1;
		// Create body and fixtures.
		defineBody();
		setBounds(0, 0, 40 / Constants.PPM, 30 / Constants.PPM);
		setRegion(animation.get(State.IDLE).getKeyFrame(stateTimer, true));
	}
	
}

package com.killmonster.entity.objects;

import java.util.HashMap;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.killmonster.GameWorldManager;
import com.killmonster.util.Constants;
import com.killmonster.util.Utils;

public class TreeOne extends GameObject {
	
	private static final String TEXTURE_FILE = "objects/tree_one_atlas.png";

	public TreeOne(GameWorldManager gameWorldManager, float x, float y, boolean flip) {
		super(gameWorldManager.getAssets().get(TEXTURE_FILE), gameWorldManager.getWorld(), x, y);
		
		name = "Tree1";
		bodyWidth = 39f;
		bodyHeight = 92f;
		offsetX = .195f;
		offsetY = .46f;
		
		// Create animations by extracting frames from the spritesheet.
		animation = new HashMap<>();
		animation.put(State.IDLE, 	 	Utils.createAnimation(getTexture(), 16f / Constants.PPM, 0, 3, 0, 0, 39, 92));
		animation.put(State.HIT, 		Utils.createAnimation(getTexture(), 0f / Constants.PPM, 1, 1, 0, 0, 32, 32));
		animation.put(State.DESTROYED, 	Utils.createAnimation(getTexture(), 0f / Constants.PPM, 1, 1, 0, 0, 32, 32));
		
		super.defineBody(BodyType.StaticBody);
		
		
		setBounds(0, 0, 32 / Constants.PPM, 92 / Constants.PPM);
		textureRegion = animation.get(State.IDLE).getKeyFrame(stateTimer, true);
		setRegion(textureRegion);
		textureRegion.flip(flip, false);
	}

}

package com.killmonster.entity.objects;

import java.util.HashMap;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.killmonster.GameWorldManager;
import com.killmonster.util.Constants;
import com.killmonster.util.Utils;

public class TreeTwo extends GameObject {

	private static final String TEXTURE_FILE = "objects/tree/tree_two_atlas.png";

	public TreeTwo(GameWorldManager gameWorldManager, float x, float y, boolean facingRight) {
		super(gameWorldManager.getAssets().get(TEXTURE_FILE), gameWorldManager.getWorld(), x, y);
		
		name = "Tree2";
		bodyWidth = 62f;
		bodyHeight = 54f;
		offsetX = .31f;
		offsetY = .27f;
		this.facingRight = facingRight;
		
		// Create animations by extracting frames from the spritesheet.
		animation = new HashMap<>();
		animation.put(State.IDLE, 	 	Utils.createAnimation(getTexture(), 16f / Constants.PPM, 0, 3, 0, 62, 54));
		animation.put(State.HIT, 		Utils.createAnimation(getTexture(), 0f / Constants.PPM, 1, 1, 0, 62, 54));
		animation.put(State.DESTROYED, 	Utils.createAnimation(getTexture(), 0f / Constants.PPM, 1, 1, 0, 62, 54));
		
		super.defineBody(BodyType.StaticBody);
		
		setBounds(0, 0, 62 / Constants.PPM, 54 / Constants.PPM);
		textureRegion = animation.get(State.IDLE).getKeyFrame(stateTimer, true);
		setRegion(textureRegion);
	}

}

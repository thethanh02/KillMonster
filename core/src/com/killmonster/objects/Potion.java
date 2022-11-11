package com.killmonster.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.killmonster.GameWorldManager;
import com.killmonster.character.Player;
import com.killmonster.util.CategoryBits;
import com.killmonster.util.Constants;
import com.killmonster.util.Utils;
import com.killmonster.util.box2d.BodyBuilder;

public class Potion extends Sprite implements Disposable {
	private final static String TEXTURE_FILE = "objects/blue_potion.png";
	
	private Animation<TextureRegion> animation;
	
	private GameWorldManager gameWorldManager;
	
	private World currentWorld;
	private BodyBuilder bodyBuilder;
	private Body b2body;
	private Fixture bodyFixture;
	private float stateTimer;
	
	private float bodyWidth;
	private float bodyHeight;
	private float offsetX;
	private float offsetY;
	
	private boolean isPickedUp;
	private boolean isDestroyed;
	private int healthRegen;
	
	public Potion(AssetManager assets, World currentWorld, float x, float y) {
		super((Texture) assets.get(TEXTURE_FILE));
		this.currentWorld = currentWorld;
		setPosition(x, y);
		
		bodyWidth = 8f;
		bodyHeight = 14f;
		offsetX = .065f;
		offsetY = .067f;
		
		isPickedUp = false;
		isDestroyed = false;
		healthRegen = 10;
		
		bodyBuilder = new BodyBuilder(currentWorld);
		
		// Create animations by extracting frames from the spritesheet.
		animation = Utils.createAnimation(getTexture(), 14f / Constants.PPM, 0, 6,  0, 0, 12, 16);
		
		// Create body and fixtures.
		b2body = bodyBuilder
				.type(BodyDef.BodyType.StaticBody)
				.position(getX(), getY(), Constants.PPM)
				.buildBody();
		
		bodyFixture = bodyBuilder
				.newRectangleFixture(b2body.getPosition(), bodyWidth / 2, bodyHeight / 2, Constants.PPM)
				.categoryBits(CategoryBits.OBJECT)
				.maskBits((short)(CategoryBits.GROUND | CategoryBits.PLAYER))
				.setUserData(this)
				.buildFixture();
		
		bodyFixture.setSensor(true);

		setBounds(0, 0, 12 / Constants.PPM, 16 / Constants.PPM);
		setRegion(animation.getKeyFrame(stateTimer, true));
	}
	
	public void update(float delta) {
		if (isPickedUp) {
			if (!isDestroyed) {
				currentWorld.destroyBody(b2body);
				isDestroyed = true;
			}
		} else {
			stateTimer += delta;
			setRegion(animation.getKeyFrame(stateTimer, true));
			float textureX = b2body.getPosition().x - offsetX;
			float textureY = b2body.getPosition().y - offsetY;
			setPosition(textureX, textureY);
		}
	}
	
	public void healing(Player c) {
		c.healed(healthRegen);
	}
	
	public boolean isPickedUp() {
		return isPickedUp;
	}
	
	public void setIsPickedUp(boolean pickedUp) {
		isPickedUp = pickedUp;
	}
	
	@Override
	public void dispose() {
		
	}
}

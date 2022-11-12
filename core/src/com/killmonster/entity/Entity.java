package com.killmonster.entity;

import com.killmonster.util.Constants;
import com.killmonster.util.CategoryBits;
import com.killmonster.util.box2d.BodyBuilder;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Queue;

public abstract class Entity extends Sprite implements Disposable {

	protected World currentWorld;
	protected BodyBuilder bodyBuilder;
	protected Body body;
	protected Fixture bodyFixture;
	
	protected float stateTimer;
	protected boolean facingRight;
	protected boolean isInvincible;
	protected boolean isDestroyed;
	protected boolean setToDestroy;
	protected boolean isHitted;
	
	protected String name;
	protected int health;
	
	protected float bodyHeight;
	protected float bodyWidth;
	protected float offsetX;
	protected float offsetY;
	
	protected Entity lockedOnTarget;
	protected Entity inRangeTarget;
	
	protected TextureRegion textureRegion;
	protected Queue<Actor> damageIndicators; // not removing expired ones yet.

	public Entity(Texture texture, World currentWorld, float x, float y) {
		super(texture);
		this.currentWorld = currentWorld;
		setPosition(x, y);
		
		bodyBuilder = new BodyBuilder(currentWorld);
		damageIndicators = new Queue<>();

	}
    
	public void update(float delta) {
	}
    
	protected void defineBody(BodyDef.BodyType type, short bodyCategoryBits, short bodyMaskBits, short feetMaskBits, short meleeWeaponMaskBits) {
		body = bodyBuilder.type(type)
				.position(getX(), getY(), Constants.PPM)
				.buildBody();

		createBodyFixture(bodyCategoryBits, bodyMaskBits);
	}

	protected void createBodyFixture(short categoryBits, short maskBits) {
		bodyFixture = bodyBuilder
				.newRectangleFixture(body.getPosition(), bodyWidth / 2, bodyHeight / 2, Constants.PPM)
				.categoryBits(categoryBits)
				.maskBits(maskBits)
				.setUserData(this)
				.buildFixture();
	}

	public void receiveDamage(int damage) {
		if (!isInvincible) {
			health -= damage;

			if (health <= 0) {
				setCategoryBits(bodyFixture, CategoryBits.DESTROYED);
				setToDestroy = true;
			} else {
				isHitted = true;
			}
		}
	}

	public void knockedBack(float force) {
		body.applyLinearImpulse(new Vector2(force, 1f), body.getWorldCenter(), true);
	}

	public static void setCategoryBits(Fixture f, short bits) {
		Filter filter = new Filter();
		filter.categoryBits = bits;
		f.setFilterData(filter);
	}

	public Body getBody() {
		return body;
	}
	
	public int getHealth() {
		return health;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isSetToKill() {
		return setToDestroy;
	}
    
	public boolean isKilled() {
		return isDestroyed;
	}
	
	public boolean isHitted() {
		return isHitted;
	}
	
	public boolean isInvincible() {
		return isInvincible;
	}
	
	public boolean isFacingRight() {
		return facingRight;
	}
	
	public boolean hasLockedOnTarget() {
		return lockedOnTarget != null;
	}
    
	public boolean hasInRangeTarget() {
		return inRangeTarget != null;
	}
	
	public void setLockedOnTarget(Entity lockedOnTarget) {
		this.lockedOnTarget = lockedOnTarget;
	}
    
	public void setInRangeTarget(Entity enemy) {
		inRangeTarget = enemy;
	}
	
	public void flipXTextureRegion() {
		textureRegion.flip(true, false);
	}
	
	public Queue<Actor> getDamageIndicators() {
		return damageIndicators;
	}
	
	@Override
	public void dispose() {
	}
	    
}

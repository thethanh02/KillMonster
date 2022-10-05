package com.killmonster.character;

import com.killmonster.GameWorldManager;
import com.killmonster.util.*;

import java.util.HashMap;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class Player extends Character {
    
    private static final String TEXTURE_FILE = "character/player/Player.png";

    private GameWorldManager gameWorldManager;
    
    public Player(GameWorldManager gameWorldManager, float x, float y) {
        super(gameWorldManager.getAssets().get(TEXTURE_FILE), gameWorldManager.getWorld(), x, y);
        this.gameWorldManager = gameWorldManager;

    	name = "Player";
        bodyWidth = 10;
        bodyHeight = 34;
        offsetX = .31f;
        offsetY = .228f;

        health = 100;
        movementSpeed = .5f;
        jumpHeight = 4.5f;
        
        attackForce = 1f;
        attackRange = 15;
        attackDamage = 25;
        
        typeFixtureShape = "CircleShape";
        
        // Create animations by extracting frames from the spritesheet.
        animation = new HashMap<>();
        animation.put(State.IDLE, 	 	Utils.createAnimation(getTexture(), 14f / Constants.PPM, 0, 4,  0, 0 * 40,  64, 40));
        animation.put(State.RUNNING, 	Utils.createAnimation(getTexture(), 9f / Constants.PPM,  0, 5,  0, 1 * 40,  64, 40));
        animation.put(State.JUMPING, 	Utils.createAnimation(getTexture(), 10f / Constants.PPM, 0, 2,  0, 2 * 40,  64, 40));
        animation.put(State.FALLING, 	Utils.createAnimation(getTexture(), 10f / Constants.PPM, 0, 0,  0, 3 * 40,  64, 40));
        animation.put(State.ATTACKING,  Utils.createAnimation(getTexture(), 18f / Constants.PPM, 0, 2,  0, 4 * 40,  64, 40));
        animation.put(State.KILLED, 	Utils.createAnimation(getTexture(), 24f / Constants.PPM, 0, 7,  0, 6 * 40,  64, 40));
        
        attackTime = animation.get(State.ATTACKING).getFrameDuration() * 3;
        // Create body and fixtures.
        defineBody();

        setBounds(0, 0, 64 / Constants.PPM, 40 / Constants.PPM);
        setRegion(animation.get(State.FALLING).getKeyFrame(stateTimer, true));
    }

    public void defineBody() {
        short bodyCategoryBits = CategoryBits.PLAYER;
        short bodyMaskBits = CategoryBits.GROUND | CategoryBits.PLATFORM | CategoryBits.WALL | CategoryBits.ENEMY | CategoryBits.MELEE_WEAPON;
        short feetMaskBits = CategoryBits.GROUND | CategoryBits.PLATFORM;
        short weaponMaskBits = CategoryBits.ENEMY | CategoryBits.OBJECT;

    	b2body = bodyBuilder
    			.type(BodyDef.BodyType.DynamicBody)
                .position(getX(), getY(), Constants.PPM)
                .buildBody();

        createBodyFixture(bodyCategoryBits, bodyMaskBits);
        createFeetFixture(feetMaskBits);
        super.createMeleeWeaponFixture(weaponMaskBits);
    }
    
    public void createBodyFixture(short categoryBits, short maskBits) {
        bodyFixture = bodyBuilder
        		.newRectangleFixture(b2body.getPosition(), 9.5f, 13f, Constants.PPM)
                .categoryBits(categoryBits)
                .maskBits(maskBits)
                .setUserData(this)
                .buildFixture();
    }
    
    public void createFeetFixture(short maskBits) {
        Vector2[] feetPolyVertices = new Vector2[4];
        feetPolyVertices[0] =  new Vector2(-bodyWidth / 2 + 1, -bodyHeight / 2 + 3);
        feetPolyVertices[1] =  new Vector2(bodyWidth / 2 - 1, -bodyHeight / 2 + 3);
        feetPolyVertices[2] =  new Vector2(-bodyWidth / 2 + 1, -bodyHeight / 2 + 2);
        feetPolyVertices[3] =  new Vector2(bodyWidth / 2 - 1, -bodyHeight / 2 + 2);

        feetFixture = bodyBuilder
        		.newPolygonFixture(feetPolyVertices, Constants.PPM)
                .categoryBits(CategoryBits.FEET)
                .maskBits(maskBits)
                .isSensor(true)
                .setUserData(this)
                .buildFixture();
    }

    public void reposition(Vector2 position) {
        b2body.setTransform(position, 0);
    }

    public void reposition(float x, float y) {
        b2body.setTransform(x, y, 0);
    }

    @Override
    public void inflictDamage(Character c, int damage) {
        if ((this.facingRight && c.facingRight()) || (!this.facingRight && !c.facingRight())) {
            damage *= 2;
            gameWorldManager.getMessageArea().show("Critical hit!");
        }

        super.inflictDamage(c, damage);
        gameWorldManager.getDamageIndicator().show(c, damage);
        gameWorldManager.getMessageArea().show(String.format("You dealt %d pts damage to %s", damage, c.getName()));
        CameraShake.shake(8 / Constants.PPM, .1f);

        if (c.isSetToKill()) {
            gameWorldManager.getMessageArea().show(String.format("You earned 10 exp."));
        }
    }
    
    @Override
    public void receiveDamage(int damage) {
        super.receiveDamage(damage);

        // Sets the player to be untouchable for a while.
        if (!isInvincible) {
            CameraShake.shake(8 / Constants.PPM, .1f);
            isInvincible = true;

            Timer.schedule(new Task() {
                @Override
                public void run() {
                    if (!setToKill) {
                        isInvincible = false;
                    }
                }
            }, 3f);
        }
    }

}
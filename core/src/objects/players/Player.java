package objects.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import helper.Constants;

import static helper.Constants.*;

import java.util.HashMap;

public class Player extends Entity {
	
	private float flipX = 0, flipW = 1;
	private boolean loop = true;
	
	private float stateTime;
	private boolean isAttacking;
	
//	private TextureAtlas textureAtlas;
	private Character.State currentState;
	private Character.State previousState;
	private HashMap<Character.State, Animation<TextureRegion>> animation;
	TextureRegion currentFrame;
	
	public Player(float width, float height, Body body) {
		super(width / PPM, height / PPM, body);
		
		body.getFixtureList().get(0).setUserData(this);
		
		speed = 3.5f;
		flipX = 0;
		animation = new HashMap<>();
		
		stateTime = 0;
		isAttacking = false;
		
		currentState = Character.State.IDLE;
		previousState = Character.State.IDLE;
//		textureAtlas = new TextureAtlas(Gdx.files.internal("maps/unnamed.atlas"));
//		animation = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("tile")); 
//		animation.setFrameDuration(FRAME_TIME);
		
		animation.put(Character.State.IDLE, loadAnimation("res/player_sprites.png", 64, 40, 0, 5, 5f/Constants.PPM));
		animation.put(Character.State.RUNNING, loadAnimation("res/player_sprites.png", 64, 40, 1, 6, 6f/Constants.PPM));
		animation.put(Character.State.ATTACKING, loadAnimation("res/player_sprites.png", 64, 40, 4, 3, 6f/Constants.PPM));
		animation.put(Character.State.JUMPING, loadAnimation("res/player_sprites.png", 64, 40, 2, 3, 15f/Constants.PPM));
		animation.put(Character.State.JUMP_FALLING, loadAnimation("res/player_sprites.png", 64, 40, 4, 1, 32f/Constants.PPM));
		updateAttackBox(1f);
	}
	
	
	@Override
	public void update() {
		x = body.getPosition().x * PPM;
		y = body.getPosition().y * PPM;
		
		checkUserInput();
		updateFrame();
	}
	
	@Override 
	public void render(SpriteBatch batch) {
		batch.draw(
				currentFrame, 
				this.x - 21.5f * 1.5f + flipX * currentFrame.getRegionWidth(), 
				this.y - 14 * 1.5f,
				currentFrame.getRegionWidth() * flipW, 
				currentFrame.getRegionHeight());
		
	}
	
	public void checkUserInput() {
		if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && !isAttacking) {
			isAttacking = true;
			loop = false;
			return;
		}
		
		velX = 0;
		if (!isAttacking) {
			if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !isJumping()) {
                loop = false;
                float force = body.getMass() * 12;
    			body.setLinearVelocity(body.getLinearVelocity().x, 0);
    			body.applyLinearImpulse(new Vector2(0, force), body.getPosition(), true);
            } else if ((Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))) {
            	loop = true;
            	velX = 1;
    			flipX = 0;
    			flipW = 1;
    			updateAttackBox(1f);
            } else if ((Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))) {
                loop = true;
                velX = -1;
    			flipX = 1;
    			flipW = -1;
    			updateAttackBox(-1f);
            }
//            } else isMoving = false;
		}
		body.setLinearVelocity(velX * speed, body.getLinearVelocity().y < 8.5f ? body.getLinearVelocity().y : 8.5f);
		
		if (isAttacking && stateTime >= animation.get(Character.State.ATTACKING).getAnimationDuration()) {
			isAttacking = false;
		}
	}
	
	public void updateFrame() {
		currentState = getState();
		currentFrame = animation.get(currentState).getKeyFrame(stateTime, loop);
		stateTime = (currentState == previousState) ? stateTime + Gdx.graphics.getDeltaTime() : 0;
        previousState = currentState;
	}
	
	public Character.State getState() {
		if (isAttacking) {
            return Character.State.ATTACKING;
        } else if (body.getLinearVelocity().y > 0) {
            return Character.State.JUMPING;
        } else if (body.getLinearVelocity().y < 0) {
            return Character.State.JUMP_FALLING;
        } else if (body.getLinearVelocity().x != 0) {
            return Character.State.RUNNING;
        } else {
            return Character.State.IDLE;
        }
	}
	
	private void updateAttackBox(float changeDir) {
		if (body.getFixtureList().size > 1) {
			body.destroyFixture(body.getFixtureList().get(1));
		}
		PolygonShape polygonShape = new PolygonShape();
		polygonShape.setAsBox(
				10 / PPM, 
				4.8f / PPM, 
				new Vector2(0.48f * changeDir, - 0.13f), 
				0);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = polygonShape;	
		body.createFixture(fixtureDef);
		body.getFixtureList().get(1).setUserData("attackBox");
		body.getFixtureList().get(1).setSensor(true);
	}
	
	public Animation<TextureRegion> loadAnimation(String name, int width, int heigh, int col, int amount, float frameTime) {
		Texture texture = new Texture(name);
		TextureRegion[][] tmpFrames = TextureRegion.split(texture, width, heigh);
		TextureRegion[] animationFrames = new TextureRegion[amount];
		int index = 0;
		for (int i = 0; i < amount; i++) 
			animationFrames[index++] = tmpFrames[col][i];
		
		return new Animation<>(frameTime, animationFrames);
	}
	
	public boolean isJumping() {
		return body.getLinearVelocity().y != 0;
	}
}

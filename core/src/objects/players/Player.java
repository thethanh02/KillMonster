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
	
	private int jumpCounter;
	private float flipX = 0, flipW = 1;
	private float elapseTime = 0f;
	
//	private TextureAtlas textureAtlas;
	
	private HashMap<String, Animation<TextureRegion>> animation;
	private String animationState;
	
	private float xStart, yStart;
	
	public Player(float width, float height, Body body) {
		super(width / PPM, height / PPM, body);
		xStart = body.getPosition().x / PPM;
		yStart = body.getPosition().y / PPM;
		this.speed = 4f;
		flipX = 0;
		animation = new HashMap<>();
		animationState = "idleAni";
//		this.jumpCounter = 0;
		
//		textureAtlas = new TextureAtlas(Gdx.files.internal("maps/unnamed.atlas"));
//		animation = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("tile")); 
//		animation.setFrameDuration(FRAME_TIME);
		
		animation.put("idleAni", loadAnimation("res/player_sprites.png", 64, 40, 0, 5, 1/8f));
		animation.put("runAni", loadAnimation("res/player_sprites.png", 64, 40, 1, 6, 1/8f));
		animation.put("attackAni", loadAnimation("res/player_sprites.png", 64, 40, 4, 3, 1/8f));
		animation.put("jumpAni", loadAnimation("res/player_sprites.png", 64, 40, 2, 3, 1/3f));
		updateAttackBox(1f);
	}
	
	
	@Override
	public void update() {
		x = body.getPosition().x * PPM;
		y = body.getPosition().y * PPM;
		
		checkUserInput();
	}
	
	@Override 
	public void render(SpriteBatch batch) {
		elapseTime += Gdx.graphics.getDeltaTime();
		TextureRegion currentFrame = animation.get(animationState).getKeyFrame(elapseTime, true);
		batch.draw(
				currentFrame, 
				this.x - 21.5f * 1.5f + flipX * currentFrame.getRegionWidth(), 
				this.y - 14 * 1.5f,
				currentFrame.getRegionWidth() * flipW, 
				currentFrame.getRegionHeight());
	}
	
	public void checkUserInput() {
		animationState = "idleAni";
		
		velX = 0;
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			animationState = "runAni";
			velX = 1;
			flipX = 0;
			flipW = 1;
			updateAttackBox(1f);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			animationState = "runAni";
			velX = -1;
			flipX = 1;
			flipW = -1;
			updateAttackBox(-1f);
		}
		
		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && jumpCounter < 2) {
			animationState = "jumpAni";
			jumpCounter++;
			float force = body.getMass() * 12;
			body.setLinearVelocity(body.getLinearVelocity().x, 0);
			body.applyLinearImpulse(new Vector2(0, force), body.getPosition(), true);
		}
		
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
			animationState = "attackAni";
		}
		
		if (body.getLinearVelocity().y == 0) {
			jumpCounter = 0;
		} else {
			animationState = "jumpAni";
		}
		
		body.setLinearVelocity(velX * speed, body.getLinearVelocity().y < 10 ? body.getLinearVelocity().y : 10);
	}
	
	private void updateAttackBox(float changeDir) {
		if (body.getFixtureList().size > 1) {
			body.destroyFixture(body.getFixtureList().get(1));
		}
		PolygonShape polygonShape = new PolygonShape();
		polygonShape.setAsBox(
				10 / PPM, 
				4.8f / PPM, 
				new Vector2((xStart / PPM + 0.48f * changeDir), (yStart / PPM - 0.13f)), 
				0);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = polygonShape;	
		body.createFixture(fixtureDef);
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
	
}

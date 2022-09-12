package objects.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.bullet.linearmath.int4;

import static helper.Constants.*;

import java.util.HashMap;

public class Player extends Entity {
	
	private static final float FRAME_TIME = 1/15f;
	
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
		this.speed = 10f;
		flipX = 1;
		animation = new HashMap<>();
		animationState = "idleAni";
//		this.jumpCounter = 0;
		
//		textureAtlas = new TextureAtlas(Gdx.files.internal("maps/unnamed.atlas"));
//		animation = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("tile")); 
//		animation.setFrameDuration(FRAME_TIME);
		
		animation.put("idleAni", loadAnimation("Idle-Sheet.png", 64, 80));
		animation.put("runAni", loadAnimation("Run-Sheet.png", 80, 80));
		animation.put("attackAni", loadAnimation("attack-01-sheet.png", 96, 80));
		animation.put("jumpAni", loadAnimation("Jump-All-Sheet.png", 64, 64));
		updateAttackBox(0.7f);
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
		batch.draw(currentFrame, this.x - this.width / 3 - 30 + flipX * currentFrame.getRegionWidth(), this.y - this.height / 1.5f - 40, currentFrame.getRegionWidth() * flipW, currentFrame.getRegionHeight());
	}
	
	public void checkUserInput() {
		animationState = "idleAni";
		
		velX = 0;
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			animationState = "runAni";
			velX = 1;
			flipX = 0;
			flipW = 1;
			updateAttackBox(0.7f);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			animationState = "runAni";
			velX = -1;
			flipX = 1;
			flipW = -1;
			updateAttackBox(-0.7f);
		}
		
		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && jumpCounter < 2) {
			animationState = "jumpAni";
			jumpCounter++;
			float force = body.getMass() * 25;
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
		
//		body.setLinearVelocity(velX * speed, velY * speed);
		body.setLinearVelocity(velX * speed, body.getLinearVelocity().y < 12 ? body.getLinearVelocity().y : 12);
	}
	
	private void updateAttackBox(float changeDir) {
		if (body.getFixtureList().size > 1) {
			body.destroyFixture(body.getFixtureList().get(1));
		}
		PolygonShape polygonShape = new PolygonShape();
		polygonShape.setAsBox(
				this.width / 5, 
				this.height / 2.5f, 
				new Vector2((xStart / PPM + changeDir), (yStart / PPM)), 
				0);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = polygonShape;
		body.createFixture(fixtureDef);
//		body.getFixtureList().get(1).
		body.getFixtureList().get(1).setSensor(true);
	}
	
	public Animation<TextureRegion> loadAnimation(String name, int width, int heigh) {
		Texture texture = new Texture(name);
		TextureRegion[][] tmpFrames = TextureRegion.split(texture, width, heigh);
		TextureRegion[] animationFrames = new TextureRegion[texture.getWidth()/width];
		int index = 0;
		for (int i = 0; i < texture.getWidth()/width; i++) 
			animationFrames[index++] = tmpFrames[0][i];
		
		return new Animation<>(FRAME_TIME, animationFrames);
	}
	
}

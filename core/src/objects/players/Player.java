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
import com.badlogic.gdx.physics.bullet.linearmath.int4;

import static helper.Constants.*;

public class Player extends Entity {
	
	private static final float FRAME_TIME = 1/10f;
	
	private int jumpCounter;
	
	private TextureRegion[] animationFrames;
	private Animation<TextureRegion> animation;
	private float elapseTime = 0f;
	
	private TextureAtlas textureAtlas;
	
	public Player(float width, float height, Body body) {
		super(width, height, body);
		this.speed = 15f;
		this.jumpCounter = 0;
		
//		textureAtlas = new TextureAtlas(Gdx.files.internal("maps/unnamed.atlas"));
//		animation = new Animation<TextureRegion>(FRAME_TIME, textureAtlas.findRegions("tile")); 
//		animation.setFrameDuration(FRAME_TIME);
		
		Texture texture = new Texture("Run-Sheet.png");
		TextureRegion[][] tmpFrames = TextureRegion.split(texture, 80, 80);
		animationFrames = new TextureRegion[640/80];
		int index = 0;
		for (int i = 0; i < 640/80; i++) {
				animationFrames[index++] = tmpFrames[0][i];
		}
		animation = new Animation<>(FRAME_TIME, animationFrames);
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
		TextureRegion currentFrame = animation.getKeyFrame(elapseTime, true);
		batch.draw(currentFrame, this.x - this.width / 2, this.y - this.height / 2, currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
	}
	
	public void checkUserInput() {
		velX = 0;
		if (Gdx.input.isKeyPressed(Input.Keys.D))
			velX = 1;	
		if (Gdx.input.isKeyPressed(Input.Keys.A))
			velX = -1;
		
//		velY = 0;
//		if (Gdx.input.isKeyPressed(Input.Keys.W)) 
//			velY = 1;
//		if (Gdx.input.isKeyPressed(Input.Keys.S)) 
//			velY = -1;
		
		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && jumpCounter < 2) {
			jumpCounter++;
			float force = body.getMass() * 25;
			body.setLinearVelocity(body.getLinearVelocity().x, 0);
			body.applyLinearImpulse(new Vector2(0, force), body.getPosition(), true);
		}
		
		if (body.getLinearVelocity().y == 0) {
			jumpCounter = 0;
		}
		
//		body.setLinearVelocity(velX * speed, velY * speed);
		body.setLinearVelocity(velX * speed, body.getLinearVelocity().y < 25 ? body.getLinearVelocity().y : 25);
	}
}

package objects.players;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;

import static helper.Constants.*;

public class Box extends Entity {
	
	private Texture texture;
	private Sprite sprite;
	
	public Box(float width, float height, Body body) {
		super(width, height, body);
		this.speed = 25f;
		
		texture = new Texture("maps/props.png");
		sprite = new Sprite(texture, 32*5, 0, 32, 64);
	}
	
	@Override
	public void update() {
		x = body.getPosition().x * PPM;
		y = body.getPosition().y * PPM;
		body.setLinearDamping(100);
	}
	
	@Override
	public void render(SpriteBatch batch) {
		sprite.setBounds(x - width / 2, y - height / 2, 32, 64);
		sprite.draw(batch);
	}
}

package helper;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import static helper.Constants.*;

public class BodyHelperService {
	public static Body createBody(float x, float y, float width, float height, boolean isStatic, World world, String name) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = isStatic ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(x / PPM, y / PPM);
		bodyDef.fixedRotation = true;
		Body body = world.createBody(bodyDef);
		
		PolygonShape polygonShape = new PolygonShape();
		polygonShape.setAsBox(width / 2 / PPM, height / 2 / PPM);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = polygonShape;
		fixtureDef.density = 1000;
		fixtureDef.friction = 0;
		body.createFixture(fixtureDef);
		body.getFixtureList().get(0).setUserData(name);
		polygonShape.dispose();
		return body;
	}
}

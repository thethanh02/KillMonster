package helper;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.killmonster.GameScreen;

import objects.players.Enemy;

public class ListenerClass implements ContactListener {
	private GameScreen gameScreen;
	
	public ListenerClass(GameScreen gameScreen) {
		this.gameScreen = gameScreen;
	}
	
	@Override
	public void beginContact(Contact contact) {
		Fixture A = contact.getFixtureA();
		Fixture B = contact.getFixtureB();
		
		if (A.getUserData() instanceof Enemy && (B.getUserData().equals("ground") || B.getUserData().equals("isNotFloor"))) {
			Enemy enemy = (Enemy) A.getUserData();
			enemy.rotateVelX();
		}
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}
	
}

package com.killmonster.util.box2d;

import com.killmonster.util.Constants;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class BodyBuilder {

	private World world;
	private Body body;
	private Fixture currentOperatingFixture;
	
	private BodyDef bdef;
	private FixtureDef fdef;
	private Shape bodyShape;
	private Object userData;
	
	/**
	 * Return an instance of BodyBuilder which lets you create body in the specified world.
	 * @param world the world where the body will be built.
	 */
	public BodyBuilder(World world) {
		this.world = world;
		
		bdef = new BodyDef();
		fdef = new FixtureDef();
	}


	/**
	 * Sets the type of current body.
	 * @param bodyType type of body.
	 * @return BodyBuilder instance.
	 */
	public BodyBuilder type(BodyDef.BodyType bodyType) {
		bdef.type = bodyType;
		return this;
	}

	/**
	 * Sets the position of current body.
	 * @param x x coordinate.
	 * @param y y coordinate.
	 * @param ppm scale factor.
	 * @return BodyBuilder instance.
	 */
	public BodyBuilder position(float x, float y, float ppm) {
		bdef.position.set(x / ppm, y / ppm);
		return this;
	}

	/**
	 * Sets the position of current body.
	 * @param position a Vector2 instance containing x and y coordinate.
	 * @param ppm scale factor.
	 * @return BodyBuilder instance.
	 */
	public BodyBuilder position(Vector2 position, float ppm) {
		bdef.position.set(position.scl(1 / ppm));
		return this;
	}
	
	/**
	 * Creates a body in the world according to current's Body Definition.
	 * @return newly built body.
	 */
	public Body buildBody() {
		body = world.createBody(bdef);
		return body;
	}


	/**
	 * Creates a new rectangle fixture whose position is relative to the body's position.
	 * This will clear the current Fixture Definition.
	 * Always call buildFixture() before creating another new fixture, but if this is the very first
	 * fixture you are creating with this BodyBuilder instance, then you don't need to call it though.
	 * @param centerPos the center position of the rectangle.
	 * @param hx the half-width.
	 * @param hy the half-height.
	 * @param ppm scale factor.
	 * @return BodyBuilder instance.
	 */
	public BodyBuilder newRectangleFixture(Vector2 centerPos, float hx, float hy, float ppm) {
		fdef = new FixtureDef();
		userData = null;
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(hx / Constants.PPM, hy / Constants.PPM);
		bodyShape = shape;
		fdef.shape = shape;
		return this;
	}

	/**
	 * Creates a new Polygon fixture whose position is relative to the body's position.
	 * This will clear the current Fixture Definition.
	 * Always call buildFixture() before creating another new fixture, but if this is the very first
	 * fixture you are creating with this BodyBuilder instance, then you don't need to call it though.
	 * @param vertices the vertices of the polygon in relation to the position of the body.
	 * @param ppm scale factor.
	 * @return BodyBuilder instance.
	 */
	public BodyBuilder newPolygonFixture(Vector2[] vertices, float ppm) {
		fdef = new FixtureDef();
		userData = null;
		
		PolygonShape shape = new PolygonShape();
		for (Vector2 vector : vertices) {
		    vector.scl(1 / ppm);
		}
		shape.set(vertices);
		bodyShape = shape;
		fdef.shape = shape;
		return this;
	}

	/**
	 * Creates a new Polyline fixture. Body's position should be set to zero if using this.
	 * This will clear the current Fixture Definition.
	 * Always call buildFixture() before creating another new fixture, but if this is the very first
	 * fixture you are creating with this BodyBuilder instance, then you don't need to call it though.
	 * @param vertices the vertices of the polyline.
	 * @param ppm scale factor.
	 * @return BodyBuilder instance.
	 */
	public BodyBuilder newPolylineFixture(Vector2[] vertices, float ppm) {
		fdef = new FixtureDef();
		userData = null;
		
		ChainShape shape = new ChainShape();
		for (Vector2 vector : vertices) {
		    vector.scl(1 / ppm);
		}
		shape.createChain(vertices);
		bodyShape = shape;
		fdef.shape = shape;
		return this;
	}

	/**
	 * Creates a new EdgeShape fixture whose position is relative to the body's position.
	 * This will clear the current Fixture Definition.
	 * Always call buildFixture() before creating another new fixture, but if this is the very first
	 * fixture you are creating with this BodyBuilder instance, then you don't need to call it though.
	 * @param vertex1 the first vertex of the body.
	 * @param vertex2 the second vertex of the body.
	 * @param ppm scale factor.
	 * @return BodyBuilder instance.
	 */
	public BodyBuilder newEdgeShapeFixture(Vector2 vertex1, Vector2 vertex2, float ppm) {
		fdef = new FixtureDef();
		userData = null;
		
		EdgeShape shape = new EdgeShape();
		shape.set(vertex1.scl(1 / ppm), vertex2.scl(1 / ppm));
		bodyShape = shape;
		fdef.shape = shape;
		return this;
	}

	/**
	 * Creates a new Circle fixture whose position is relative to the body's position.
	 * This will clear the current Fixture Definition.
	 * Always call buildFixture() before creating another new fixture, but if this is the very first
	 * fixture you are creating with this BodyBuilder instance, then you don't need to call it though.
	 * @param position a Vector2 instance containing x and y coordinate (Center of the circle).
	 * @param radius the radius of the circle.
	 * @param ppm scale factor.
	 * @return BodyBuilder instance.
	 */
	public BodyBuilder newCircleFixture(Vector2 position, float radius, float ppm) {
		fdef = new FixtureDef();
		userData = null;
		
		CircleShape shape = new CircleShape();
		shape.setPosition(position.scl(1 / ppm));
		shape.setRadius(radius / ppm);
		bodyShape = shape;
		fdef.shape = shape;
		return this;
	}

	/**
	 * Sets the category bits.
	 * @param categoryBits category bits.
	 * @return BodyBuilder instance.
	 */
	public BodyBuilder categoryBits(short categoryBits) {
		fdef.filter.categoryBits = categoryBits;
		return this;
	}

	/**
	 * Sets the mask bits (what this fixture can collide with)
	 * @param maskBits mask bits.
	 * @return BodyBuilder instance.
	 */
	public BodyBuilder maskBits(short maskBits) {
		fdef.filter.maskBits = (short) maskBits;
		return this;
	}
	
	/**
	 * Sets current fixture to a sensor. A sensor will not collide with anything.
	 * @return BodyBuilder instance.
	 */
	public BodyBuilder isSensor(boolean isSensor) {
		fdef.isSensor = isSensor;
		return this;
	}
	
	/**
	 * Sets current fixture's friction.
	 * @param friction friction.
	 * @return BodyBuilder instance.
	 */
	public BodyBuilder friction(float friction) {
		fdef.friction = friction;
		return this;
	}

	/**
	 * Sets current fixture's restitution.
	 * @param restitution restitution.
	 * @return BodyBuilder instance.
	 */
	public BodyBuilder restitution(float restitution) {
		fdef.restitution = restitution;
		return this;
	}
	
	/**
	 * Sets current fixture's userData. It will be set upon calling buildFixture().
	 * @param userData user data.
	 * @return BodyBuilder instance.
	 */
	public BodyBuilder setUserData(Object userData) {
		this.userData = userData;
		return this;
	}
	
	/**
	 * Creates the fixture based on current's Fixture Definition.
	 * @return newly created Fixture.
	 */
	public Fixture buildFixture() {
		currentOperatingFixture = body.createFixture(fdef);
		currentOperatingFixture.setUserData(userData);
		bodyShape.dispose();
		return currentOperatingFixture;
	}

}

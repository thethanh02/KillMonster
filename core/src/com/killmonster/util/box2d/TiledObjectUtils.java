package com.killmonster.util.box2d;

import com.killmonster.map.GameMap;
import com.killmonster.map.GameMapLayer;
import com.killmonster.util.CategoryBits;
import com.killmonster.util.Constants;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

public class TiledObjectUtils {

	private static final boolean GROUND_COLLIDABLE = true;
	private static final boolean PLATFORM_COLLIDABLE = true;
	private static final boolean WALL_COLLIDABLE = true;
	private static final boolean CLIFF_MARKER_COLLIDABLE = false;
	
	/**
	 * Parses the layers of the specified TiledMap, and creates the corresponding bodies.
	 * @param world the world where all layer bodies will be built.
	 * @param rayHandler the RayHandler to handle lighting.
	 * @param gameMap the tiled map to parse.
	 */
	public static void parseLayers(World world, GameMap gameMap) {
		createPolylines(world, gameMap, GameMapLayer.GROUND, CategoryBits.GROUND, GROUND_COLLIDABLE, Constants.GROUND_FRICTION);
		createRectangles(world, gameMap, GameMapLayer.PLATFORM, CategoryBits.PLATFORM, PLATFORM_COLLIDABLE, Constants.GROUND_FRICTION);
		createPolylines(world, gameMap, GameMapLayer.WALL, CategoryBits.WALL, WALL_COLLIDABLE, 0);
		createPolylines(world, gameMap, GameMapLayer.CLIFF_MARKER, CategoryBits.CLIFF_MARKER, CLIFF_MARKER_COLLIDABLE, 0);
	}

	private static void createRectangles(World world, GameMap gameMap, GameMapLayer layer,
										short categoryBits, boolean collidable, float friction) {
		BodyBuilder bodyBuilder = new BodyBuilder(world);
		MapObjects mapObjects = gameMap.getTiledMap().getLayers().get(layer.ordinal()).getObjects();
		
		for (RectangleMapObject object : mapObjects.getByType(RectangleMapObject.class)) {
		Rectangle rect = object.getRectangle();
		Vector2 centerPos = new Vector2(rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() / 2);
		
		bodyBuilder.type(BodyDef.BodyType.StaticBody)
				.position(centerPos, Constants.PPM)
				.buildBody();
		
		bodyBuilder.newRectangleFixture(centerPos, rect.getWidth() / 2, rect.getHeight() / 2, Constants.PPM)
				.categoryBits(categoryBits)
				.friction(friction)
				.isSensor(!collidable)
				.buildFixture();
		}
	}

	private static void createPolylines(World world, GameMap gameMap, GameMapLayer layer,
										short categoryBits, boolean collidable, float friction) {
		BodyBuilder bodyBuilder = new BodyBuilder(world);
		MapObjects mapObjects = gameMap.getTiledMap().getLayers().get(layer.ordinal()).getObjects();
		
		for (PolylineMapObject object : mapObjects.getByType(PolylineMapObject.class)) {
			float[] vertices = object.getPolyline().getTransformedVertices();
			Vector2[] worldVertices = new Vector2[vertices.length / 2];
			
			for (int i = 0; i < worldVertices.length; i++) {
				worldVertices[i] = new Vector2(vertices[i * 2], vertices[i * 2 + 1]);
			}
			
			
			bodyBuilder.type(BodyDef.BodyType.StaticBody)
					.position(0, 0, Constants.PPM)
					.buildBody();
			
			bodyBuilder.newPolylineFixture(worldVertices, Constants.PPM)
					.categoryBits(categoryBits)
					.friction(friction)
					.isSensor(!collidable)
					.buildFixture();
		}
	}

}
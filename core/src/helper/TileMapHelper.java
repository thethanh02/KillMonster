package helper;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.killmonster.*;

import objects.players.Enemy;
import objects.players.Player;

import com.badlogic.gdx.math.Rectangle;

import static helper.Constants.*;

public class TileMapHelper {
	private TiledMap tiledMap;
	private GameScreen gameScreen;
	
	public TileMapHelper(GameScreen gameScreen) {
		this.gameScreen = gameScreen;
	}
	
	public OrthogonalTiledMapRenderer setupMap() {
		tiledMap = new TmxMapLoader().load("res/level0.tmx");
		parseMapObjects(tiledMap.getLayers().get("entity").getObjects());
		return new OrthogonalTiledMapRenderer(tiledMap);
	}
	
	public void parseMapObjects(MapObjects mapObjects) {
		for (MapObject mapObject : mapObjects) {
			if (mapObject instanceof PolygonMapObject) {
				String name = mapObject.getName();
				if (name.equals("isNotFloor"))
					createStaticBody((PolygonMapObject) mapObject, true, "isNotFloor");
				else 
					createStaticBody((PolygonMapObject) mapObject, false, "ground");
			}
			
			if (mapObject instanceof RectangleMapObject) {
				Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
				String name = mapObject.getName();
				if (name.equals("player")) {
					Body body = BodyHelperService.createBody(
							rectangle.getX(), 
							rectangle.getY(), 
							20.1f, 
							27,
							false, 
							gameScreen.getWorld(),
							"player");
					gameScreen.setPlayer(new Player(rectangle.getWidth(), rectangle.getHeight(), body));
				} else if (name.equals("enemy")) {
					Body body = BodyHelperService.createBody(
							rectangle.getX(), 
							rectangle.getY(), 
							23, 
							25,
							false, 
							gameScreen.getWorld(),
							"enemy");
					gameScreen.setEnemy(new Enemy(rectangle.getWidth(), rectangle.getHeight(), body));
				}
			} 
		}
	}
	
	private void createStaticBody(PolygonMapObject polygonMapObject, boolean setSensor, String name) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		Body body = gameScreen.getWorld().createBody(bodyDef);
		Shape shape = createPolygonShape(polygonMapObject);
		body.createFixture(shape, 1000);
		body.getFixtureList().get(0).setSensor(setSensor);
		body.getFixtureList().get(0).setUserData(name);
		shape.dispose();
	}

	private Shape createPolygonShape(PolygonMapObject polygonMapObject) {
		float[] vertices = polygonMapObject.getPolygon().getTransformedVertices();
		Vector2[] worldVertices = new Vector2[vertices.length / 2];
		for (int i = 0; i < vertices.length / 2; i++) {
			Vector2 current = new Vector2(vertices[i * 2] / PPM, vertices[i * 2 + 1] / PPM);
			worldVertices[i] = current;
		}
		
		PolygonShape shape = new PolygonShape();
		shape.set(worldVertices);
		return shape;
	}
	
	public int getMapWidth() {
		return tiledMap.getProperties().get("width", Integer.class);
	}
	
	public int getMapHeight() {
		return tiledMap.getProperties().get("height", Integer.class);
	}
}

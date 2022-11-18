package com.killmonster.map;

import com.killmonster.*;
import com.killmonster.entity.character.*;
import com.killmonster.entity.character.Character;
import com.killmonster.entity.objects.*;
import com.killmonster.entity.shooter.*;
import com.killmonster.util.box2d.TiledObjectUtils;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class GameMap implements Disposable {

	private GameWorldManager gameWorldManager;
	private String mapFilePath;
	private TiledMap tiledMap;
	
	private Music backgroundMusic;
	private int mapWidth;
	private int mapHeight;
	private int mapTileSize;
	
	public GameMap(GameWorldManager gameWorldManager, String mapFilePath) {
		this.gameWorldManager = gameWorldManager;
		this.mapFilePath = mapFilePath;
		tiledMap = gameWorldManager.getMapLoader().load(mapFilePath);
		
		// Extract width, height and tile size from the map.
		mapWidth = tiledMap.getProperties().get("width", Integer.class);
		mapHeight = tiledMap.getProperties().get("height", Integer.class);
		int tileWidth = tiledMap.getProperties().get("tilewidth", Integer.class);
		int tileHeight = tiledMap.getProperties().get("tileheight", Integer.class);
		assert tileWidth == tileHeight;
		mapTileSize = tileWidth;
		
		backgroundMusic = gameWorldManager.getAssets().get((String) tiledMap.getProperties().get("backgroundMusic"));
		
		// Create bodies in the world according to each map layer.
		TiledObjectUtils.parseLayers(gameWorldManager.getWorld(), this);
	}
	
	public Vector2 getPosPlayer() {
		MapObject object = tiledMap.getLayers().get(GameMapLayer.PLAYER.ordinal()).getObjects().getByType(RectangleMapObject.class).get(0);
		Rectangle rect = ((RectangleMapObject) object).getRectangle();
		
		return new Vector2((rect.getX() + rect.getWidth()/2) / 100, (rect.getY() + rect.getHeight()/2) / 100);
	}
    
	public Player spawnPlayer() {
		MapObject object = tiledMap.getLayers().get(GameMapLayer.PLAYER.ordinal()).getObjects().getByType(RectangleMapObject.class).get(0);
		Rectangle rect = ((RectangleMapObject) object).getRectangle();
		
		return new Player(gameWorldManager, rect.getX() + rect.getWidth()/2, rect.getY() + rect.getHeight()/2);
	}
	
	public Array<Character> spawnNPCs() {
		Array<Character> npcs = new Array<>();
		
		for (MapObject object : tiledMap.getLayers().get(GameMapLayer.CRABBY.ordinal()).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			npcs.add(new Crabby(gameWorldManager.getAssets(), gameWorldManager.getWorld(), rect.getX() + rect.getWidth()/2, rect.getY() + rect.getHeight()/2));
		}
        
		return npcs;
	}
	
	public Array<Box> spawnBoxes() {
		Array<Box> box = new Array<>();
		
		for (MapObject object : tiledMap.getLayers().get(GameMapLayer.BOX.ordinal()).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			Box x = new Box(gameWorldManager, rect.getX() + rect.getWidth()/2, rect.getY() + rect.getHeight()/2);
			box.add(x);
		}
        
		return box;
	}
	
	public Array<Spike> spawnSpikes() {
		Array<Spike> spikes = new Array<>();
		
		for (MapObject object : tiledMap.getLayers().get(GameMapLayer.SPIKE.ordinal()).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			Spike x = new Spike(gameWorldManager, rect.getX() + rect.getWidth()/2, rect.getY() + rect.getHeight()/2);
			spikes.add(x);
		}
        
		return spikes;
	}
	
	public Array<Water> spawnWater() {
		Array<Water> water = new Array<>();
		
		for (MapObject object : tiledMap.getLayers().get(GameMapLayer.WATER.ordinal()).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			Water x = new Water(gameWorldManager, rect.getX() + rect.getWidth()/2, rect.getY() + rect.getHeight()/2);
			water.add(x);
		}
        
		return water;
	}
	
	public Array<Shooter> spawnCannons() {
		Array<Shooter> cannons = new Array<>();
		
		for (MapObject object : tiledMap.getLayers().get(GameMapLayer.CANNON.ordinal()).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			Cannon x = new Cannon(gameWorldManager, rect.getX() + rect.getWidth()/2, rect.getY() + rect.getHeight()/2);
			cannons.add(x);
		}
        
		return cannons;
	}
	
	public Array<GameObject> spawnTrees() {
		Array<GameObject> objs = new Array<>();
		
		for (MapObject object : tiledMap.getLayers().get(GameMapLayer.TREE_ONE.ordinal()).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			TreeOne x = new TreeOne(gameWorldManager, rect.getX() + rect.getWidth()/2, rect.getY() + rect.getHeight()/2);
			objs.add(x);
		}
        
		return objs;
	}
    
	public TiledMap getTiledMap() {
		return tiledMap;
	}
	
	public int getMapWidth() {
		return mapWidth;
	}
	
	public int getMapHeight() {
		return mapHeight;
	}
	
	public int getMapTileSize() {
		return mapTileSize;
	}
	
	public void playBackgroundMusic(float vol) {
		backgroundMusic.setLooping(true);
		backgroundMusic.setVolume(vol);
		backgroundMusic.play();
	}
	
	public void stopBackgroundMusic() {
		backgroundMusic.stop();
	}
	
	public void pauseBackgroundMusic() {
		backgroundMusic.pause();
	}
	
	@Override
	public String toString() {
		return mapFilePath;
	}
	
	@Override
	public void dispose() {
		tiledMap.dispose();
	}

}
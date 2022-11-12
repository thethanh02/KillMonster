package com.killmonster.map;

import com.killmonster.*;
import com.killmonster.entity.character.*;
import com.killmonster.entity.character.Character;
import com.killmonster.entity.objects.*;
import com.killmonster.util.box2d.TiledObjectUtils;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class GameMap implements Disposable {

	private GameWorldManager gameWorldManager;
	private String mapFilePath;
	private TiledMap tiledMap;
	
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
		
		
		// Update brightness according to this map.
		
		// Create bodies in the world according to each map layer.
		TiledObjectUtils.parseLayers(gameWorldManager.getWorld(), this);
	}
    
	public Player spawnPlayer() {
		MapObject object = tiledMap.getLayers().get(GameMapLayer.PLAYER.ordinal()).getObjects().getByType(RectangleMapObject.class).get(0);
		Rectangle rect = ((RectangleMapObject) object).getRectangle();
		
		return new Player(gameWorldManager, rect.getX() + rect.getWidth()/2, rect.getY() + rect.getHeight()/2);
	}
	
	public Array<Character> spawnNPCs() {
		Array<Character> npcs = new Array<>();
		
		for (MapObject object : tiledMap.getLayers().get(GameMapLayer.ENEMIES.ordinal()).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			npcs.add(new Crabby(gameWorldManager.getAssets(), gameWorldManager.getWorld(), rect.getX() + rect.getWidth()/2, rect.getY() + rect.getHeight()/2));
		}
        
		return npcs;
	}
    
	public Array<BluePotion> spawnPotions() {
		Array<BluePotion> potions = new Array<>();

		for (MapObject object : tiledMap.getLayers().get(GameMapLayer.POTION.ordinal()).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			potions.add(new BluePotion(gameWorldManager.getAssets(), gameWorldManager.getWorld(), rect.getX() + rect.getWidth()/2, rect.getY() + rect.getHeight()/2));
		}
        
		return potions;
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
	
	
	@Override
	public String toString() {
		return mapFilePath;
	}
	
	@Override
	public void dispose() {
		tiledMap.dispose();
	}

}
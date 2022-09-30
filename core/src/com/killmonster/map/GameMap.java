package com.killmonster.map;

import com.killmonster.*;
import com.killmonster.character.*;
import com.killmonster.character.Character;
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
        
        return new Player(gameWorldManager, rect.getX(), rect.getY());
    }
    
    public Array<Character> spawnNPCs() {
        Array<Character> knights = new Array<>();
        
        for (MapObject object : tiledMap.getLayers().get(GameMapLayer.NPCS.ordinal()).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            knights.add(new Crabby(gameWorldManager.getAssets(), gameWorldManager.getWorld(), rect.getX(), rect.getY()));
        }
        
        return knights;
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
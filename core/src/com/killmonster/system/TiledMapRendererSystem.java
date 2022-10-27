package com.killmonster.system;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.killmonster.event.GameEventManager;
import com.killmonster.event.GameEventType;
import com.killmonster.event.MapChangedEvent;
import com.killmonster.util.Constants;

public class TiledMapRendererSystem extends EntitySystem {
	
	private OrthographicCamera camera;
	private OrthogonalTiledMapRenderer renderer;
	
	public TiledMapRendererSystem(OrthographicCamera camera) {
		this.camera = camera;
		renderer = new OrthogonalTiledMapRenderer(null, 1 / Constants.PPM);
		
		GameEventManager.getINSTANCE().addEventListener(GameEventType.MAP_CHANGED, (MapChangedEvent e) -> {
			renderer.setMap(e.getGameMap().getTiledMap());
		});
	}
	
	@Override
	public void update(float deltaTime) {
		renderer.setView(camera);
		renderer.render();
	}
	
}

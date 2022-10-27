package com.killmonster.system;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class Box2DDebugRendererSystem extends EntitySystem{
	
	private World world;
	private Camera camera;
	private Box2DDebugRenderer renderer;
	
	public Box2DDebugRendererSystem(World world, Camera camera) {
		super();
		this.world = world;
		this.camera = camera;
		this.renderer = new Box2DDebugRenderer();
	}
	
	@Override
	public void update(float deltaTime) {
		renderer.render(world, camera.combined);
	}
}

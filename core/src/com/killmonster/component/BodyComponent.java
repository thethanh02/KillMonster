package com.killmonster.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.killmonster.util.box2d.BodyBuilder;

public class BodyComponent implements Component {
	
	public BodyBuilder bodyBuilder;
	public Body body;
	public Fixture bodyFixture;
	public Fixture meleeWeaponFixture;
	public Fixture feetFixture;
	
	public BodyComponent(World world) {
		this.bodyBuilder = new BodyBuilder(world);
	}
}

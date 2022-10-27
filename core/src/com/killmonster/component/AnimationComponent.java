package com.killmonster.component;

import java.util.HashMap;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimationComponent implements Component {
	
	public HashMap<StatComponent.State, Animation<TextureRegion>> animations;
	
	public AnimationComponent() {
		animations = new HashMap<>();
	}
}

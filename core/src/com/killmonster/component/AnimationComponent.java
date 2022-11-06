package com.killmonster.component;

import java.util.HashMap;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimationComponent implements Component {
	
	public HashMap<CharacterState, Animation<TextureRegion>> animations;
	
	public AnimationComponent() {
		animations = new HashMap<>();
	}
	
	public Animation<TextureRegion> get(CharacterState state) {
		return animations.get(state);
	}
	
	public void put(CharacterState state, Animation<TextureRegion> animation) {
		animations.put(state, animation);
	}
	
}

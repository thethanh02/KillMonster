package com.killmonster.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class SpriteComponent implements Component {
	
	public Sprite sprite;
	
	public SpriteComponent(Texture texture, float x, float y) {
		sprite = new Sprite(texture);
		sprite.setPosition(x, y);
	}
}

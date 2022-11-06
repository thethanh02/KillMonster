package com.killmonster.component;

import com.badlogic.ashley.core.ComponentMapper;

public class Mappers {

	public static final ComponentMapper<CharacterStatsComponent> CHARACTER_STATS;
	public static final ComponentMapper<AnimationComponent> ANIMATION;
	public static final ComponentMapper<BodyComponent> BODY;
	public static final ComponentMapper<SpriteComponent> SPRITE;
	public static final ComponentMapper<StateComponent> STATE;
	public static final ComponentMapper<CombatTargetComponent> COMBAT_TARGET;
	
	static {
		CHARACTER_STATS = ComponentMapper.getFor(CharacterStatsComponent.class);
		ANIMATION = ComponentMapper.getFor(AnimationComponent.class);
		BODY = ComponentMapper.getFor(BodyComponent.class);
		SPRITE = ComponentMapper.getFor(SpriteComponent.class);
		STATE = ComponentMapper.getFor(StateComponent.class);
		COMBAT_TARGET = ComponentMapper.getFor(CombatTargetComponent.class);
	}
}

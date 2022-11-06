package com.killmonster.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.killmonster.character.Character;
import com.killmonster.component.BodyComponent;
import com.killmonster.component.CharacterAIComponent;
import com.killmonster.component.CharacterStatsComponent;
import com.killmonster.component.CombatTargetComponent;
import com.killmonster.component.Mappers;
import com.killmonster.component.StateComponent;
import com.killmonster.util.Constants;
import com.killmonster.util.Utils;

public class CharacterAISystem extends IteratingSystem {

	private StateComponent state;
	private BodyComponent body;
	private CharacterStatsComponent stats;
	private CombatTargetComponent targets;
	
	public CharacterAISystem() {
		super(Family.all(CharacterAIComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Character character = (entity instanceof Character) ? (Character) entity : null;
		if (character == null) return;
		
		state = Mappers.STATE.get(entity);
		body = Mappers.BODY.get(entity);
		stats = Mappers.CHARACTER_STATS.get(entity);
		targets = Mappers.COMBAT_TARGET.get(entity);
		
		if (state.setToKill) return;
		
		if (state.isAlerted && targets.lockedOnTarget != null) {
			if (targets.hasInRangeTarget()) {
				character.swingWeapon();
				
				if (targets.lockedOnTarget.getComponent(StateComponent.class).setToKill) {
					targets.lockedOnTarget = null;
				}
			} else {
				BodyComponent targetBody = Mappers.BODY.get(targets.lockedOnTarget);
				
				float selfPositionX = body.body.getPosition().x;
				float targetPositionX = targetBody.body.getPosition().x;
				
				if (Utils.getDistance(selfPositionX, targetPositionX) >= stats.attackRange * 2 / Constants.PPM) {
					character.getBehavioralModel().jumpIfStucked(deltaTime, .1f);
				}
			}
		} else {
			character.getBehavioralModel().moveRandomly(deltaTime, 0, 5, 0, 5);
		}
	}
	
}

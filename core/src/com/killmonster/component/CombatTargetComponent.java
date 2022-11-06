package com.killmonster.component;

import com.killmonster.character.Character;
import com.badlogic.ashley.core.Component;

public class CombatTargetComponent implements Component {
	
	public Character lockedOnTarget;
	public Character inRangeTarget;
	
	public boolean hasLockedOntarget() {
		return lockedOnTarget != null;
	}
	
	public boolean hasInRangeTarget() {
		return inRangeTarget != null;
	}
}

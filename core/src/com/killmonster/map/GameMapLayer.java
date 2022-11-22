package com.killmonster.map;

public enum GameMapLayer {

	GROUND,         // 0: Player can only jump while standing on the ground.
	PLATFORM,       // 1: Floating and one-way passthrough platforms. Player can also jump on this.
	WALL,           // 2: Verical walls or ceiling. Colliding with these wont set isJumping back to false.
	CLIFF_MARKER,   // 3: Alert an NPC that it is near a cliff.
	PLAYER,         // 4: Player
	BOX,			// 5: Box
	SPIKE,			// 6: Spike
	CANNON,			// 7: Cannon
	WATER,			// 8: Water
	TREE_ONE,		// 9: Tree1
	TREE_TWO,		// 10: Tree2
	TREE_THREE,		// 11: Tree3
	CRABBY,        	// 12: Enemies Crabby
	SHARK,			// 13: Enemies Shark
	PINK_STAR,		// 14: Enemies PinkStar
	BLUE_DIAMOND,	// 15: Blue Diamond
	GREEN_DIAMOND,	// 16: Green Diamond
	RED_DIAMOND,	// 17: Red Diamond
	CANNON_FLIP,	// 18: Cannon flipX
	BARREL;			// 19: Barrel
}
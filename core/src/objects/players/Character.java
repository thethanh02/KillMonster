package objects.players;

public interface Character {
	public enum State {
		JUMP_FALLING,
		JUMPING,
		ATTACKING,
		IDLE,
		RUNNING,
		KILLED
	}
}

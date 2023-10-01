package entities;

public enum CollisionHandler {
    PLAYER(0), PUSH(1), STOP(2), RIVER(3), BOAT(4), VICTORY(999);

    public final int priority;

    CollisionHandler(int priority) {
        this.priority = priority;
    }
}

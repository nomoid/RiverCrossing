package entities;

public enum CollisionHandler {
    PUSH(0), STOP(1), RIVER(2), BOAT(3);

    public final int priority;

    CollisionHandler(int priority) {
        this.priority = priority;
    }
}

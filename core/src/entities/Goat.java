package entities;

import com.badlogic.gdx.graphics.Color;
import com.nomoid.rivercrossing.Coordinate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static entities.CollisionHandler.*;

public class Goat extends Entity {

    public Goat(EntityContext context, int x, int y) {
        super(context);
        this.x = x;
        this.y = y;
    }

    @Override
    public Color getColor() {
        return new Color(0.8f, 0.9f, 0.6f, 1);
    }

    @Override
    public String getText() {
        return "G";
    }

    @Override
    public CollisionHandler getCollisionHandler() {
        return PUSH;
    }

    @Override
    public boolean hasIndependentBehavior() {
        return true;
    }

    @Override
    public boolean independentBehavior(EntityContext context, int tickCount) {
        for (Entity entity : context) {
            if (entity instanceof Cabbage) {
                HashSet<CollisionHandler> blockers =
                        new HashSet<>(Arrays.asList(
                                STOP,
                                RIVER,
                                BOAT,
                                PLAYER,
                                PUSH
                        ));
                Coordinate source = Coordinate.fromEntity(this);
                Coordinate target = Coordinate.fromEntity(entity);
                HashSet<Coordinate> ignore = new HashSet<>(Arrays.asList(source, target));
                List<Coordinate> canReach = context.canReach(this, entity, blockers, ignore);
                if (canReach == null || canReach.size() < 2) {
                    continue;
                }
                if (tickCount % 2 == 0) {
                    return true;
                }
                Coordinate nextStep = canReach.get(1);
                if (nextStep.equals(target)) {
                    context.enqueueRemoval(entity.getId());
                }
                setPosition(nextStep.x, nextStep.y);
                return true;
            }
        }
        return false;
    }
}

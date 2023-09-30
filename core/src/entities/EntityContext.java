package entities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EntityContext implements Iterable<Entity> {

    private final Map<Integer, Entity> entities = new HashMap<>();

    public void addEntity(Entity entity) {
        int id = entity.getId();
        if (!entities.containsKey(id)) {
            entities.put(id, entity);
        }
    }

    public Entity getEntity(int id) {
        return entities.get(id);
    }

    @Override
    public Iterator<Entity> iterator() {
        return entities.values().iterator();
    }
}


package entities;

import com.nomoid.rivercrossing.Coordinate;

import java.util.*;

public class EntityContext implements Iterable<Entity> {

    private int minX;
    private int maxX;
    private int minY;
    private int maxY;
    private final Map<Integer, Entity> entities = new LinkedHashMap<>();
    private final Set<Integer> toRemove = new HashSet<>();

    public void addEntity(Entity entity) {
        int id = entity.getId();
        if (!entities.containsKey(id)) {
            entities.put(id, entity);
        }
    }

    public void enqueueRemoval(int id) {
        toRemove.add(id);
    }

    public void processRemoval() {
        for (int i : toRemove) {
            entities.remove(i);
        }
    }

    public Entity getEntity(int id) {
        return entities.get(id);
    }

    @Override
    public Iterator<Entity> iterator() {
        return entities.values().iterator();
    }

    public EntityContext(int minX, int maxX, int minY, int maxY) {
        reset(minX, maxX, minY, maxY);
    }

    public void reset(int minX, int maxX, int minY, int maxY) {
        entities.clear();
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    public int hScore(Coordinate source, Coordinate target) {
        return Math.abs(target.y - source.y) + Math.abs(target.x - source.x);
    }

    class CoordinateWithFScore implements Comparable<CoordinateWithFScore> {
        Coordinate coordinate;
        Coordinate target;
        int fScore;

        public CoordinateWithFScore(Coordinate coordinate, Coordinate target, int fScore) {
            this.coordinate = coordinate;
            this.target = target;
            this.fScore = fScore;
        }

        public int compareTo(CoordinateWithFScore other) {
            int i = Integer.compare(fScore, other.fScore);
            if (i != 0) {
                return i;
            }
            int j = Integer.compare(hScore(coordinate, target), hScore(other.coordinate, target));
            if (j != 0) {
                return j;
            }
            return coordinate.compareTo(other.coordinate);
        }
    }

    public Set<Coordinate> neighbors(Coordinate coordinate, Set<CollisionHandler> blockers, Set<Coordinate> ignore) {
        HashSet<Coordinate> neighbors = new HashSet<>();
        int x = coordinate.x;
        int y = coordinate.y;
        if (x > minX) {
            neighbors.add(new Coordinate(x - 1, y));
        }
        if (x < maxX) {
            neighbors.add(new Coordinate(x + 1, y));
        }
        if (y > minY) {
            neighbors.add(new Coordinate(x, y - 1));
        }
        if (y < maxY) {
            neighbors.add(new Coordinate(x, y + 1));
        }
        for (Entity entity : this) {
            CollisionHandler collisionHandler = entity.getCollisionHandler();
            if (blockers.contains(collisionHandler)) {
                Coordinate entityCoord = Coordinate.fromEntity(entity);
                if (!ignore.contains(entityCoord)) {
                    neighbors.remove(entityCoord);
                }
            }
        }
        return neighbors;
    }

    public List<Coordinate> canReach(Entity sourceEntity, Entity targetEntity, Set<CollisionHandler> blockers, Set<Coordinate> ignore) {
        int largeValue = 1000000000;
        Coordinate source = Coordinate.fromEntity(sourceEntity);
        Coordinate target = Coordinate.fromEntity(targetEntity);
        PriorityQueue<CoordinateWithFScore> openSet = new PriorityQueue<>();
        HashMap<Coordinate, Coordinate> cameFrom = new HashMap<>();
        HashMap<Coordinate, Integer> gScore = new HashMap<>();
        gScore.put(source, 0);
        HashMap<Coordinate, Integer> fScore = new HashMap<>();
        fScore.put(source, hScore(source, target));
        openSet.add(new CoordinateWithFScore(source, target, fScore.get(source)));

        while (!openSet.isEmpty()) {
            CoordinateWithFScore top = openSet.poll();
            Coordinate current = top.coordinate;
            if (current.equals(target)) {
                ArrayList<Coordinate> canReach = new ArrayList<>();
                canReach.add(target);
                Coordinate cameFromCoord = cameFrom.get(target);
                while (cameFromCoord != null) {
                    canReach.add(cameFromCoord);
                    cameFromCoord = cameFrom.get(cameFromCoord);
                }
                Collections.reverse(canReach);
                System.out.println(canReach);
                return canReach;
            }
            for (Coordinate neighbor : neighbors(current, blockers, ignore)) {
                int tentativeGScore = gScore.getOrDefault(current, largeValue) + 1;
                if (tentativeGScore < gScore.getOrDefault(neighbor, largeValue * 2)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    openSet.remove(new CoordinateWithFScore(neighbor, target, fScore.getOrDefault(neighbor, largeValue)));
                    fScore.put(neighbor, tentativeGScore + hScore(neighbor, target));
                    openSet.add(new CoordinateWithFScore(neighbor, target, fScore.get(neighbor)));
                }
            }
        }
        return null;
    }
}


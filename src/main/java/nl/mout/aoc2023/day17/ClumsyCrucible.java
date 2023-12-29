package nl.mout.aoc2023.day17;

import java.util.*;
import java.util.function.Predicate;

import static java.lang.Character.digit;
import static java.lang.Integer.MAX_VALUE;
import static nl.mout.aoc2023.support.InputLoader.loadInput;

public class ClumsyCrucible {

    private final int[][] map;

    public ClumsyCrucible(String input) {
        this.map = input.lines()
                .map(this::parseLine)
                .toArray(int[][]::new);
    }

    public int part1() {
        return findLeastHeatLoss(
                v -> v.location.row == map.length - 1 && v.location.column == map[0].length - 1,
                v -> v.steps < 3,
                v -> true
        );
    }

    public int part2() {
        return findLeastHeatLoss(
                v -> v.location.row == map.length - 1 && v.location.column == map[0].length - 1 && v.steps >= 4,
                v -> v.steps < 10,
                v -> v.steps == 0 || v.steps >= 4
        );
    }

    private record Vertex(Location location, Direction direction, int steps) {
    }

    private record Node(int distance, Vertex vertex) implements Comparable<Node> {
        @Override
        public int compareTo(Node o) {
            return this.distance - o.distance;
        }
    }

    private record Location(int row, int column) {
    }

    enum Direction {
        NORTH, WEST, SOUTH, EAST
    }

    private int[] parseLine(String line) {
        return line.chars().map(ch -> digit(ch, 10)).toArray();
    }

    private int findLeastHeatLoss(Predicate<Vertex> isAtTarget, Predicate<Vertex> isForwardAllowed, Predicate<Vertex> isTurnAllowed) {
        var dist = new HashMap<Vertex, Integer>();
        var start = new Vertex( new Location(0, 0), Direction.EAST, 0);
        dist.put(start, 0);
        var queue = new PriorityQueue<>(List.of(new Node(0, start)));

        while (!queue.isEmpty()) {
            var node = queue.poll();
            if (isAtTarget.test(node.vertex)) {
                return dist.get(node.vertex);
            }
            var neighbors = getNeighbors(node.vertex, isForwardAllowed, isTurnAllowed);
            for (Vertex neighbor : neighbors) {
                var alt = node.distance + map[neighbor.location.row][neighbor.location.column];
                if (alt < dist.getOrDefault(neighbor, MAX_VALUE)) {
                    dist.put(neighbor, alt);
                    queue.offer(new Node(alt, neighbor));
                }
            }
        }
        throw new IllegalStateException("Target location not reachable");
    }

    private Optional<Vertex> moveForward(Vertex vertex, Predicate<Vertex> isAllowed) {
        if (!isAllowed.test(vertex)) {
            return Optional.empty();
        }

        var forward = switch (vertex.direction) {
            case NORTH -> new Location(vertex.location.row - 1, vertex.location.column);
            case WEST -> new Location(vertex.location.row, vertex.location.column - 1);
            case SOUTH -> new Location(vertex.location.row + 1, vertex.location.column);
            case EAST -> new Location(vertex.location.row, vertex.location.column + 1);
        };

        if (isOnMap(forward)) {
            return Optional.of(new Vertex(forward, vertex.direction, vertex.steps + 1));
        }
        return Optional.empty();
    }

    private Optional<Vertex> turnLeft(Vertex vertex, Predicate<Vertex> isAllowed) {
        if (!isAllowed.test(vertex)) {
            return Optional.empty();
        }

        Location left;
        Direction direction;
        switch (vertex.direction) {
            case NORTH -> {
                direction = Direction.WEST;
                left = new Location(vertex.location.row, vertex.location.column - 1);
            }
            case WEST -> {
                direction = Direction.SOUTH;
                left = new Location(vertex.location.row + 1, vertex.location.column);
            }
            case SOUTH -> {
                direction = Direction.EAST;
                left = new Location(vertex.location.row, vertex.location.column + 1);
            }
            default -> {
                direction = Direction.NORTH;
                left = new Location(vertex.location.row - 1, vertex.location.column);
            }
        }

        if (isOnMap(left)) {
            return Optional.of(new Vertex(left, direction, 1));
        }
        return Optional.empty();
    }

    private Optional<Vertex> turnRight(Vertex vertex, Predicate<Vertex> isAllowed) {
        if (!isAllowed.test(vertex)) {
            return Optional.empty();
        }

        Location right;
        Direction direction;
        switch (vertex.direction) {
            case NORTH -> {
                direction = Direction.EAST;
                right = new Location(vertex.location.row, vertex.location.column + 1);
            }
            case WEST -> {
                direction = Direction.NORTH;
                right = new Location(vertex.location.row - 1, vertex.location.column);
            }
            case SOUTH -> {
                direction = Direction.WEST;
                right = new Location(vertex.location.row, vertex.location.column - 1);
            }
            default -> {
                direction = Direction.SOUTH;
                right = new Location(vertex.location.row + 1, vertex.location.column);
            }
        }

        if (isOnMap(right)) {
            return Optional.of(new Vertex(right, direction, 1));
        }
        return Optional.empty();
    }

    private boolean isOnMap(Location location) {
        return 0 <= location.row && location.row < map.length && 0 <= location.column && location.column < map[0].length;
    }

    private Set<Vertex> getNeighbors(Vertex vertex, Predicate<Vertex> isForwardAllowed, Predicate<Vertex> isTurnAllowed) {
        var result = new HashSet<Vertex>();
        moveForward(vertex, isForwardAllowed).ifPresent(result::add);
        turnLeft(vertex, isTurnAllowed).ifPresent(result::add);
        turnRight(vertex, isTurnAllowed).ifPresent(result::add);
        return result;
    }

    public static void main(String[] args) {
        var input = loadInput("day17-input.txt");
        var clumsyCrucible = new ClumsyCrucible(input);
        System.out.printf("Part 1: %d\n", clumsyCrucible.part1());
        System.out.printf("Part 2: %d\n", clumsyCrucible.part2());
    }
}

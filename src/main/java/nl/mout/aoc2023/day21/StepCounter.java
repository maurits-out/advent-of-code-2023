package nl.mout.aoc2023.day21;

import java.util.*;

import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.range;
import static nl.mout.aoc2023.support.InputLoader.loadInput;

public class StepCounter {

    private final char[][] grid;
    private final Position start;

    public StepCounter(String input) {
        grid = input.lines().map(String::toCharArray).toArray(char[][]::new);
        start = range(0, grid.length).boxed()
                .flatMap(r -> range(0, grid[r].length)
                        .mapToObj(c -> new Position(r, c)))
                .filter(p -> getGridValue(p) == 'S')
                .findFirst().orElseThrow();
    }

    public long part1() {
        return calculateDistances().stream()
                .filter(distance -> distance <= 64 && distance % 2 == 0)
                .count();
    }

    public long part2() {
        var distances = calculateDistances().stream()
                .collect(partitioningBy(dist -> dist % 2 == 0));

        var evenCorners = distances.get(true).stream().filter(dist -> dist > 65).count();
        var oddCorners = distances.get(false).stream().filter(dist -> dist > 65).count();

        // the number of full squares we can travel in a straight line from S
        var n = 202300; // ((26501365 - (131 / 2)) / 131) with 131 being grid.length;

        var squaresWithEventParity = (long) n * n;
        var squaresWithOddParity = (long) (n + 1) * (n + 1);
        var cornersToCutOut = (n + 1) * oddCorners;
        var cornersToAdd = n * evenCorners;

        return (squaresWithEventParity * distances.get(true).size()) +
                (squaresWithOddParity * distances.get(false).size()) -
                cornersToCutOut + cornersToAdd;
    }

    private record Position(int row, int column) {
    }

    private record QueueElement(Position position, int distance) {
    }

    private Set<Position> getReachableAdjacentPositions(Position position) {
        var neighbors = Set.of(
                new Position(position.row() - 1, position.column),
                new Position(position.row() + 1, position.column),
                new Position(position.row(), position.column - 1),
                new Position(position.row(), position.column + 1)
        );
        return neighbors.stream()
                .filter(p -> isOnGrid(p) && getGridValue(p) == '.')
                .collect(toSet());
    }

    private boolean isOnGrid(Position p) {
        return p.row() >= 0 && p.row() < grid.length && p.column() >= 0 && p.column() < grid[p.row()].length;
    }

    private char getGridValue(Position p) {
        return grid[p.row()][p.column()];
    }

    private Collection<Integer> calculateDistances() {
        var visited = new HashMap<Position, Integer>();
        var queue = new LinkedList<>(List.of(new QueueElement(start, 0)));
        while (!queue.isEmpty()) {
            var current = queue.pop();
            if (!visited.containsKey(current.position())) {
                visited.put(current.position(), current.distance());
                getReachableAdjacentPositions(current.position()).stream()
                        .filter(p -> !visited.containsKey(p))
                        .forEach(p -> queue.add(new QueueElement(p, current.distance() + 1)));
            }
        }
        return visited.values();
    }

    public static void main(String[] args) {
        var input = loadInput("day21-input.txt");
        var stepCounter = new StepCounter(input);
        System.out.printf("Part 1: %d\n", stepCounter.part1());
        System.out.printf("Part 2: %d\n", stepCounter.part2());
    }
}

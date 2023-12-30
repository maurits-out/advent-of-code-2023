package nl.mout.aoc2023.day23;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.*;
import java.util.function.Function;

import static java.lang.Math.max;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.range;
import static nl.mout.aoc2023.support.InputLoader.loadInput;
import static org.jgrapht.Graphs.successorListOf;

public class Walk {

    private static final Map<Character, List<Delta>> DIRECTIONS = Map.of(
            '>', List.of(new Delta(0, 1)),
            'v', List.of(new Delta(1, 0)),
            '.', List.of(new Delta(-1, 0), new Delta(1, 0), new Delta(0, -1), new Delta(0, 1))
    );

    private final char[][] map;
    private final int height;
    private final int width;

    public Walk(String input) {
        map = input.lines().map(String::toCharArray).toArray(char[][]::new);
        height = map.length;
        width = map[0].length;
    }

    public int part1() {
        return findLongestPath(DIRECTIONS::get);
    }

    public int part2() {
        return findLongestPath(key -> DIRECTIONS.get('.'));
    }

    private record Delta(int dr, int dc) {
    }

    private record Location(int row, int column) {

        Set<Location> getNeighbors() {
            return Set.of(new Location(row - 1, column),
                    new Location(row + 1, column),
                    new Location(row, column - 1),
                    new Location(row, column + 1)
            );
        }

        Location move(Delta delta) {
            return new Location(row + delta.dr(), column + delta.dc());
        }
    }

    private record QueueElement(int distance, Location location) {
    }

    private Location getStart() {
        return new Location(0, 1);
    }

    private Location getDestination() {
        return new Location(height - 1, width - 2);
    }

    private boolean isOnGrid(Location location) {
        return location.row() >= 0 && location.row() < height && location.column() >= 0 && location.column() < width;
    }

    private char getMapValue(Location location) {
        return map[location.row()][location.column()];
    }

    private int countNonForestNeighbors(Location location) {
        return (int) location.getNeighbors().stream()
                .filter(neighbor -> isOnGrid(neighbor) && getMapValue(neighbor) != '#')
                .count();
    }

    private Set<Location> findIntersections() {
        return range(0, height)
                .boxed()
                .flatMap(r -> range(0, width).mapToObj(c -> new Location(r, c)))
                .filter(location -> getMapValue(location) != '#' && countNonForestNeighbors(location) >= 3)
                .collect(toSet());
    }

    private SimpleDirectedWeightedGraph<Location, DefaultEdge> applyEdgeContraction(Function<Character, List<Delta>> directions) {
        var graph = new SimpleDirectedWeightedGraph<Location, DefaultEdge>(DefaultEdge.class);
        graph.addVertex(getStart());
        graph.addVertex(getDestination());
        findIntersections().forEach(graph::addVertex);

        for (Location startLocation : graph.vertexSet()) {
            var queue = new LinkedList<>(List.of(new QueueElement(0, startLocation)));
            var visited = new HashSet<>(List.of(startLocation));
            while (!queue.isEmpty()) {
                var element = queue.pop();
                var location = element.location();
                var distance = element.distance();
                if (distance != 0 && graph.vertexSet().contains(location)) {
                    graph.addEdge(startLocation, location);
                    graph.setEdgeWeight(startLocation, location, distance);
                } else {
                    var value = getMapValue(location);
                    directions.apply(value).stream()
                            .map(location::move)
                            .filter(neighbor -> isOnGrid(neighbor) && getMapValue(neighbor) != '#' && !visited.contains(neighbor))
                            .forEach(neighbor -> {
                                queue.add(new QueueElement(distance + 1, neighbor));
                                visited.add(neighbor);
                            });
                }
            }
        }
        return graph;
    }

    private int findLongestPath(Location location, Set<Location> visited, SimpleDirectedWeightedGraph<Location, DefaultEdge> graph) {
        int max;
        if (location.equals(getDestination())) {
            max = 0;
        } else {
            max = Integer.MIN_VALUE;
            visited.add(location);
            for (Location neighbor : successorListOf(graph, location)) {
                if (!visited.contains(neighbor)) {
                    var edge = graph.getEdge(location, neighbor);
                    max = max(max, findLongestPath(neighbor, visited, graph) + (int) graph.getEdgeWeight(edge));
                }
            }
            visited.remove(location);
        }
        return max;
    }

    private int findLongestPath(Function<Character, List<Delta>> allowedDirections) {
        var graph = applyEdgeContraction(allowedDirections);
        return findLongestPath(getStart(), new HashSet<>(), graph);
    }

    public static void main(String[] args) {
        var input = loadInput("day23-input.txt");
        var walk = new Walk(input);
        System.out.printf("Part 1: %d\n", walk.part1());
        System.out.printf("Part 2: %d\n", walk.part2());
    }
}

package nl.mout.aoc2023.day18;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.function.Function;

import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;
import static java.util.Collections.list;
import static java.util.stream.IntStream.range;
import static nl.mout.aoc2023.support.InputLoader.loadInput;

public class LavaductLagoon {

    private static final Map<Character, List<Integer>> MOVEMENTS = Map.of(
            'U', List.of(0, 1),
            '3', List.of(0, 1),
            'D', List.of(0, -1),
            '1', List.of(0, -1),
            'L', List.of(-1, 0),
            '2', List.of(-1, 0),
            'R', List.of(1, 0),
            '0', List.of(1, 0)
    );

    private final List<String> lines;

    public LavaductLagoon(String input) {
        this.lines = input.lines().toList();
    }

    public long part1() {
        return calculateCubicMetersOfLava(this::parseLinePart1);
    }

    public long part2() {
        return calculateCubicMetersOfLava(this::parseLinePart2);
    }

    private record DigStep(char direction, int meters) {
    }

    private record Point(int x, int y) {
    }

    private List<String> parseLine(String line) {
        var tokenizer = new StringTokenizer(line, " ");
        return list(tokenizer).stream().map(Object::toString).toList();
    }

    private DigStep parseLinePart1(String line) {
        var parts = parseLine(line);
        var direction = parts.getFirst().charAt(0);
        var meters = parseInt(parts.get(1));
        return new DigStep(direction, meters);
    }


    private DigStep parseLinePart2(String line) {
        var color = parseLine(line).getLast();
        var direction = color.charAt(7);
        var meters = parseInt(color.substring(2, 7), 16);
        return new DigStep(direction, meters);
    }

    private long calculateCubicMetersOfLava(Function<String, DigStep> parseLineFunction) {
        var digSteps = lines.stream().map(parseLineFunction).toList();
        var area = calculateArea(digSteps);
        var pointCount = calculatePoints(digSteps);
        var interiorPoints = calculateInteriorPoints(area, pointCount);
        return interiorPoints + pointCount;
    }

    private int calculatePoints(List<DigStep> digSteps) {
        return digSteps.stream().mapToInt(DigStep::meters).sum();
    }

    // https://en.wikipedia.org/wiki/Pick%27s_theorem
    private long calculateInteriorPoints(long area, int b) {
        return area - (b / 2) + 1;
    }

    // https://en.wikipedia.org/wiki/Shoelace_formula
    private long calculateArea(List<DigStep> digSteps) {
        var points = getPolygonPoints(digSteps);
        var first = points.getFirst();
        var last = points.getLast();
        points.addFirst(last);
        points.addLast(first);

        return abs(range(1, points.size() - 1).mapToLong(i -> {
            var p1 = points.get(i);
            var p2 = points.get(i + 1);
            return (long) (p1.y() + p2.y()) * (p1.x() - p2.x());
        }).sum() / 2);
    }

    private List<Point> getPolygonPoints(List<DigStep> digSteps) {
        var points = new ArrayList<Point>();
        points.add(new Point(0, 0));
        for (var step: digSteps) {
            var d = MOVEMENTS.get(step.direction());
            int dx = d.getFirst(), dy = d.getLast();
            var last = points.getLast();
            points.add(new Point(
                    last.x + (dx * step.meters()),
                    last.y + (dy * step.meters())
            ));
        }
        return points;
    }

    public static void main(String[] args) {
        var input = loadInput("day18-input.txt");
        var lavaductLagoon = new LavaductLagoon(input);
        System.out.printf("Part 1: %d\n", lavaductLagoon.part1());
        System.out.printf("Part 2: %d\n", lavaductLagoon.part2());
    }
}

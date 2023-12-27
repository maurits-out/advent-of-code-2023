package nl.mout.aoc2023.day22;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Arrays.sort;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.toSet;
import static nl.mout.aoc2023.support.InputLoader.loadInput;

public class SandSlabs {

    public static final Pattern NUMBER_PATTERN = compile("\\d+");
    private final String input;

    public SandSlabs(String input) {
        this.input = input;
    }

    private int[][] parseInput() {
        return input.lines()
                .map(line -> NUMBER_PATTERN.matcher(line).results()
                        .mapToInt(r -> parseInt(r.group()))
                        .toArray())
                .sorted(comparing(brick -> brick[2]))
                .toArray(int[][]::new);
    }

    private boolean isOverlapping(int[] brick1, int[] brick2) {
        return max(brick1[0], brick2[0]) <= min(brick1[3], brick2[3]) &&
                max(brick1[1], brick2[1]) <= min(brick1[4], brick2[4]);
    }

    private int calculateZ(int[] currentBrick, int indexCurrentBrick, int[][] bricks) {
        var z = 1;
        for (int j = 0; j < indexCurrentBrick; j++) {
            int[] brick = bricks[j];
            if (isOverlapping(currentBrick, brick)) {
                z = max(z, brick[5] + 1);
            }
        }
        return z;
    }

    private void fall(int[][] bricks) {
        for (int i = 0; i < bricks.length; i++) {
            var currentBrick = bricks[i];
            var z = calculateZ(currentBrick, i, bricks);
            currentBrick[5] -= (currentBrick[2] - z);
            currentBrick[2] = z;
        }
    }


    int part1() {
        var bricks = parseInput();
        fall(bricks);
        sort(bricks, comparingInt(brick -> brick[2]));

        var lowerSupportsUpper = new ArrayList<Set<Integer>>(bricks.length);
        var upperIsSupportedByLower = new ArrayList<Set<Integer>>(bricks.length);
        for (var i = 0; i < bricks.length; i++) {
            lowerSupportsUpper.add(new HashSet<>());
            upperIsSupportedByLower.add(new HashSet<>());
        }

        for (var j = 0; j < bricks.length; j++) {
            var upper = bricks[j];
            for (var i = 0; i < j; i++) {
                var lower = bricks[i];
                if (isOverlapping(lower, upper) && lower[5] + 1 == upper[2]) {
                    lowerSupportsUpper.get(i).add(j);
                    upperIsSupportedByLower.get(j).add(i);
                }
            }
        }

        var count = 0;
        for (int i = 0; i < bricks.length; i++) {
            var upperBricks = lowerSupportsUpper.get(i);
            if (upperBricks.stream().allMatch(j -> upperIsSupportedByLower.get(j).size() > 1)) {
                count++;
            }
        }
        return count;
    }

    int part2() {
        var bricks = parseInput();
        fall(bricks);
        sort(bricks, comparingInt(brick -> brick[2]));

        var upperBricksByLower = new ArrayList<Set<Integer>>(bricks.length);
        var lowerBricksByUpper = new ArrayList<Set<Integer>>(bricks.length);
        for (var i = 0; i < bricks.length; i++) {
            upperBricksByLower.add(new HashSet<>());
            lowerBricksByUpper.add(new HashSet<>());
        }

        for (var j = 0; j < bricks.length; j++) {
            var upper = bricks[j];
            for (var i = 0; i < j; i++) {
                var lower = bricks[i];
                if (isOverlapping(lower, upper) && lower[5] + 1 == upper[2]) {
                    upperBricksByLower.get(i).add(j);
                    lowerBricksByUpper.get(j).add(i);
                }
            }
        }

        var sum = 0;
        for (int i = 0; i < bricks.length; i++) {
            var upperBricks = upperBricksByLower.get(i).stream().filter(upper -> lowerBricksByUpper.get(upper).size() == 1).toList();
            var queue = new LinkedList<>(upperBricks);
            var falling = new HashSet<>(queue);

            while (!queue.isEmpty()) {
                var j = queue.pop();
                upperBricksByLower.get(j).stream()
                        .filter(k -> !falling.contains(k))
                        .filter(k -> falling.containsAll(lowerBricksByUpper.get(k)))
                        .forEach(k -> {
                            queue.add(k);
                            falling.add(k);
                        });
            }
            sum += falling.size();
        }
        return sum;
    }

    private int countFallingBricks(int currentBrick, ArrayList<Set<Integer>> lowerSupportsUpper, ArrayList<Set<Integer>> upperIsSupportedByLower) {
        Set<Integer> bricksToFall = lowerSupportsUpper.get(currentBrick).stream()
                .filter(upperBrick -> upperIsSupportedByLower.get(upperBrick).size() < 2)
                .collect(toSet());
        return bricksToFall.stream().mapToInt(brick -> brick).reduce(bricksToFall.size(), (acc, brick) -> acc + countFallingBricks(brick, lowerSupportsUpper, upperIsSupportedByLower));
    }

    public static void main(String[] args) {
        var input = loadInput("day22-input.txt");
        var sandSlabs = new SandSlabs(input);
        System.out.println("Part 1: " + sandSlabs.part1());
        System.out.println("Part 2: " + sandSlabs.part2());

    }
}

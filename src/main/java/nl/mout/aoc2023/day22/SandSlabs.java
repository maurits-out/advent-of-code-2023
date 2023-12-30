package nl.mout.aoc2023.day22;

import java.util.*;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Arrays.sort;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;
import static java.util.regex.Pattern.compile;
import static nl.mout.aoc2023.support.InputLoader.loadInput;

public class SandSlabs {

    public static final Pattern NUMBER_PATTERN = compile("\\d+");

    private final List<Set<Integer>> lowerSupportingUpper = new ArrayList<>();
    private final List<Set<Integer>> upperSupportedByLower = new ArrayList<>();

    public SandSlabs(String input) {
        initializeSupportLists(input);
    }

    public int part1() {
        var count = 0;
        for (var upperBricks : lowerSupportingUpper) {
            if (upperBricks.stream().allMatch(j -> upperSupportedByLower.get(j).size() > 1)) {
                count++;
            }
        }
        return count;
    }

    public int part2() {
        var sum = 0;
        for (var lowerBricks : lowerSupportingUpper) {
            var upperBricks = lowerBricks.stream()
                    .filter(upper -> upperSupportedByLower.get(upper).size() == 1)
                    .toList();
            var queue = new LinkedList<>(upperBricks);
            var falling = new HashSet<>(queue);
            while (!queue.isEmpty()) {
                var j = queue.pop();
                lowerSupportingUpper.get(j).stream()
                        .filter(k -> !falling.contains(k) && falling.containsAll(upperSupportedByLower.get(k)))
                        .forEach(k -> {
                            queue.add(k);
                            falling.add(k);
                        });
            }
            sum += falling.size();
        }
        return sum;
    }

    private int[][] parseInput(String input) {
        return input.lines()
                .map(line -> NUMBER_PATTERN.matcher(line).results()
                        .mapToInt(r -> parseInt(r.group()))
                        .toArray())
                .sorted(comparing(brick -> brick[2]))
                .toArray(int[][]::new);
    }

    private void initializeSupportLists(String input) {
        var bricks = parseInput(input);
        dropBricks(bricks);
        sort(bricks, comparingInt(brick -> brick[2]));
        for (var i = 0; i < bricks.length; i++) {
            lowerSupportingUpper.add(new HashSet<>());
            upperSupportedByLower.add(new HashSet<>());
            var upper = bricks[i];
            for (var j = 0; j < i; j++) {
                var lower = bricks[j];
                if (isOverlappingOnXY(lower, upper) && isDirectlyUnder(lower, upper)) {
                    lowerSupportingUpper.get(j).add(i);
                    upperSupportedByLower.get(i).add(j);
                }
            }
        }
    }

    private boolean isDirectlyUnder(int[] lowerBrick, int[] upperBrick) {
        return lowerBrick[5] + 1 == upperBrick[2];
    }

    private boolean isOverlappingOnXY(int[] brick1, int[] brick2) {
        return max(brick1[0], brick2[0]) <= min(brick1[3], brick2[3]) &&
                max(brick1[1], brick2[1]) <= min(brick1[4], brick2[4]);
    }

    private int calculateZForBrickToDrop(int[] brickToDrop, int indexOfBrickToDrop, int[][] bricks) {
        var z = 1;
        for (int j = 0; j < indexOfBrickToDrop; j++) {
            var brick = bricks[j];
            if (isOverlappingOnXY(brickToDrop, brick)) {
                z = max(z, brick[5] + 1);
            }
        }
        return z;
    }

    private void dropBricks(int[][] bricks) {
        for (int i = 0; i < bricks.length; i++) {
            var brickToDrop = bricks[i];
            var z = calculateZForBrickToDrop(brickToDrop, i, bricks);
            brickToDrop[5] -= (brickToDrop[2] - z);
            brickToDrop[2] = z;
        }
    }

    public static void main(String[] args) {
        var input = loadInput("day22-input.txt");
        var sandSlabs = new SandSlabs(input);
        System.out.printf("Part 1: %d\n", sandSlabs.part1());
        System.out.printf("Part 2: %d\n", sandSlabs.part2());
    }
}

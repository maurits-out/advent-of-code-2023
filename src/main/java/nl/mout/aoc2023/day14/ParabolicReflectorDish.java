package nl.mout.aoc2023.day14;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static nl.mout.aoc2023.support.InputLoader.loadInput;

public class ParabolicReflectorDish {

    private final String input;

    public ParabolicReflectorDish(String input) {
        this.input = input;
    }

    int part1() {
        var dish = input.lines().map(String::toCharArray).toArray(char[][]::new);
        tiltNorth(dish);

        return calculateTotalLoad(dish);
    }

    private int calculateTotalLoad(char[][] dish) {
        var totalLoad = 0;
        for (var r = 0; r < dish.length; r++) {
            for (var c = 0; c < dish[r].length; c++) {
                var loadFactor = dish.length - r;
                if (dish[r][c] == 'O') {
                    totalLoad += loadFactor;
                }
            }
        }
        return totalLoad;
    }

    private void tiltNorth(char[][] dish) {
        for (var r = 1; r < dish.length; r++) {
            for (var c = 0; c < dish[r].length; c++) {
                if (dish[r][c] == 'O') {
                    for (var u = r - 1; u >= 0 && dish[u][c] == '.'; u--) {
                        dish[u][c] = 'O';
                        dish[u + 1][c] = '.';
                    }
                }
            }
        }
    }

    private void tiltWest(char[][] dish) {
        for (var c = 1; c < dish[0].length; c++) {
            for (var r = 0; r < dish.length; r++) {
                if (dish[r][c] == 'O') {
                    for (var u = c - 1; u >= 0 && dish[r][u] == '.'; u--) {
                        dish[r][u] = 'O';
                        dish[r][u + 1] = '.';
                    }
                }
            }
        }
    }

    private void tiltSouth(char[][] dish) {
        for (var r = dish.length - 2; r >= 0; r--) {
            for (var c = 0; c < dish[r].length; c++) {
                if (dish[r][c] == 'O') {
                    for (var u = r + 1; u < dish.length && dish[u][c] == '.'; u++) {
                        dish[u][c] = 'O';
                        dish[u - 1][c] = '.';
                    }
                }
            }
        }
    }

    private void tiltEast(char[][] dish) {
        for (var c = dish[0].length - 2; c >= 0; c--) {
            for (var r = 0; r < dish.length; r++) {
                if (dish[r][c] == 'O') {
                    for (var u = c + 1; u < dish[0].length && dish[r][u] == '.'; u++) {
                        dish[r][u] = 'O';
                        dish[r][u - 1] = '.';
                    }
                }
            }
        }
    }

    private void cycle(char[][] dish) {
        tiltNorth(dish);
        tiltWest(dish);
        tiltSouth(dish);
        tiltEast(dish);
    }

    private char[][] copy(char[][] dish) {
        var dishCopy = new char[dish.length][];
        for (int r = 0; r < dish.length; r++) {
            dishCopy[r] = new char[dish[r].length];
            System.arraycopy(dish[r], 0, dishCopy[r], 0, dish[r].length);
        }
        return dishCopy;
    }

    private boolean isIdenticalDish(char[][] dish, char[][] other) {
        for (int r = 0; r < dish.length; r++) {
            if (!Arrays.equals(dish[r], other[r])) {
                return false;
            }
        }
        return true;
    }

    private Optional<Integer> getIndexOfIdenticalDish(List<char[][]> previousDishes, char[][] dish) {
        for (var i = previousDishes.size() - 1; i >= 0; i--) {
            if (isIdenticalDish(dish, previousDishes.get(i))) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    int part2() {
        var dish = input.lines().map(String::toCharArray).toArray(char[][]::new);
        var previousDishes = new ArrayList<char[][]>();
        var cycleCount = 0;
        var loopFound = false;
        var totalCycles = 1000000000;

        while (cycleCount < totalCycles) {
            if (!loopFound) {
                previousDishes.add(copy(dish));
            }
            cycle(dish);
            cycleCount++;
            if (!loopFound) {
                var indexOfSameDish = getIndexOfIdenticalDish(previousDishes, dish);
                if (indexOfSameDish.isPresent()) {
                    var loopSize = cycleCount - indexOfSameDish.get();
                    var remainingCycles = totalCycles - cycleCount;
                    cycleCount += (remainingCycles / loopSize) * loopSize;
                    loopFound = true;
                }
            }
        }

        return calculateTotalLoad(dish);
    }

    public static void main(String[] args) {
        var input = loadInput("day14-input.txt");
        var dish = new ParabolicReflectorDish(input);
        System.out.println("Part 1: " + dish.part1());
        System.out.println("Part 2: " + dish.part2());
    }
}

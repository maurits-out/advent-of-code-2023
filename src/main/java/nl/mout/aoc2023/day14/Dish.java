package nl.mout.aoc2023.day14;

import java.util.Arrays;

import static java.util.Arrays.deepEquals;
import static java.util.Arrays.deepHashCode;

public class Dish {

    private final char[][] grid;

    private Dish(char[][] grid) {
        this.grid = grid;
    }

    public Dish tiltNorth() {
        var copy = copyGrid();
        for (var r = 1; r < copy.length; r++) {
            for (var c = 0; c < copy[r].length; c++) {
                if (copy[r][c] == 'O') {
                    for (var u = r - 1; u >= 0 && copy[u][c] == '.'; u--) {
                        copy[u][c] = 'O';
                        copy[u + 1][c] = '.';
                    }
                }
            }
        }
        return new Dish(copy);
    }

    public Dish tiltWest() {
        var copy = copyGrid();
        for (var c = 1; c < copy[0].length; c++) {
            for (var r = 0; r < copy.length; r++) {
                if (copy[r][c] == 'O') {
                    for (var u = c - 1; u >= 0 && copy[r][u] == '.'; u--) {
                        copy[r][u] = 'O';
                        copy[r][u + 1] = '.';
                    }
                }
            }
        }
        return new Dish(copy);
    }

    public Dish tiltSouth() {
        var copy = copyGrid();
        for (var r = copy.length - 2; r >= 0; r--) {
            for (var c = 0; c < copy[r].length; c++) {
                if (copy[r][c] == 'O') {
                    for (var u = r + 1; u < copy.length && copy[u][c] == '.'; u++) {
                        copy[u][c] = 'O';
                        copy[u - 1][c] = '.';
                    }
                }
            }
        }
        return new Dish(copy);
    }

    public Dish tiltEast() {
        var copy = copyGrid();
        for (var c = copy[0].length - 2; c >= 0; c--) {
            for (var r = 0; r < copy.length; r++) {
                if (copy[r][c] == 'O') {
                    for (var u = c + 1; u < copy[0].length && copy[r][u] == '.'; u++) {
                        copy[r][u] = 'O';
                        copy[r][u - 1] = '.';
                    }
                }
            }
        }
        return new Dish(copy);
    }

    public int totalLoad() {
        var totalLoad = 0;
        for (var r = 0; r < grid.length; r++) {
            for (var c = 0; c < grid[r].length; c++) {
                if (grid[r][c] == 'O') {
                    var loadFactor = grid.length - r;
                    totalLoad += loadFactor;
                }
            }
        }
        return totalLoad;
    }

    private char[][] copyGrid() {
        char[][] copy = new char[grid.length][];
        for (var r = 0; r < grid.length; r++) {
            copy[r] = Arrays.copyOf(grid[r], grid[r].length);
        }
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Dish dish = (Dish) o;
        return deepEquals(grid, dish.grid);
    }

    @Override
    public int hashCode() {
        return deepHashCode(grid);
    }

    public static Dish of(String input) {
        char[][] grid = input.lines().map(String::toCharArray).toArray(char[][]::new);
        return new Dish(grid);
    }
}

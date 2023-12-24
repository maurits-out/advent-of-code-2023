package nl.mout.aoc2023.day11;

import nl.mout.aoc2023.support.InputLoader;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.range;

public class CosmicExpansion {

    private final List<String> image;
    private final List<Coordinate> galaxies;

    private final Set<Integer> emptyRows;
    private final Set<Integer> emptyColumns;


    public CosmicExpansion(String input) {
        this.image = input.lines().toList();
        this.emptyRows = range(0, image.size())
                .filter(row -> image.get(row).chars().allMatch(ch -> ch == '.'))
                .boxed()
                .collect(toSet());
        this.emptyColumns = range(0, image.getFirst().length())
                .filter(column -> image.stream().allMatch(row -> row.charAt(column) == '.'))
                .boxed()
                .collect(toSet());
        this.galaxies = range(0, image.size())
                .boxed()
                .flatMap(r -> range(0, image.getFirst().length()).boxed().map(c -> new Coordinate(r, c)))
                .filter(coordinate -> image.get(coordinate.row).charAt(coordinate.column) == '#')
                .toList();
    }

    record Coordinate(int row, int column) {

    }

    public static void main(String[] args) {
        var input = InputLoader.loadInput("day11-input.txt");
        var cosmicExpansion = new CosmicExpansion(input);
        System.out.println("Part 1: " + cosmicExpansion.part1());
        System.out.println("Part 2: " + cosmicExpansion.part2());
    }

    private int countEmptyRows(int from, int to) {
        return (int) IntStream.rangeClosed(from + 1, to - 1).filter(emptyRows::contains).count();
    }

    private int countEmptyColumns(int from, int to) {
        return (int) IntStream.rangeClosed(from + 1, to - 1).filter(emptyColumns::contains).count();
    }

    private long part1() {
        return calculateDistances(2);
    }

    private long part2() {
        return calculateDistances(1000000);
    }

    private long calculateDistances(int factor) {
        var sum = 0L;
        for (var i = 0; i < galaxies.size() - 1; i++) {
            for (var j = i + 1; j < galaxies.size(); j++) {
                var first = galaxies.get(i);
                var second = galaxies.get(j);
                var emptyRowCount = countEmptyRows(min(first.row, second.row), max(first.row, second.row));
                var emptyColumnCount = countEmptyColumns(min(first.column, second.column), max(first.column, second.column));

                var distance = Math.abs(first.row - second.row) - emptyRowCount + (factor * emptyRowCount)
                        + Math.abs(first.column - second.column) - emptyColumnCount + (factor * emptyColumnCount);
                sum += distance;
            }
        }
        return sum;
    }
}

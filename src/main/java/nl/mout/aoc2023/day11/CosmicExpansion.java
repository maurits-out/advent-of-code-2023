package nl.mout.aoc2023.day11;

import java.util.List;
import java.util.Set;

import static java.lang.Math.*;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static nl.mout.aoc2023.support.InputLoader.loadInput;

public class CosmicExpansion {

    private final List<Coordinate> galaxies;
    private final Set<Integer> emptyRows;
    private final Set<Integer> emptyColumns;

    public CosmicExpansion(String input) {
        List<String> image = input.lines().toList();
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

    public long part1() {
        return calculateDistances(2);
    }

    public long part2() {
        return calculateDistances(1000000);
    }

    private record Coordinate(int row, int column) {
    }

    private int countEmptyRows(int from, int to) {
        return (int) rangeClosed(from + 1, to - 1).filter(emptyRows::contains).count();
    }

    private int countEmptyColumns(int from, int to) {
        return (int) rangeClosed(from + 1, to - 1).filter(emptyColumns::contains).count();
    }

    private long calculateDistances(int factor) {
        var sum = 0L;
        for (var i = 0; i < galaxies.size() - 1; i++) {
            for (var j = i + 1; j < galaxies.size(); j++) {
                var first = galaxies.get(i);
                var second = galaxies.get(j);
                var emptyRowCount = countEmptyRows(min(first.row, second.row), max(first.row, second.row));
                var emptyColumnCount = countEmptyColumns(min(first.column, second.column), max(first.column, second.column));
                var distance = abs(first.row - second.row) - emptyRowCount + (factor * emptyRowCount)
                        + abs(first.column - second.column) - emptyColumnCount + (factor * emptyColumnCount);
                sum += distance;
            }
        }
        return sum;
    }

    public static void main(String[] args) {
        var input = loadInput("day11-input.txt");
        var cosmicExpansion = new CosmicExpansion(input);
        System.out.printf("Part 1: %d\n", cosmicExpansion.part1());
        System.out.printf("Part 2: %d\n", cosmicExpansion.part2());
    }
}

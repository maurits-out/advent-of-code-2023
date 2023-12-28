package nl.mout.aoc2023.day12;

import nl.mout.aoc2023.support.InputLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Arrays.stream;

public class HotSprings {

    private final List<ConditionRecord> records;

    private final Map<CacheKey, Long> cache = new HashMap<>();

    public HotSprings(String input) {
        this.records = parse(input);
    }

    public long part1() {
        return countAndSumArrangements(records.stream());
    }

    public long part2() {
        return countAndSumArrangements(records.stream().map(ConditionRecord::extend));
    }

    private long countAndSumArrangements(Stream<ConditionRecord> records) {
        return records.mapToLong(record -> countArrangements(record.field(), record.sizes())).sum();
    }

    private record ConditionRecord(String field, List<Integer> sizes) {
        ConditionRecord extend() {
            var extendedField = new StringBuilder();
            extendedField.repeat(field + "?", 5);
            extendedField.setLength(extendedField.length() - 1);

            var extendedSizes = new ArrayList<Integer>();
            for (var i = 0; i < 5; i++) {
                extendedSizes.addAll(sizes);
            }

            return new ConditionRecord(extendedField.toString(), extendedSizes);
        }
    }

    private record CacheKey(String field, List<Integer> expectedBlockSizes) {
    }

    private List<ConditionRecord> parse(String input) {
        return input.lines().map(line -> {
            var parts = line.split("[ ,]");
            var sizes = stream(parts, 1, parts.length).map(Integer::parseInt).toList();
            return new ConditionRecord(parts[0], sizes);
        }).toList();
    }

    private long countArrangements(String field, List<Integer> expectedBlockSizes) {
        if (field.isEmpty()) {
            if (expectedBlockSizes.isEmpty()) {
                return 1;
            }
            return 0;
        }

        if (expectedBlockSizes.isEmpty()) {
            if (field.indexOf('#') == -1) {
                return 1;
            }
            return 0;
        }

        var key = new CacheKey(field, expectedBlockSizes);
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        var count = 0L;
        var current = field.charAt(0);
        if (current == '#' || current == '?') {
            var expectedSize = expectedBlockSizes.getFirst();
            if (expectedSize <= field.length()
                    && field.substring(0, expectedSize).indexOf('.') == -1
                    && (expectedSize == field.length() || field.charAt(expectedSize) != '#')) {
                var updatedBlockSizes = expectedBlockSizes.subList(1, expectedBlockSizes.size());
                if (expectedSize == field.length()) {
                    count += countArrangements("", updatedBlockSizes);
                } else {
                    count += countArrangements(field.substring(expectedSize + 1), updatedBlockSizes);
                }
            }
        }
        if (current == '.' || current == '?') {
            count += countArrangements(field.substring(1), expectedBlockSizes);
        }

        cache.put(key, count);

        return count;
    }

    public static void main(String[] args) {
        var input = InputLoader.loadInput("day12-input.txt");
        var hotSprings = new HotSprings(input);
        System.out.printf("Part 1: %d\n", hotSprings.part1());
        System.out.printf("Part 2: %d\n", hotSprings.part2());
    }
}

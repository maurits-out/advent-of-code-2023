package nl.mout.aoc2023.day12;

import nl.mout.aoc2023.support.InputLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.stream;

public class HotSprings {

    final List<ConditionRecord> records;

    final Map<CacheKey, Long> cache = new HashMap<>();

    record CacheKey(String field, List<Integer> expectedBlockSizes) {
    }

    HotSprings(String input) {
        this.records = parse(input);
    }

    List<ConditionRecord> parse(String input) {
        return input.lines().map(line -> {
            var parts = line.split("[ ,]");
            var sizes = stream(parts, 1, parts.length).map(Integer::parseInt).toList();
            return new ConditionRecord(parts[0], sizes);
        }).toList();
    }

    long countArrangements(String field, List<Integer> expectedBlockSizes) {
        if (field.isEmpty()) {
            if (expectedBlockSizes.isEmpty()) {
                return 1;
            }
            return 0;
        }

        if (expectedBlockSizes.isEmpty()) {
            if (field.indexOf('#') >= 0) {
                return 0;
            }
            return 1;
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

    record ConditionRecord(String field, List<Integer> sizes) {
    }

    long part1() {
        return records.stream()
                .mapToLong(record -> countArrangements(record.field(), record.sizes()))
                .sum();
    }


    long part2() {
        var extendedRecords = new ArrayList<ConditionRecord>();
        for (ConditionRecord record : records) {
            var extendedField = new StringBuilder();
            var extendedSizes = new ArrayList<Integer>();
            for (var i = 0; i < 5; i++) {
                extendedField.append(record.field).append('?');
                extendedSizes.addAll(record.sizes);
            }
            extendedField.setLength(extendedField.length() - 1);
            extendedRecords.add(new ConditionRecord(extendedField.toString(), extendedSizes));
        }
        return extendedRecords.stream().mapToLong(record -> countArrangements(record.field, record.sizes)).sum();
    }

    public static void main(String[] args) {
        var input = InputLoader.loadInput("day12-input.txt");
        var hotSprings = new HotSprings(input);
        System.out.println("Part 1: " + hotSprings.part1());
        System.out.println("Part 2: " + hotSprings.part2());
    }
}

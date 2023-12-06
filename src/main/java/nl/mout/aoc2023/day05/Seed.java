package nl.mout.aoc2023.day05;

import java.util.*;
import java.util.regex.Pattern;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Arrays.stream;
import static java.util.stream.IntStream.range;
import static nl.mout.aoc2023.support.InputLoader.loadInput;

public class Seed {

    private List<Long> seeds;
    private List<MappingTable> mappingTables;

    private Seed(String input) {
        parse(input);
    }

    private void parse(String input) {
        var fragments = input.split("\n\n");
        this.seeds = Pattern
                .compile("(\\d+)").matcher(fragments[0]).results()
                .map(matchResult -> Long.parseLong(matchResult.group(1)))
                .toList();
        this.mappingTables = range(1, fragments.length)
                .mapToObj(i -> parseMappingTable(fragments[i]))
                .toList();
    }

    private MappingTable parseMappingTable(String fragment) {
        var mappings = fragment.lines()
                .skip(1)
                .map(this::parseMapping)
                .toList();
        return new MappingTable(mappings);
    }

    private Mapping parseMapping(String line) {
        var numbers = stream(line.split("\\s+"))
                .mapToLong(Long::parseLong)
                .toArray();
        return new Mapping(numbers[0], numbers[1], numbers[2]);
    }

    private long part1() {
        return seeds.stream().mapToLong(seed -> {
            var current = seed;
            for (var mappingTable : mappingTables) {
                current = findDestination(current, mappingTable);
            }
            return current;
        }).min().orElseThrow();
    }

    private long part2() {
        Deque<Interval> remainingIntervals = getSeedIntervals();
        for (var mappingTable : mappingTables) {
            remainingIntervals = calculateNextIntervals(mappingTable, remainingIntervals);
        }
        return remainingIntervals.stream().mapToLong(interval -> interval.from).min().orElseThrow();
    }

    private Deque<Interval> calculateNextIntervals(MappingTable mappingTable, Deque<Interval> remainingIntervals) {
        var nextIntervals = new LinkedList<Interval>();
        while (!remainingIntervals.isEmpty()) {
            var interval = remainingIntervals.pop();
            var matchFound = false;
            for (var mapping : mappingTable.mappings) {
                var overlappingInterval = new Interval(max(mapping.sourceStart, interval.from), min(mapping.sourceStart + mapping.length, interval.to));
                if (overlappingInterval.from < overlappingInterval.to) {
                    matchFound = true;
                    nextIntervals.push(new Interval(getDestination(overlappingInterval.from, mapping), getDestination(overlappingInterval.to, mapping)));
                    if (interval.from < overlappingInterval.from) {
                        remainingIntervals.push(new Interval(interval.from, overlappingInterval.from));
                    }
                    if (overlappingInterval.to < interval.to) {
                        remainingIntervals.push(new Interval(overlappingInterval.to, interval.to));
                    }
                }
            }
            if (!matchFound) {
                nextIntervals.push(interval);
            }
        }
        return nextIntervals;
    }

    private Deque<Interval> getSeedIntervals() {
        var intervals = new LinkedList<Interval>();
        for (var i = 0; i < seeds.size(); i += 2) {
            intervals.push(new Interval(seeds.get(i), seeds.get(i) + seeds.get(i + 1)));
        }
        return intervals;
    }

    private long findDestination(long value, MappingTable mappingTable) {
        return mappingTable.mappings.stream()
                .filter(mapping -> isInRange(value, mapping))
                .findFirst()
                .map(mapping -> getDestination(value, mapping))
                .orElse(value);
    }

    private boolean isInRange(long value, Mapping mapping) {
        return mapping.sourceStart <= value && value < mapping.sourceStart + mapping.length;
    }

    private long getDestination(long value, Mapping mapping) {
        return mapping.destStart + (value - mapping.sourceStart);
    }

    public static void main(String[] args) {
        var input = loadInput("day05-input.txt");
        var seed = new Seed(input);
        System.out.println("Part 1: " + seed.part1());
        System.out.println("Part 2: " + seed.part2());
    }

    private record Interval(long from, long to) {
    }

    private record Mapping(long destStart, long sourceStart, long length) {
    }

    private record MappingTable(List<Mapping> mappings) {
    }
}

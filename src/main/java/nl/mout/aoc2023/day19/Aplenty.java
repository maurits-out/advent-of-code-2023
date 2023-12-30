package nl.mout.aoc2023.day19;

import java.util.*;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.System.lineSeparator;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.iterate;
import static nl.mout.aoc2023.support.InputLoader.loadInput;

public class Aplenty {

    private static final Pattern PART_PATTERN = Pattern.compile("[,=]");
    private static final Pattern WORKFLOW_PATTERN = Pattern.compile("[{},]");

    private Map<String, Workflow> workflows;
    private List<Map<String, Integer>> parts;

    public Aplenty(String input) {
        parseInput(input);
    }

    public int part1() {
        return parts.stream()
                .filter(p -> evaluate(p).equals("A"))
                .mapToInt(this::sumPartValues)
                .sum();
    }

    public long part2() {
        var intervals = Map.of(
                "x", new Interval(1, 4001),
                "m", new Interval(1, 4001),
                "a", new Interval(1, 4001),
                "s", new Interval(1, 4001)
        );
        return countCombinations("in", intervals);
    }

    private record Workflow(String name, List<Rule> rules) {
    }

    private record Interval(int from, int to) {
        boolean isValid() {
            return from < to;
        }
    }

    private void parseInput(String input) {
        var sections = input.split(lineSeparator() + lineSeparator());
        this.workflows = sections[0].lines().map(this::parseWorkflow).collect(toMap(Workflow::name, identity()));
        this.parts = sections[1].lines().map(this::parsePart).toList();
    }

    private Map<String, Integer> parsePart(String line) {
        var p = PART_PATTERN.split(line.substring(1, line.length() - 1));
        return iterate(0, i -> i < p.length, i -> i + 2)
                .boxed()
                .collect(toMap(i -> p[i], i -> parseInt(p[i + 1])));
    }

    private Workflow parseWorkflow(String line) {
        var p = WORKFLOW_PATTERN.split(line);
        var name = p[0];
        var rules = new ArrayList<Rule>();
        for (var i = 1; i < p.length; i++) {
            var colonPos = p[i].indexOf(':');
            if (colonPos >= 0) {
                var category = p[i].substring(0, 1);
                var operand = p[i].substring(1, 2);
                var value = parseInt(p[i].substring(2, colonPos));
                var dest = p[i].substring(colonPos + 1);
                rules.add(new Comparison(category, operand, value, dest));
            } else {
                rules.add(new Constant(p[i]));
            }
        }
        return new Workflow(name, rules);
    }

    private String applyWorkflow(Workflow workflow, Map<String, Integer> part) {
        return workflow.rules().stream()
                .map(rule -> rule.evaluate(part))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst().orElseThrow();
    }

    private String evaluate(Map<String, Integer> part) {
        var current = "in";
        while (!current.equals("A") && (!current.equals("R"))) {
            var workflow = workflows.get(current);
            current = applyWorkflow(workflow, part);
        }
        return current;
    }

    private int sumPartValues(Map<String, Integer> part) {
        return part.values().stream().mapToInt(v -> v).sum();
    }

    private Interval applyComparisonToInterval(Comparison c, Interval interval) {
        if (c.operand().equals("<")) {
            return new Interval(interval.from(), min(interval.to(), c.value()));
        }
        return new Interval(max(interval.from(), c.value() + 1), interval.to());
    }

    private Map<String, Interval> updateIntervals(Map<String, Interval> intervals, String category, Interval interval) {
        var updatedIntervals = new HashMap<>(intervals);
        updatedIntervals.put(category, interval);
        return updatedIntervals;
    }

    private long countCombinations(String name, Map<String, Interval> intervals) {
        if (!intervals.values().stream().allMatch(Interval::isValid)) {
            return 0;
        }

        if (name.equals("A")) {
            return countCombinations(intervals);
        }

        if (name.equals("R")) {
            return 0;
        }

        var sum = 0L;

        loop:
        for (var rule : workflows.get(name).rules()) {
            switch (rule) {
                case Constant(var dest) when dest.equals("A") -> {
                    sum += countCombinations(dest, intervals);
                    break loop;
                }
                case Constant(var dest) when dest.equals("R") -> {
                    break loop;
                }
                case Constant(var dest) -> {
                    sum += countCombinations(dest, intervals);
                    break loop;
                }
                case Comparison c -> {
                    var interval = applyComparisonToInterval(c, intervals.get(c.category()));
                    sum += countCombinations(c.dest(), updateIntervals(intervals, c.category(), interval));
                    interval = applyComparisonToInterval(c.negate(), intervals.get(c.category()));
                    intervals = updateIntervals(intervals, c.category(), interval);
                }
            }
        }
        return sum;
    }

    private long countCombinations(Map<String, Interval> intervals) {
        return intervals.values().stream()
                .mapToLong(i -> i.to() - i.from())
                .reduce(1, (a, b) -> a * b);
    }

    public static void main(String[] args) {
        var input = loadInput("day19-input.txt");
        var aplenty = new Aplenty(input);
        System.out.printf("Part 1: %d\n", aplenty.part1());
        System.out.printf("Part 2: %d\n", aplenty.part2());
    }
}

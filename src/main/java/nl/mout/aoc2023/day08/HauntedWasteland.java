package nl.mout.aoc2023.day08;

import org.apache.commons.math3.util.ArithmeticUtils;

import java.util.Map;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toMap;
import static nl.mout.aoc2023.support.InputLoader.loadInput;

public class HauntedWasteland {

    private final char[] instructions;
    private final Map<String, String[]> nodes;

    public HauntedWasteland(String input) {
        instructions = input.lines().findFirst().orElseThrow().toCharArray();
        nodes = input.lines().skip(2).collect(toMap(
                line -> line.substring(0, 3),
                line -> new String[]{line.substring(7, 10), line.substring(12, 15)}
        ));
    }

    public int part1() {
        return countSteps("AAA", node -> node.equals("ZZZ"));
    }

    public long part2() {
        return nodes.keySet().stream()
                .filter(n -> n.endsWith("A"))
                .map(start -> Long.valueOf(countSteps(start, node -> node.endsWith("Z"))))
                .reduce(ArithmeticUtils::lcm).orElseThrow();
    }

    private int countSteps(String start, Predicate<String> stop) {
        int ip = 0, steps = 0;
        var current = start;
        while (!stop.test(current)) {
            var elements = nodes.get(current);
            current = (instructions[ip] == 'L') ? elements[0] : elements[1];
            ip = (ip < instructions.length - 1) ? ip + 1 : 0;
            steps++;
        }
        return steps;
    }

    public static void main(String[] args) {
        var input = loadInput("day08-input.txt");
        var hauntedWasteland = new HauntedWasteland(input);
        System.out.printf("Part 1: %d\n", hauntedWasteland.part1());
        System.out.printf("Part 2: %d\n", hauntedWasteland.part2());
    }
}

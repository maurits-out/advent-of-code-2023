package nl.mout.aoc2023.day08;

import java.util.Map;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toMap;
import static nl.mout.aoc2023.support.InputLoader.loadInput;

public class HauntedWasteland {

    final char[] instructions;
    final Map<String, String[]> nodes;

    HauntedWasteland(String input) {
        instructions = input.lines().findFirst().orElseThrow().toCharArray();
        nodes = input.lines().skip(2).collect(toMap(
                line -> line.substring(0, 3),
                line -> new String[]{line.substring(7, 10), line.substring(12, 15)}
        ));
    }

    int countSteps(String start, Predicate<String> stop) {
        int ip = 0, steps = 0;
        var current = start;
        while (!stop.test(current)) {
            current = (instructions[ip] == 'L') ? nodes.get(current)[0] : nodes.get(current)[1];
            ip = (ip < instructions.length - 1) ? ip + 1 : 0;
            steps++;
        }
        return steps;
    }

    long lcm(long a, long b) {
        return (a * b) / gcd(a, b);
    }

    long gcd(long a, long b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }

    int part1() {
        return countSteps("AAA", node -> node.equals("ZZZ"));
    }

    long part2() {
        return nodes.keySet().stream()
                .filter(n -> n.endsWith("A"))
                .map(start -> Long.valueOf(countSteps(start, node -> node.endsWith("Z"))))
                .reduce(this::lcm).orElseThrow();
    }

    public static void main(String[] args) {
        var input = loadInput("day08-input.txt");
        var hauntedWasteland = new HauntedWasteland(input);
        System.out.println("Part 1: " + hauntedWasteland.part1());
        System.out.println("Part 2: " + hauntedWasteland.part2());
    }
}

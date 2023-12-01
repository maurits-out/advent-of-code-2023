package nl.mout.aoc2023.day01;

import nl.mout.aoc2023.support.InputLoader;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class Trebuchet {

    private final String input;

    public Trebuchet(String input) {
        this.input = input;
    }

    public int part1() {
        return calculateSum(createDigitMapping());
    }

    public int part2() {
        var mapping = createDigitMapping();
        extendMappingWithSpelledOutDigits(mapping);
        return calculateSum(mapping);
    }

    private Map<String, Integer> createDigitMapping() {
        return IntStream.range(0, 10)
                .boxed()
                .collect(toMap(String::valueOf, identity()));
    }

    private void extendMappingWithSpelledOutDigits(Map<String, Integer> mapping) {
        mapping.put("one", 1);
        mapping.put("two", 2);
        mapping.put("three", 3);
        mapping.put("four", 4);
        mapping.put("five", 5);
        mapping.put("six", 6);
        mapping.put("seven", 7);
        mapping.put("eight", 8);
        mapping.put("nine", 9);
    }

    private int calculateSum(Map<String, Integer> mapping) {
        return input.lines()
                .mapToInt(line -> extractCalibrationValue(line, mapping))
                .sum();
    }

    private int extractCalibrationValue(String line, Map<String, Integer> mapping) {
        var digits = new ArrayList<Integer>();
        for (var i = 0; i < line.length(); i++) {
            var s = line.substring(i);
            mapping.keySet()
                    .stream()
                    .filter(s::startsWith)
                    .findFirst()
                    .ifPresent(key -> digits.add(mapping.get(key)));
        }
        return (digits.getFirst() * 10) + digits.getLast();
    }

    public static void main(String[] args) {
        var input = InputLoader.loadInput("day01-input.txt");
        var trebuchet = new Trebuchet(input);
        System.out.printf("Part 1: %d\n", trebuchet.part1());
        System.out.printf("Part 2: %d\n", trebuchet.part2());
    }
}

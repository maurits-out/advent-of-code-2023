package nl.mout.aoc2023.day09;

import java.util.List;
import java.util.StringTokenizer;
import java.util.function.BiFunction;

import static java.util.Collections.list;
import static java.util.stream.IntStream.range;
import static nl.mout.aoc2023.support.InputLoader.loadInput;

public class MirageMaintenance {

    private final List<List<Integer>> lines;

    MirageMaintenance(String input) {
        lines = input.lines().map(this::parseLine).toList();
    }

    List<Integer> parseLine(String line) {
        return list(new StringTokenizer(line, " ")).stream()
                .map(token -> Integer.parseInt((String) token))
                .toList();
    }

    boolean allZero(List<Integer> numbers) {
        return numbers.stream().allMatch(n -> n == 0);
    }

    List<Integer> calculateDiffs(List<Integer> numbers) {
        return range(0, numbers.size() - 1)
                .mapToObj(i -> numbers.get(i + 1) - numbers.get(i))
                .toList();
    }

    int extrapolate(List<Integer> numbers, BiFunction<List<Integer>, Integer, Integer> placeholderFunction) {
        if (allZero(numbers)) {
            return 0;
        }
        var diffs = calculateDiffs(numbers);
        return placeholderFunction.apply(numbers, extrapolate(diffs, placeholderFunction));
    }

    int extrapolateAndSum(BiFunction<List<Integer>, Integer, Integer> placeholderFunction) {
        return lines.stream().mapToInt(numbers -> extrapolate(numbers, placeholderFunction)).sum();
    }

    int part1() {
        return extrapolateAndSum((row, placeholder) -> row.getLast() + placeholder);
    }

    int part2() {
        return extrapolateAndSum((row, placeholder) -> row.getFirst() - placeholder);
    }

    public static void main(String[] args) {
        var input = loadInput("day09-input.txt");
        var mirageMaintenance = new MirageMaintenance(input);
        System.out.println("Part 1: " + mirageMaintenance.part1());
        System.out.println("Part 2: " + mirageMaintenance.part2());
    }
}

package nl.mout.aoc2023.day13;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;

import static java.lang.Math.min;
import static java.util.Arrays.stream;
import static java.util.stream.IntStream.range;
import static nl.mout.aoc2023.support.InputLoader.loadInput;

public class PointOfIncidence {

    private final List<List<String>> patterns;

    PointOfIncidence(String input) {
        patterns = stream(input.split("\n\n"))
                .map(part -> Arrays.asList(part.split("\n")))
                .toList();
    }

    Optional<Integer> getReflectionRow(List<String> pattern, BiPredicate<List<String>, List<String>> match) {
        return range(1, pattern.size()).filter(r -> {
            var above = pattern.subList(0, r).reversed();
            var below = pattern.subList(r, pattern.size());
            var min = min(above.size(), below.size());
            if (above.size() > min) {
                above = above.subList(0, min);
            } else if (below.size() > min) {
                below = below.subList(0, min);
            }
            return match.test(above, below);
        }).boxed().findFirst();
    }

    Optional<Integer> getReflectionColumn(List<String> pattern, BiPredicate<List<String>, List<String>> match) {
        var transposed = transpose(pattern);
        return getReflectionRow(transposed, match);
    }

    private List<String> transpose(List<String> pattern) {
        var transposed = range(0, pattern.getFirst().length())
                .mapToObj(c -> new StringBuilder())
                .toList();
        range(0, pattern.getFirst().length()).forEach(c ->
                range(0, pattern.size()).forEach(r ->
                        transposed.get(c).append(pattern.get(r).charAt(c))
                )
        );
        return transposed.stream().map(StringBuilder::toString).toList();
    }

    int getReflectionScore(List<String> pattern, BiPredicate<List<String>, List<String>> match) {
        return getReflectionRow(pattern, match)
                .map(r -> r * 100)
                .orElseGet(() -> getReflectionColumn(pattern, match).orElseThrow());
    }

    int part1() {
        return patterns.stream()
                .mapToInt(pattern -> getReflectionScore(pattern, List::equals))
                .sum();
    }

    int part2() {
        BiPredicate<List<String>, List<String>> match = (above, below) -> {
            var smudges = 0;
            for (var i = 0; i < above.size(); i++) {
                var line1 = above.get(i);
                var line2 = below.get(i);
                for (var j = 0; j < line1.length(); j++) {
                    if (line1.charAt(j) != line2.charAt(j)) {
                        smudges++;
                    }
                }
            }
            return smudges == 1;
        };

        return patterns.stream()
                .mapToInt(pattern -> getReflectionScore(pattern, match))
                .sum();
    }

    public static void main(String[] args) {
        var input = loadInput("day13-input.txt");
        var pointOfIncidence = new PointOfIncidence(input);
        System.out.println("Part 1: " + pointOfIncidence.part1());
        System.out.println("Part 2: " + pointOfIncidence.part2());
    }
}

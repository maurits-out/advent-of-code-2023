package nl.mout.aoc2023.day03;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import static java.lang.Character.isDigit;
import static java.lang.Integer.parseInt;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.stream.IntStream.rangeClosed;
import static nl.mout.aoc2023.support.InputLoader.loadInput;

public class GearRatios {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");
    private final String[] schemaLines;
    private final int width;

    private GearRatios(String input) {
        schemaLines = input.lines().toArray(String[]::new);
        width = schemaLines[0].length();
    }

    private int part1() {
        var sum = 0;
        for (var row = 0; row < schemaLines.length; row++) {
            var line = schemaLines[row];
            var matcher = NUMBER_PATTERN.matcher(line);
            while (matcher.find()) {
                var number = parseInt(matcher.group());
                var adjacentSymbols = getAdjacentSymbols(row, matcher.start(), matcher.end());
                if (!adjacentSymbols.isEmpty()) {
                    sum += number;
                }
            }
        }
        return sum;
    }

    private int part2() {
        var numbersByGear = new HashMap<AdjacentSymbol, List<Integer>>();
        for (var row = 0; row < schemaLines.length; row++) {
            var line = schemaLines[row];
            var matcher = NUMBER_PATTERN.matcher(line);
            while (matcher.find()) {
                var number = parseInt(matcher.group());
                getAdjacentSymbols(row, matcher.start(), matcher.end()).stream()
                        .filter(AdjacentSymbol::isGear)
                        .forEach(as -> numbersByGear.compute(as, (key, numbers) -> {
                            if (numbers == null) {
                                return new ArrayList<>(List.of(number));
                            } else {
                                numbers.add(number);
                                return numbers;
                            }
                        }));
            }
        }
        return numbersByGear.values().stream().filter(numbers -> numbers.size() == 2).mapToInt(numbers -> numbers.getFirst() * numbers.getLast()).sum();
    }

    private List<AdjacentSymbol> getAdjacentSymbols(int row, int start, int end) {
        var symbols = new ArrayList<AdjacentSymbol>();
        if (row > 0) {
            rangeClosed(max(start - 1, 0), min(end, width - 1)).forEach(column -> {
                var ch = schemaLines[row - 1].charAt(column);
                if (isSymbol(ch)) {
                    symbols.add(new AdjacentSymbol(row - 1, column, ch));
                }
            });
        }
        if (row < schemaLines.length - 1) {
            rangeClosed(max(start - 1, 0), min(end, width - 1)).forEach(column -> {
                var ch = schemaLines[row + 1].charAt(column);
                if (isSymbol(ch)) {
                    symbols.add(new AdjacentSymbol(row + 1, column, ch));
                }
            });
        }
        if (start > 0) {
            var ch = schemaLines[row].charAt(start - 1);
            if (isSymbol(ch)) {
                symbols.add(new AdjacentSymbol(row, start - 1, ch));
            }
        }
        if (end < width) {
            char ch = schemaLines[row].charAt(end);
            if (isSymbol(ch)) {
                symbols.add(new AdjacentSymbol(row, end, ch));
            }
        }
        return symbols;
    }

    private static boolean isSymbol(char ch) {
        return !isDigit(ch) && ch != '.';
    }

    private record AdjacentSymbol(int row, int column, char symbol) {
        boolean isGear() {
            return symbol == '*';
        }
    }

    public static void main(String[] args) {
        var input = loadInput("day03-input.txt");
        var gearRatios = new GearRatios(input);
        System.out.printf("Part 1: %d\n", gearRatios.part1());
        System.out.printf("Part 2: %d\n", gearRatios.part2());
    }
}

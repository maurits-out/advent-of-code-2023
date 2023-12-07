package nl.mout.aoc2023.day04;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.lang.Integer.parseInt;
import static java.lang.Math.pow;
import static java.lang.Math.round;
import static java.util.Arrays.fill;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;
import static nl.mout.aoc2023.support.InputLoader.loadInput;

public class Scratchcards {

    final Map<Integer, Integer> matchingNumberCountById;

    public Scratchcards(String input) {
        this.matchingNumberCountById = parseInput(input);
    }

    Map<Integer, Integer> parseInput(String input) {
        var result = new HashMap<Integer, Integer>();
        input.lines().forEach(line -> {
            var parts = line.split("[:|]");
            var cardId = parseInt(parts[0].split("\\s+")[1]);
            var winningNumbers = parseNumberSequence(parts[1]);
            var haveNumbers = parseNumberSequence(parts[2]);
            var matchingNumberCount = (int) haveNumbers.stream().filter(winningNumbers::contains).count();
            result.put(cardId, matchingNumberCount);
        });
        return result;
    }

    Set<Integer> parseNumberSequence(String part) {
        return stream(part.trim().split("\\s+"))
                .map(Integer::parseInt)
                .collect(toSet());
    }

    int part1() {
        return matchingNumberCountById.values().stream()
                .mapToInt(count -> count == 0 ? 0 : (int) round(pow(2, count - 1)))
                .sum();
    }

    int part2() {
        var countPerCard = new int[matchingNumberCountById.size() + 1];
        fill(countPerCard, 1);

        var total = 0;
        for (var i = 1; i <= matchingNumberCountById.size(); i++) {
            total += countPerCard[i];
            for (var j = i + 1; j <= i + matchingNumberCountById.get(i); j++) {
                countPerCard[j] += countPerCard[i];
            }
        }
        return total;
    }

    public static void main(String[] args) {
        var input = loadInput("day04-input.txt");
        var scratchcards = new Scratchcards(input);
        System.out.println("Part 1: " + scratchcards.part1());
        System.out.println("Part 2: " + scratchcards.part2());
    }
}

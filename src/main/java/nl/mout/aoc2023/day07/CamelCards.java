package nl.mout.aoc2023.day07;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;
import static java.util.stream.IntStream.range;
import static nl.mout.aoc2023.day07.CamelCards.HandType.*;
import static nl.mout.aoc2023.support.InputLoader.loadInput;

public class CamelCards {

    private final List<Hand> hands;

    public CamelCards(String input) {
        hands = input.lines()
                .map(this::parseLine)
                .toList();
    }

    public int part1() {
        Comparator<Hand> byType = Comparator.comparing(hand -> hand.typePart1);
        var byStrongestCard = new StrongestCardComparator('2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A');
        return countTotalWinnings(byType, byStrongestCard);
    }

    public int part2() {
        Comparator<Hand> byType = Comparator.comparing(hand -> hand.typePart2);
        var byStrongestCard = new StrongestCardComparator('J', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'Q', 'K', 'A');
        var sorted = hands.stream()
                .sorted(byType.thenComparing(byStrongestCard))
                .toArray(Hand[]::new);
        return range(0, sorted.length)
                .map(index -> (index + 1) * sorted[index].bid)
                .sum();
    }


    private int countTotalWinnings(Comparator<Hand> byType, StrongestCardComparator byStrongestCard) {
        var sorted = hands.stream()
                .sorted(byType.thenComparing(byStrongestCard))
                .toArray(Hand[]::new);
        return range(0, sorted.length)
                .map(index -> (index + 1) * sorted[index].bid)
                .sum();
    }

    private record Hand(String cards, int bid, HandType typePart1, HandType typePart2) {
    }

    enum HandType {
        HIGH_CARD, ONE_PAIR, TWO_PAIR, THREE_OF_A_KIND, FULL_HOUSE, FOUR_OF_A_KIND, FIVE_OF_A_KIND
    }

    private Hand parseLine(String line) {
        var components = line.split(" ");
        return new Hand(components[0],
                parseInt(components[1]),
                getHandTypeWithoutJoker(components[0]),
                getHandTypeWithJoker(components[0])
        );
    }

    private HandType getHandTypeWithoutJoker(String cards) {
        var counts = cards.chars()
                .boxed()
                .collect(groupingBy(ch -> (char) ch.intValue(), counting()))
                .values().stream()
                .collect(groupingBy(Long::intValue, counting()));

        HandType type;
        if (counts.getOrDefault(5, 0L) == 1) {
            type = FIVE_OF_A_KIND;
        } else if (counts.getOrDefault(4, 0L) == 1) {
            type = FOUR_OF_A_KIND;
        } else if (counts.getOrDefault(3, 0L) == 1) {
            if (counts.getOrDefault(2, 0L) == 1) {
                type = FULL_HOUSE;
            } else {
                type = THREE_OF_A_KIND;
            }
        } else {
            type = switch (counts.getOrDefault(2, 0L).intValue()) {
                case 1 -> ONE_PAIR;
                case 2 -> TWO_PAIR;
                default -> HIGH_CARD;
            };
        }
        return type;
    }

    private HandType getHandTypeWithJoker(String cards) {
        var countsExcludingJoker = cards.chars()
                .filter(ch -> ch != 'J')
                .boxed()
                .collect(groupingBy(ch -> (char) ch.intValue(), counting()))
                .values().stream()
                .collect(groupingBy(Long::intValue, counting()));
        var jokerCount = cards.chars().filter(ch -> ch == 'J').count();

        HandType type;
        if (jokerCount >= 4) {
            type = FIVE_OF_A_KIND;
        } else if (jokerCount == 3) {
            if (countsExcludingJoker.getOrDefault(2, 0L) == 1) {
                type = FIVE_OF_A_KIND;
            } else {
                type = FOUR_OF_A_KIND;
            }
        } else if (jokerCount == 2) {
            if (countsExcludingJoker.getOrDefault(3, 0L) == 1) {
                type = FIVE_OF_A_KIND;
            } else if (countsExcludingJoker.getOrDefault(2, 0L) == 1) {
                type = FOUR_OF_A_KIND;
            } else {
                type = THREE_OF_A_KIND;
            }
        } else if (jokerCount == 1) {
            if (countsExcludingJoker.getOrDefault(4, 0L) == 1) {
                type = FIVE_OF_A_KIND;
            } else if (countsExcludingJoker.getOrDefault(3, 0L) == 1) {
                type = FOUR_OF_A_KIND;
            } else if (countsExcludingJoker.getOrDefault(2, 0L) == 2) {
                type = FULL_HOUSE;
            } else if (countsExcludingJoker.getOrDefault(2, 0L) == 1) {
                type = THREE_OF_A_KIND;
            } else {
                type = ONE_PAIR;
            }
        } else {
            type = getHandTypeWithoutJoker(cards);
        }
        return type;
    }

    private static class StrongestCardComparator implements Comparator<Hand> {

        private final Map<Character, Integer> indexByLabel;

        StrongestCardComparator(char... labels) {
            this.indexByLabel = range(0, labels.length)
                    .boxed()
                    .collect(toMap(idx -> labels[idx], identity()));
        }

        @Override
        public int compare(Hand hand1, Hand hand2) {
            var index = range(0, hand1.cards.length())
                    .filter(i -> hand1.cards.charAt(i) != hand2.cards.charAt(i))
                    .findFirst().orElseThrow();
            var label1 = hand1.cards.charAt(index);
            var label2 = hand2.cards.charAt(index);
            return indexByLabel.get(label1).compareTo(indexByLabel.get(label2));
        }
    }

    public static void main(String[] args) {
        var input = loadInput("day07-input.txt");
        var camelCards = new CamelCards(input);
        System.out.printf("Part 1: %d\n", camelCards.part1());
        System.out.printf("Part 2: %d\n", camelCards.part2());
    }
}

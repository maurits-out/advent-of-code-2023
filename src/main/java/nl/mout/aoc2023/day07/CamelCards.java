package nl.mout.aoc2023.day07;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;
import static java.util.stream.IntStream.range;
import static nl.mout.aoc2023.day07.CamelCards.HandType.*;
import static nl.mout.aoc2023.support.InputLoader.loadInput;

public class CamelCards {

    final List<Hand> hands;

    CamelCards(String input) {
        hands = input.lines()
                .map(this::parseLine)
                .toList();
    }

    Hand parseLine(String line) {
        var components = line.split(" ");
        return new Hand(components[0], Integer.parseInt(components[1]), getHandTypeWithoutJoker(components[0]), getHandTypeWithJoker(components[0]));
    }

    HandType getHandTypeWithoutJoker(String cards) {
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


    HandType getHandTypeWithJoker(String cards) {
        var counts = cards.chars()
                .filter(ch -> ch != 'J')
                .boxed()
                .collect(groupingBy(ch -> (char) ch.intValue(), counting()))
                .values().stream()
                .collect(groupingBy(Long::intValue, counting()));
        var jokerCount = (int) cards.chars().filter(ch -> ch == 'J').count();

        HandType type;
        if (jokerCount >= 4) {
            type = FIVE_OF_A_KIND;
        } else if (jokerCount == 3) {
            if (counts.getOrDefault(2, 0L) == 1) {
                type = FIVE_OF_A_KIND;
            } else {
                type = FOUR_OF_A_KIND;
            }
        } else if (jokerCount == 2) {
            if (counts.getOrDefault(3, 0L) == 1) {
                type = FIVE_OF_A_KIND;
            } else if (counts.getOrDefault(2, 0L) == 1) {
                type = FOUR_OF_A_KIND;
            } else {
                type = THREE_OF_A_KIND;
            }
        } else if (jokerCount == 1) {
            if (counts.getOrDefault(4, 0L) == 1) {
                type = FIVE_OF_A_KIND;
            } else if (counts.getOrDefault(3, 0L) == 1) {
                type = FOUR_OF_A_KIND;
            } else if (counts.getOrDefault(2, 0L) == 2) {
                type = FULL_HOUSE;
            } else if (counts.getOrDefault(2, 0L) == 1) {
                type = THREE_OF_A_KIND;
            } else {
                type = ONE_PAIR;
            }
        } else {
            type = getHandTypeWithoutJoker(cards);
        }
        return type;
    }

    static class StrongestCardComparator implements Comparator<Hand> {

        private final Map<Character, Integer> indexByLabel;

        StrongestCardComparator(char... labels) {
            this.indexByLabel = range(0, labels.length).boxed().collect(toUnmodifiableMap(
                idx -> labels[idx], identity()
            ));
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

    record Hand(String cards, int bid, HandType typePart1, HandType typePart2) {
    }

    enum HandType {
        HIGH_CARD, ONE_PAIR, TWO_PAIR, THREE_OF_A_KIND, FULL_HOUSE, FOUR_OF_A_KIND, FIVE_OF_A_KIND
    }

    int part1() {
        var byStrongestCard = new StrongestCardComparator('2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A');

        var sorted = hands.stream()
                .sorted(Comparator.<Hand, HandType>comparing(hand -> hand.typePart1).thenComparing(byStrongestCard))
                .toArray(Hand[]::new);
        return range(0, sorted.length)
                .map(index -> (index + 1) * sorted[index].bid)
                .sum();
    }

    int part2() {
        var byStrongestCard = new StrongestCardComparator('J', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'Q', 'K', 'A');

        var sorted = hands.stream()
                .sorted(Comparator.<Hand, HandType>comparing(hand -> hand.typePart2).thenComparing(byStrongestCard))
                .toArray(Hand[]::new);
        return range(0, sorted.length)
                .map(index -> (index + 1) * sorted[index].bid)
                .sum();
    }

    public static void main(String[] args) {
        var input = loadInput("day07-input.txt");
        var camelCards = new CamelCards(input);
        System.out.println("Part 1: " + camelCards.part1());
        System.out.println("Part 2: " + camelCards.part2());
    }
}

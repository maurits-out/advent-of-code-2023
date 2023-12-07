package nl.mout.aoc2023.day06;

import java.util.List;

import static java.util.stream.LongStream.iterate;

public class WaitForIt {

    long part1(List<Race> races) {
        return races.stream()
                .mapToLong(this::countSuccessfulAttempt)
                .reduce(1L, (a, b) -> a * b);
    }

    long part2(Race race) {
        return countSuccessfulAttempt(race);
    }

    long countSuccessfulAttempt(Race race) {
        var start = iterate(2, buttonTime -> buttonTime + 1)
                .filter(buttonTime -> calculateDistance(race.time, buttonTime) > race.distance)
                .findFirst().orElseThrow();
        var end = race.time - start;
        return (end - start) + 1;
    }

    long calculateDistance(long raceTime, long buttonTime) {
        return (raceTime - buttonTime) * buttonTime;
    }

    record Race(long time, long distance) {
    }

    public static void main(String[] args) {
        var inputPart1 = List.of(new Race(40, 233), new Race(82, 1011), new Race(84, 1110), new Race(92, 1487));
        var inputPart2 = new Race(40828492, 233101111101487L);

        var waitForIt = new WaitForIt();
        System.out.println("Part 1: " + waitForIt.part1(inputPart1));
        System.out.println("Part 2: " + waitForIt.part2(inputPart2));
    }
}

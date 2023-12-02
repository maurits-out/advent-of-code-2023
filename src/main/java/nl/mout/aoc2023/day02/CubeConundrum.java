package nl.mout.aoc2023.day02;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;
import static nl.mout.aoc2023.support.InputLoader.loadInput;

public class CubeConundrum {

    private final List<Game> games;

    private CubeConundrum(String input) {
        this.games = input.lines().map(this::parse).toList();
    }

    private int part1() {
        return games.stream()
                .filter(this::supportsConfiguration)
                .mapToInt(Game::id)
                .sum();
    }

    private int part2() {
        return games.stream()
                .mapToInt(this::powerOfFewestNumberOfCubes)
                .sum();
    }

    private int powerOfFewestNumberOfCubes(Game game) {
        var sets = game.sets();
        var red = sets.stream().mapToInt(GameSet::red).max().orElse(1);
        var green = sets.stream().mapToInt(GameSet::green).max().orElse(1);
        var blue = sets.stream().mapToInt(GameSet::blue).max().orElse(1);
        return red * green * blue;
    }

    private boolean supportsConfiguration(Game game) {
        return game.sets().stream()
                .allMatch(set -> set.red() <= 12 && set.green() <= 13 && set.blue() <= 14);
    }

    private Game parse(String line) {
        var parts = line.split("[:;] ");
        var gameId = parseInt(parts[0].split(" ")[1]);
        var gameSets = new ArrayList<GameSet>();
        for (int i = 1; i < parts.length; i++) {
            int red = 0, green = 0, blue = 0;
            var draws = parts[i].split(",? ");
            for (int j = 0; j < draws.length; j += 2) {
                var amount = parseInt(draws[j]);
                switch (draws[j + 1]) {
                    case "red":
                        red = amount;
                        break;
                    case "blue":
                        blue = amount;
                        break;
                    case "green":
                        green = amount;
                }
            }
            gameSets.add(new GameSet(red, blue, green));
        }
        return new Game(gameId, gameSets);
    }

    private record Game(int id, List<GameSet> sets) {
    }

    private record GameSet(int red, int blue, int green) {
    }

    public static void main(String[] args) {
        var input = loadInput("day02-input.txt");
        var cubeConundrum = new CubeConundrum(input);
        System.out.printf("Part 1: %d\n", cubeConundrum.part1());
        System.out.printf("Part 2: %d\n", cubeConundrum.part2());
    }
}

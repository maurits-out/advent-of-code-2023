package nl.mout.aoc2023.day16;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static java.util.stream.IntStream.range;
import static nl.mout.aoc2023.day16.LavaFloor.Direction.*;
import static nl.mout.aoc2023.support.InputLoader.loadInput;

public class LavaFloor {

    private final char[][] layout;

    public LavaFloor(String input) {
        this.layout = input.lines().map(String::toCharArray).toArray(char[][]::new);
    }

    public long part1() {
        var start = new Beam(new Location(0, 0), EAST);
        return energize(start);
    }

    public long part2() {
        var beams = new ArrayList<Beam>();
        range(0, layout[0].length).forEach(c -> {
            beams.add(new Beam(new Location(0, c), SOUTH));
            beams.add(new Beam(new Location(layout.length - 1, c), NORTH));
        });
        range(0, layout.length).forEach(r -> {
            beams.add(new Beam(new Location(r, 0), EAST));
            beams.add(new Beam(new Location(r, layout[r].length - 1), WEST));
        });
        return beams.stream().mapToLong(this::energize).max().orElseThrow();
    }

    private record Location(int row, int column) {
    }

    enum Direction {
        NORTH, EAST, SOUTH, WEST
    }

    private record Beam(Location location, Direction direction) {
    }

    private List<Beam> moveForward(Beam beam) {
        var location = beam.location();
        var direction = beam.direction();
        var result = switch (direction) {
            case NORTH -> new Beam(new Location(location.row() - 1, location.column()), NORTH);
            case EAST -> new Beam(new Location(location.row(), location.column() + 1), EAST);
            case SOUTH -> new Beam(new Location(location.row() + 1, location.column()), SOUTH);
            case WEST -> new Beam(new Location(location.row(), location.column() - 1), WEST);
        };
        return List.of(result);
    }

    private List<Beam> moveMirrorSlash(Beam beam) {
        var location = beam.location();
        var result = switch (beam.direction()) {
            case NORTH -> new Beam(new Location(location.row(), location.column() + 1), EAST);
            case EAST -> new Beam(new Location(location.row() - 1, location.column()), NORTH);
            case SOUTH -> new Beam(new Location(location.row(), location.column() - 1), WEST);
            case WEST -> new Beam(new Location(location.row() + 1, location.column()), SOUTH);
        };
        return List.of(result);
    }

    private List<Beam> moveMirrorBackslash(Beam beam) {
        var location = beam.location();
        var result = switch (beam.direction()) {
            case NORTH -> new Beam(new Location(location.row(), location.column() - 1), WEST);
            case EAST -> new Beam(new Location(location.row() + 1, location.column()), SOUTH);
            case SOUTH -> new Beam(new Location(location.row(), location.column() + 1), EAST);
            case WEST -> new Beam(new Location(location.row() - 1, location.column()), NORTH);
        };
        return List.of(result);
    }

    private List<Beam> splitHyphen(Beam beam) {
        var location = beam.location();
        var direction = beam.direction();
        return switch (direction) {
            case NORTH, SOUTH -> List.of(
                    new Beam(new Location(location.row(), location.column() + 1), EAST),
                    new Beam(new Location(location.row(), location.column() - 1), WEST)
            );
            case EAST -> List.of(new Beam(new Location(location.row(), location.column() + 1), direction));
            case WEST -> List.of(new Beam(new Location(location.row(), location.column() - 1), direction));
        };
    }

    private List<Beam> splitPipe(Beam beam) {
        var location = beam.location();
        var direction = beam.direction();
        return switch (direction) {
            case NORTH -> List.of(new Beam(new Location(location.row() - 1, location.column()), direction));
            case SOUTH -> List.of(new Beam(new Location(location.row() + 1, location.column()), direction));
            case EAST, WEST -> List.of(
                    new Beam(new Location(location.row() - 1, location.column()), NORTH),
                    new Beam(new Location(location.row() + 1, location.column()), SOUTH)
            );
        };
    }

    private boolean isOnGrid(Beam beam) {
        return 0 <= beam.location().row() && beam.location().row() < layout.length &&
                0 <= beam.location().column() && beam.location().column() < layout[0].length;
    }

    private long energize(Beam start) {
        var beams = new LinkedList<>(List.of(start));
        var history = new HashSet<Beam>();
        while (!beams.isEmpty()) {
            var beam = beams.pop();
            while (isOnGrid(beam) && !history.contains(beam)) {
                history.add(beam);
                char ch = layout[beam.location().row()][beam.location().column()];
                var newBeams = switch (ch) {
                    case '.' -> moveForward(beam);
                    case '/' -> moveMirrorSlash(beam);
                    case '\\' -> moveMirrorBackslash(beam);
                    case '-' -> splitHyphen(beam);
                    case '|' -> splitPipe(beam);
                    default -> throw new IllegalStateException("Unsupported character in layout: " + ch);
                };
                beam = newBeams.getFirst();
                if (newBeams.size() > 1) {
                    beams.push(newBeams.getLast());
                }
            }
        }
        return history.stream().map(Beam::location).distinct().count();
    }

    public static void main(String[] args) {
        var input = loadInput("day16-input.txt");
        var lavaFloor = new LavaFloor(input);
        System.out.printf("Part 1: %d\n", lavaFloor.part1());
        System.out.printf("Part 2: %d\n", lavaFloor.part2());
    }
}

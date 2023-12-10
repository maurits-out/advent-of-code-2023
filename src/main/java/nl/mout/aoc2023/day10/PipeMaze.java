package nl.mout.aoc2023.day10;

import java.util.HashSet;
import java.util.Set;

import static java.util.stream.IntStream.range;
import static nl.mout.aoc2023.day10.PipeMaze.Direction.*;
import static nl.mout.aoc2023.support.InputLoader.loadInput;

public class PipeMaze {

    private final char[][] maze;
    private final int width;
    private final int height;

    PipeMaze(String input) {
        this.maze = input.lines().map(String::toCharArray).toArray(char[][]::new);
        this.height = maze.length;
        this.width = maze[0].length;
    }

    Coordinate findStart() {
        return range(0, height)
                .boxed()
                .flatMap(r -> range(0, width)
                        .boxed()
                        .map(c -> new Coordinate(r, c)))
                .filter(p -> maze[p.row][p.column] == 'S')
                .findFirst().orElseThrow();
    }

    Coordinate move(Coordinate current, Direction direction) {
        return switch (direction) {
            case NORTH -> new Coordinate(current.row - 1, current.column);
            case SOUTH -> new Coordinate(current.row + 1, current.column);
            case WEST -> new Coordinate(current.row, current.column - 1);
            case EAST -> new Coordinate(current.row, current.column + 1);
        };
    }

    Direction updateHeading(Coordinate coordinate, Direction direction) {
        return switch (maze[coordinate.row][coordinate.column]) {
            case 'F' -> (direction == WEST) ? SOUTH : EAST;
            case '7' -> (direction == EAST) ? SOUTH : WEST;
            case 'J' -> (direction == SOUTH) ? WEST : NORTH;
            case 'L' -> (direction == WEST) ? NORTH : EAST;
            default -> direction;
        };
    }

    Set<Coordinate> getLoopCoordinates() {
        var coordinates = new HashSet<Coordinate>();
        var current = findStart();
        var direction = SOUTH;

        do {
            current = move(current, direction);
            direction = updateHeading(current, direction);
            coordinates.add(current);
        } while (maze[current.row][current.column] != 'S');

        return coordinates;
    }

    int part1() {
        return getLoopCoordinates().size() / 2;
    }

    int part2() {
        var loopCoordinates = getLoopCoordinates();
        var enclosedTileCount = 0;

        // replace S
        var start = findStart();
        maze[start.row][start.column] = '7';

        for (var r = 0; r < height; r++) {
            var outsideLoop = true;
            var temp = false;
            for (var c = 0; c < width; c++) {
                char current = maze[r][c];
                if (loopCoordinates.contains(new Coordinate(r, c))) {
                    if (current == '|') {
                        outsideLoop = !outsideLoop;
                    } else if (current == 'F') {
                        temp = true;
                    } else if (current == 'L') {
                        temp = false;
                    } else if (current == '7' && !temp) {
                        outsideLoop = !outsideLoop;
                    } else if (current == 'J' && temp) {
                        outsideLoop = !outsideLoop;
                    }
                } else if (!outsideLoop) {
                    enclosedTileCount++;
                }
            }
        }

        return enclosedTileCount;
    }

    enum Direction {
        NORTH, EAST, SOUTH, WEST
    }

    record Coordinate(int row, int column) {
    }

    public static void main(String[] args) {
        var input = loadInput("day10-input.txt");
        var pipeMaze = new PipeMaze(input);
        System.out.println("Part 1: " + pipeMaze.part1());
        System.out.println("Part 2: " + pipeMaze.part2());
    }
}

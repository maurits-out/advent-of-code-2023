package nl.mout.aoc2023.day10;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import static nl.mout.aoc2023.day10.PipeMaze.Direction.*;
import static nl.mout.aoc2023.support.InputLoader.loadInput;

public class PipeMaze {

    private final char[][] maze;
    private final int width;
    private final int height;

    public PipeMaze(String input) {
        this.maze = input.lines().map(String::toCharArray).toArray(char[][]::new);
        this.height = maze.length;
        this.width = maze[0].length;
    }

    public int part1() {
        return getLoopCoordinates().size() / 2;
    }

    public int part2() {
        var loopCoordinates = getLoopCoordinates();
        var enclosedTileCount = 0;

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

    private record Coordinate(int row, int column) {
    }

    private Coordinate findStart() {
        for (var r = 0; r < height; r++) {
            for (var c = 0; c < width; c++) {
                if (maze[r][c] == 'S') {
                    return new Coordinate(r ,c);
                }
            }
        }
        throw new NoSuchElementException();
    }

    private Coordinate move(Coordinate coordinate, Direction direction) {
        return switch (direction) {
            case NORTH -> new Coordinate(coordinate.row - 1, coordinate.column);
            case SOUTH -> new Coordinate(coordinate.row + 1, coordinate.column);
            case WEST -> new Coordinate(coordinate.row, coordinate.column - 1);
            case EAST -> new Coordinate(coordinate.row, coordinate.column + 1);
        };
    }

    private Direction updateHeading(Coordinate coordinate, Direction direction) {
        return switch (maze[coordinate.row][coordinate.column]) {
            case 'F' -> (direction == WEST) ? SOUTH : EAST;
            case '7' -> (direction == EAST) ? SOUTH : WEST;
            case 'J' -> (direction == SOUTH) ? WEST : NORTH;
            case 'L' -> (direction == WEST) ? NORTH : EAST;
            default -> direction;
        };
    }

    private Set<Coordinate> getLoopCoordinates() {
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

    public static void main(String[] args) {
        var input = loadInput("day10-input.txt");
        var pipeMaze = new PipeMaze(input);
        System.out.printf("Part 1: %d\n", pipeMaze.part1());
        System.out.printf("Part 2: %d\n", pipeMaze.part2());
    }
}

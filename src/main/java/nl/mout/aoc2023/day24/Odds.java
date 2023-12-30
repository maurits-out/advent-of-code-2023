package nl.mout.aoc2023.day24;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.SingularMatrixException;

import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.lang.Long.parseLong;
import static java.lang.Math.round;
import static java.util.Arrays.stream;
import static java.util.regex.Pattern.compile;
import static nl.mout.aoc2023.support.InputLoader.loadInput;

// https://www.reddit.com/r/adventofcode/comments/18q40he/2023_day_24_part_2_a_straightforward_nonsolver/
public class Odds {

    private static final Pattern NUMBER_PATTERN = compile("-?\\d+");
    private static final long PART1_SOLUTION_LOWERBOUND = 200000000000000L;
    private static final long PART1_SOLUTION_UPPERBOUND = 400000000000000L;

    private final Hailstone[] hailstones;

    public Odds(String input) {
        hailstones = input.lines().map(line -> {
            var values = NUMBER_PATTERN.matcher(line).results()
                    .mapToLong(r -> parseLong(r.group()))
                    .toArray();
            return new Hailstone(values[0], values[1], values[2], values[3], values[4], values[5]);
        }).toArray(Hailstone[]::new);
    }

    public int part1() {
        var count = 0;
        for (var i = 0; i < hailstones.length - 1; i++) {
            for (var j = i + 1; j < hailstones.length; j++) {
                var intersection = calculateIntersection(hailstones[i], hailstones[j]);
                if (intersection.isEmpty()) {
                    continue;
                }
                if (inRange(intersection.get()) && isInFuture(hailstones[i], hailstones[j], intersection.get())) {
                    count++;
                }
            }
        }
        return count;
    }

    public long part2() {
        var xySolution = solveXY();
        var zSolution = solveZ(xySolution[0], xySolution[2]);
        return round(xySolution[0] + xySolution[1] + zSolution[0]);
    }

    private record Hailstone(long px, long py, long pz, long vx, long vy, long vz) {
    }

    private Optional<double[]> calculateIntersection(Hailstone hailstone1, Hailstone hailstone2) {
        var coefficientMatrix = new Array2DRowRealMatrix(new double[][]{
                {hailstone1.vy(), -hailstone1.vx()},
                {hailstone2.vy(), -hailstone2.vx()}
        }, false);
        var constantVector = new ArrayRealVector(new double[]{
                (hailstone1.vy() * hailstone1.px()) - (hailstone1.vx() * hailstone1.py()),
                (hailstone2.vy() * hailstone2.px()) - (hailstone2.vx() * hailstone2.py()),
        }, false);

        try {
            var solver = new LUDecomposition(coefficientMatrix).getSolver();
            return Optional.of(solver.solve(constantVector).toArray());
        } catch (SingularMatrixException ex) {
            return Optional.empty();
        }
    }

    private boolean inRange(double[] intersection) {
        return stream(intersection).allMatch(v -> PART1_SOLUTION_LOWERBOUND <= v && v <= PART1_SOLUTION_UPPERBOUND);
    }

    private boolean isInFuture(Hailstone hailstone1, Hailstone hailstone2, double[] intersection) {
        return Stream.of(hailstone1, hailstone2).allMatch(h -> isInFuture(h, intersection));
    }

    private boolean isInFuture(Hailstone hailstone, double[] intersection) {
        return hailstone.vx() * (intersection[0] - hailstone.px()) >= 0 &&
                hailstone.vy() * (intersection[1] - hailstone.py()) >= 0;
    }

    private double[] solveXY() {
        var coefficients = new double[4][];
        var constants = new double[4];
        for (int r = 0; r < 4; r++) {
            var h1 = hailstones[r];
            var h2 = hailstones[r + 1];
            coefficients[r] = new double[]{
                    h2.vy() - h1.vy(), h1.vx() - h2.vx(), h1.py() - h2.py(), h2.px() - h1.px()
            };
            constants[r] = (h2.px() * h2.vy()) - (h2.py() * h2.vx()) - (h1.px() * h1.vy()) + (h1.py() * h1.vx());
        }
        var coefficientMatrix = new Array2DRowRealMatrix(coefficients, false);
        var solver = new LUDecomposition(coefficientMatrix).getSolver();
        var constantVector = new ArrayRealVector(constants, false);
        return solver.solve(constantVector).toArray();
    }

    private double[] solveZ(double rockPx, double rockVx) {
        var coefficients = new double[2][];
        var constants = new double[2];
        for (int r = 0; r < 2; r++) {
            var h1 = hailstones[r];
            var h2 = hailstones[r + 1];
            coefficients[r] = new double[]{h1.vx() - h2.vx(), h2.px() - h1.px()};
            constants[r] = (h2.px() * h2.vz()) - (h2.pz() * h2.vx()) - (h1.px() * h1.vz()) + (h1.pz() * h1.vx()) -
                    (rockPx * h2.vz()) + (rockPx * h1.vz()) - (h1.pz() * rockVx) + (h2.pz() * rockVx);
        }
        var coefficientMatrix = new Array2DRowRealMatrix(coefficients, false);
        var solver = new LUDecomposition(coefficientMatrix).getSolver();
        var constantVector = new ArrayRealVector(constants, false);
        return solver.solve(constantVector).toArray();
    }

    public static void main(String[] args) {
        var input = loadInput("day24-input.txt");
        var odds = new Odds(input);
        System.out.printf("Part 1: %d\n", odds.part1());
        System.out.printf("Part 2: %d\n", odds.part2());
    }
}

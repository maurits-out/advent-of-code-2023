package nl.mout.aoc2023.day24;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.SingularMatrixException;

import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.lang.Long.parseLong;
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
                Hailstone hailstone1 = hailstones[i], hailstone2 = hailstones[j];
                var intersection = calculateIntersection(hailstone1, hailstone2);
                if (intersection != null && inRange(intersection) && isInFuture(hailstone1, hailstone2, intersection)) {
                    count++;
                }
            }
        }
        return count;
    }

    private double[] calculateIntersection(Hailstone hailstone1, Hailstone hailstone2) {
        var coefficients = new Array2DRowRealMatrix(new double[][]{
                {hailstone1.vy(), -hailstone1.vx()},
                {hailstone2.vy(), -hailstone2.vx()}
        }, false);
        var solver = new LUDecomposition(coefficients).getSolver();
        var constants = new ArrayRealVector(new double[]{
                (hailstone1.vy() * hailstone1.px()) - (hailstone1.vx() * hailstone1.py()),
                (hailstone2.vy() * hailstone2.px()) - (hailstone2.vx() * hailstone2.py()),
        }, false);

        try {
            return solver.solve(constants).toArray();
        } catch (SingularMatrixException ex) {
            return null;
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

    private record Hailstone(long px, long py, long pz, long vx, long vy, long vz) {
    }

    public long part2() {
        var coefficients = new double[4][];
        var constants = new double[4];
        double[] xandy = solveXY(coefficients, constants);
        System.out.printf("x = %f; y = %f; vx = %f; vy = %f\n", xandy[0], xandy[1], xandy[2], xandy[3]);
        double[] yandz = solveYZ(coefficients, constants);
        System.out.printf("y = %f; z = %f; vy = %f; vz = %f\n", yandz[0], yandz[1], yandz[2], yandz[3]);

        return (long) (xandy[0] + xandy[1] + yandz[1]);
    }

    private double[] solveXY(double[][] coefficients, double[] constants) {
        for (int r = 0; r < 4; r++) {
            var h1 = hailstones[r * 2];
            var h2 = hailstones[(r * 2) + 1];
            coefficients[r] = new double[] {
                    h2.vy() - h1.vy(), h1.vx() - h2.vx(), h1.py() - h2.py(), h2.px() - h1.px()
            };
            constants[r] = (h2.px() * h2.vy()) - (h2.py() * h2.vx()) - (h1.px() * h1.vy()) + (h1.py() * h1.vx());
        }
        var coefficientMatrix = new Array2DRowRealMatrix(coefficients, false);
        var solver = new LUDecomposition(coefficientMatrix).getSolver();
        var constantVector = new ArrayRealVector(constants, false);
        return solver.solve(constantVector).toArray();
    }

    private double[] solveYZ(double[][] coefficients, double[] constants) {
        for (int r = 0; r < 4; r++) {
            var h1 = hailstones[r * 2];
            var h2 = hailstones[(r * 2) + 1];
            coefficients[r] = new double[] {
                    h2.vz() - h1.vz(), h1.vy() - h2.vy(), h1.pz() - h2.pz(), h2.py() - h1.py()
            };
            constants[r] = (h2.py() * h2.vz()) - (h2.pz() * h2.vy()) - (h1.py() * h1.vz()) + (h1.pz() * h1.vy());
        }
        var coefficientMatrix = new Array2DRowRealMatrix(coefficients, false);
        var solver = new LUDecomposition(coefficientMatrix).getSolver();
        var constantVector = new ArrayRealVector(constants, false);
        return solver.solve(constantVector).toArray();
    }

    public static void main(String[] args) {
        var input = loadInput("day24-input.txt");
        var odds = new Odds(input);
        System.out.println("Part 1: " + odds.part1());
        System.out.println("Part 2: " + odds.part2());
    }
}

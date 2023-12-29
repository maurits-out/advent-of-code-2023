package nl.mout.aoc2023.day15;

import java.util.*;

import static java.lang.Integer.parseInt;
import static java.util.Collections.list;
import static nl.mout.aoc2023.support.InputLoader.loadInput;

public class LensLibrary {

    private final String input;

    public LensLibrary(String input) {
        this.input = input;
    }

    public int part1() {
        var tokenizer = new StringTokenizer(input, ",");
        return list(tokenizer).stream()
                .mapToInt(token -> hash((String) token))
                .sum();
    }

    public int part2() {
        var boxes = createMap();
        var steps = new StringTokenizer(input, ",");
        while (steps.hasMoreTokens()) {
            var step = steps.nextToken();
            if (step.endsWith("-")) {
                removeLens(step, boxes);
            } else {
                addLens(step, boxes);
            }
        }
        return focusingPower(boxes);
    }

    private record Lens(String label, int focalLength) {
    }

    private List<List<Lens>> createMap() {
        var boxes = new ArrayList<List<Lens>>(256);
        for (var i = 0; i < 256; i++) {
            boxes.add(new ArrayList<>());
        }
        return boxes;
    }

    private void addLens(String step, List<List<Lens>> boxes) {
        var parts = new StringTokenizer(step, "=");
        var lens = new Lens(parts.nextToken(), parseInt(parts.nextToken()));
        var lenses = boxes.get(hash(lens.label()));
        var i = 0;
        while (i < lenses.size()) {
            if (lenses.get(i).label().equals(lens.label())) {
                lenses.set(i, lens);
                break;
            }
            i++;
        }
        if (i == lenses.size()) {
            lenses.add(lens);
        }
    }

    private void removeLens(String step, List<List<Lens>> boxes) {
        var label = step.substring(0, step.length() - 1);
        var lenses = boxes.get(hash(label));
        for (var i = 0; i < lenses.size(); i++) {
            if (lenses.get(i).label().equals(label)) {
                lenses.remove(i);
                break;
            }
        }
    }

    private int focusingPower(List<List<Lens>> boxes) {
        var sum = 0;
        for (var i = 0; i < 256; i++) {
            var lenses = boxes.get(i);
            for (var j = 0; j < lenses.size(); j++) {
                sum += (1 + i) * (j + 1) * lenses.get(j).focalLength();
            }
        }
        return sum;
    }

    private int hash(String step) {
        var current = 0;
        for (var i = 0; i < step.length(); i++) {
            current = ((current + step.charAt(i)) * 17) % 256;
        }
        return current;
    }

    public static void main(String[] args) {
        var input = loadInput("day15-input.txt");
        var lenslibrary = new LensLibrary(input);
        System.out.printf("Part 1: %d\n", lenslibrary.part1());
        System.out.printf("Part 2: %d\n", lenslibrary.part2());
    }
}

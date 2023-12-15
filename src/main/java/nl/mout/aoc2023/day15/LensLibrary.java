package nl.mout.aoc2023.day15;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import static java.lang.Integer.parseInt;
import static nl.mout.aoc2023.support.InputLoader.loadInput;

public class LensLibrary {

    private final String input;

    public LensLibrary(String input) {
        this.input = input;
    }

    public int part1() {
        var tokenizer = new StringTokenizer(input, ",");
        var sum = 0;
        while (tokenizer.hasMoreTokens()) {
            sum += hash(tokenizer.nextToken());
        }
        return sum;
    }

    int part2() {
        var boxes = new HashMap<Integer, List<Lens>>();
        for (var i = 0; i < 256; i++) {
            boxes.put(i, new ArrayList<>());
        }

        var steps = new StringTokenizer(input, ",");
        while (steps.hasMoreTokens()) {
            var step = steps.nextToken();
            if (step.endsWith("-")) {
                var label = step.substring(0, step.length() - 1);
                var lenses = boxes.get(hash(label));
                for (var i = 0; i < lenses.size(); i++) {
                    if (lenses.get(i).label().equals(label)) {
                        lenses.remove(i);
                        break;
                    }
                }
            } else {
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
        }

        var sum = 0;
        for (var i = 0; i < 256; i++) {
            if (boxes.containsKey(i)) {
                var lenses = boxes.get(i);
                for (var j = 0; j < lenses.size(); j++) {
                    sum += (1 + i) * (j + 1) * lenses.get(j).focalLength();
                }
            }
        }

        return sum;
    }

    int hash(String step) {
        var current = 0;
        for (var i = 0; i < step.length(); i++) {
            current = ((current + step.charAt(i)) * 17) % 256;
        }
        return current;
    }

    record Lens(String label, int focalLength) {
    }


    public static void main(String[] args) {
        var input = loadInput("day15-input.txt");
        var lenslibrary = new LensLibrary(input);
        System.out.println("Part 1: " + lenslibrary.part1());
        System.out.println("Part 2: " + lenslibrary.part2());
    }
}

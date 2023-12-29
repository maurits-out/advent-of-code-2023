package nl.mout.aoc2023.day14;

import java.util.ArrayList;
import java.util.List;

import static nl.mout.aoc2023.support.InputLoader.loadInput;

public class ParabolicReflectorDish {

    private final Dish dish;

    public ParabolicReflectorDish(String input) {
        this.dish = Dish.of(input);
    }

    public int part1() {
        return dish.tiltNorth().totalLoad();
    }

    public int part2() {
        var latestDish = dish;
        var previousDishes = new ArrayList<Dish>();
        var currentCycle = 0;
        var totalCycles = 1000000000;

        while (currentCycle < totalCycles) {
            previousDishes.add(latestDish);
            latestDish = latestDish.tiltNorth().tiltWest().tiltSouth().tiltEast();
            currentCycle++;
            var indexOfSameDish = previousDishes.indexOf(latestDish);
            if (indexOfSameDish >= 0) {
                latestDish = applyShortcut(currentCycle, totalCycles, indexOfSameDish, previousDishes);
                break;
            }
        }
        return latestDish.totalLoad();
    }

    private Dish applyShortcut(int currentCycle, int totalCycles, int indexOfSameDish, List<Dish> previousDishes) {
        var loopSize = currentCycle - indexOfSameDish;
        currentCycle += ((totalCycles - currentCycle) / loopSize) * loopSize;
        var remainingCycles = totalCycles - currentCycle;
        return previousDishes.get(indexOfSameDish + remainingCycles);
    }

    public static void main(String[] args) {
        var input = loadInput("day14-input.txt");
        var dish = new ParabolicReflectorDish(input);
        System.out.printf("Part 1: %d\n", dish.part1());
        System.out.printf("Part 2: %d\n", dish.part2());
    }
}

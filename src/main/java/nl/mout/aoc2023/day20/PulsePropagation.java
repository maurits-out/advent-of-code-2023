package nl.mout.aoc2023.day20;

import org.apache.commons.math3.util.ArithmeticUtils;

import java.util.*;

import static java.util.Arrays.stream;
import static nl.mout.aoc2023.day20.Pulse.HIGH;
import static nl.mout.aoc2023.day20.Pulse.LOW;
import static nl.mout.aoc2023.support.InputLoader.loadInput;

public class PulsePropagation {

    private final Map<String, Module> modules;
    private final List<String> broadcastOutputs;

    public PulsePropagation(String input) {
        this.modules = new HashMap<>();
        this.broadcastOutputs = new ArrayList<>();

        input.lines().forEach(line -> {
            var idx = line.indexOf("->");
            var left = line.substring(0, idx - 1);
            var right = line.substring(idx + 3);
            var outputs = stream(right.split(", ")).toList();
            if (left.equals("broadcaster")) {
                broadcastOutputs.addAll(outputs);
            } else {
                var name = left.substring(1);
                if (left.charAt(0) == '%') {
                    modules.put(name, new FlipFlop(name, outputs));
                } else {
                    modules.put(name, new Conjunction(name, outputs));
                }
            }
        });

        modules.forEach((name, module) -> module.getOutputs().stream()
                .map(modules::get)
                .filter(m -> m instanceof Conjunction)
                .forEach(m -> ((Conjunction) m).addInput(name))
        );
    }

    public int part1() {
        var lowCount = 0;
        var highCount = 0;
        var broadcasterSignals = createBroadcasterSignals();
        var queue = new LinkedList<Signal>();

        for (var buttonCount = 0; buttonCount < 1000; buttonCount++) {
            lowCount++;
            queue.addAll(broadcasterSignals);
            while (!queue.isEmpty()) {
                var signal = queue.poll();
                if (signal.pulse().equals(LOW)) {
                    lowCount++;
                } else {
                    highCount++;
                }
                var module = modules.get(signal.to());
                if (module != null) {
                    queue.addAll(module.process(signal));
                }
            }
        }
        return lowCount * highCount;
    }

    public long part2() {
        var broadcasterSignals = createBroadcasterSignals();
        var queue = new LinkedList<Signal>();
        var buttonCount = 0;
        var singleInputForRx = findInputs("rx").getFirst();
        var inputCount = findInputs(singleInputForRx).size();
        var buttonCounts = new HashMap<String, Integer>();

        while (buttonCounts.size() < inputCount) {
            buttonCount++;
            queue.addAll(broadcasterSignals);
            while (!queue.isEmpty()) {
                var signal = queue.poll();
                if (signal.to().equals(singleInputForRx) && signal.pulse().equals(HIGH)) {
                    buttonCounts.put(signal.from(), buttonCount);
                }
                var module = modules.get(signal.to());
                if (module != null) {
                    queue.addAll(module.process(signal));
                }
            }
        }

        return buttonCounts.values().stream()
                .mapToLong(v -> (long) v + 1000)
                .reduce(ArithmeticUtils::lcm)
                .orElseThrow();
    }

    private List<Signal> createBroadcasterSignals() {
        return broadcastOutputs.stream()
                .map(output -> new Signal(LOW, "broadcaster", output))
                .toList();
    }


    private List<String> findInputs(String name) {
        return modules.keySet().stream()
                .filter(n -> modules.get(n).getOutputs().contains(name))
                .toList();
    }

    public static void main(String[] args) {
        var input = loadInput("day20-input.txt");
        var pulsePropagation = new PulsePropagation(input);
        System.out.printf("Part 1: %d\n", pulsePropagation.part1());
        System.out.printf("Part 2: %d\n", pulsePropagation.part2());
    }
}

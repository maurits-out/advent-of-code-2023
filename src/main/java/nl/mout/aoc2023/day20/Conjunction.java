package nl.mout.aoc2023.day20;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nl.mout.aoc2023.day20.Pulse.HIGH;
import static nl.mout.aoc2023.day20.Pulse.LOW;

public class Conjunction extends Module {

    private final Map<String, Pulse> mostRecentPulse = new HashMap<>();

    public Conjunction(String name, List<String> outputs) {
        super(name, outputs);
    }

    public void addInput(String name) {
        mostRecentPulse.put(name, LOW);
    }

    @Override
    List<Signal> process(Signal signal) {
        mostRecentPulse.put(signal.from(), signal.pulse());
        var allHigh = mostRecentPulse.values().stream().allMatch(p -> p.equals(HIGH));
        return generateSignals(allHigh ? LOW : HIGH);
    }
}

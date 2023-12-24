package nl.mout.aoc2023.day20;

import java.util.List;

import static java.util.Collections.emptyList;
import static nl.mout.aoc2023.day20.Pulse.HIGH;
import static nl.mout.aoc2023.day20.Pulse.LOW;

public class FlipFlop extends Module {

    private boolean isOn;

    public FlipFlop(String name, List<String> outputs) {
        super(name, outputs);
        this.isOn = false;
    }

    @Override
    List<Signal> process(Signal signal) {
        List<Signal> signals;
        if (signal.pulse().equals(HIGH)) {
            signals = emptyList();
        } else {
            var pulse = isOn ? LOW : HIGH;
            isOn = !isOn;
            signals = generateSignals(pulse);
        }
        return signals;
    }
}

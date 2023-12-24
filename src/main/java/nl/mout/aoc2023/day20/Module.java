package nl.mout.aoc2023.day20;


import java.util.List;

abstract class Module {

    private final String name;

    private final List<String> outputs;

    public Module(String name, List<String> outputs) {
        this.name = name;
        this.outputs = outputs;
    }

    public String getName() {
        return name;
    }

    public List<String> getOutputs() {
        return outputs;
    }

    protected List<Signal> generateSignals(Pulse pulse) {
        return outputs.stream()
                .map(output -> new Signal(pulse, name, output))
                .toList();
    }

    abstract List<Signal> process(Signal signal);
}

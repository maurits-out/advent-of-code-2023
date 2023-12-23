package nl.mout.aoc2023.day19;

import java.util.Map;
import java.util.Optional;

record Constant(String dest) implements Rule {

    @Override
    public Optional<String> evaluate(Map<String, Integer> part) {
        return Optional.of(dest);
    }
}

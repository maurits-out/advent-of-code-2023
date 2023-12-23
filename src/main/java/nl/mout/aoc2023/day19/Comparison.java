package nl.mout.aoc2023.day19;

import java.util.Map;
import java.util.Optional;

record Comparison(String category, String operand, int value, String dest) implements Rule {

    @Override
    public Optional<String> evaluate(Map<String, Integer> part) {
        var categoryValue = part.get(category);
        var result = switch (operand) {
            case ">" -> categoryValue > value;
            case "<" -> categoryValue < value;
            default -> throw new IllegalStateException("Invalid operand: " + operand);
        };
        if (result) {
            return Optional.of(dest);
        }
        return Optional.empty();
    }

    public Comparison negate() {
        return switch (operand) {
            case ">" -> new Comparison(category, "<", value + 1, dest);
            case "<" -> new Comparison(category, ">", value - 1, dest);
            default -> throw new IllegalStateException("Invalid operand: " + operand);
        };
    }
}

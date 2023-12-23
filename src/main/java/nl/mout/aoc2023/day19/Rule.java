package nl.mout.aoc2023.day19;

import java.util.Map;
import java.util.Optional;

sealed interface Rule permits Comparison, Constant {
    Optional<String> evaluate(Map<String, Integer> part);
}

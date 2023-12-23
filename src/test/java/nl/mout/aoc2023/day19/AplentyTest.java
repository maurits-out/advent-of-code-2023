package nl.mout.aoc2023.day19;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AplentyTest {

    @Test
    void testCountCombinations() {
        assertCount("""
                in{A}
                """, 256000000000000L);
        assertCount("""
                in{R}
                """, 0);
        assertCount("""
                in{x>0:px,py}
                px{A}
                py{R}
                """, 256000000000000L);
        assertCount("""
                in{x>2000:px,py}
                px{A}
                py{R}
                """, 128000000000000L);
    }

    private void assertCount(String workflows, long expected) {
        var input = workflows + "\r\n\r\n{x=787,m=2655,a=1222,s=2876}";
        var aplenty = new Aplenty(input);
        assertThat(aplenty.part2()).isEqualTo(expected);
    }

}

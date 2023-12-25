package nl.mout.aoc2023.day25;

import org.jgrapht.alg.StoerWagnerMinimumCut;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.StringTokenizer;

import static java.util.Collections.list;
import static nl.mout.aoc2023.support.InputLoader.loadInput;

public class Snowverload {

    private final SimpleGraph<String, DefaultEdge> graph;

    public Snowverload(String input) {
        graph = createGraph(input);
    }

    private SimpleGraph<String, DefaultEdge> createGraph(String input) {
        var graph = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
        input.lines().forEach(line -> {
            var tokenizer = new StringTokenizer(line);
            var from = tokenizer.nextToken().substring(0, 3);
            graph.addVertex(from);
            list(tokenizer).forEach(token -> {
                var to = (String) token;
                graph.addVertex(to);
                graph.addEdge(from, to);
            });
        });
        return graph;
    }

    public int part1() {
        var stoerWagner = new StoerWagnerMinimumCut<>(graph);
        var sizeMinCut = stoerWagner.minCut().size();
        return sizeMinCut * (graph.vertexSet().size() - sizeMinCut);
    }

    public static void main(String[] args) {
        var input = loadInput("day25-input.txt");
        var snowverload = new Snowverload(input);
        System.out.println("Answer: " + snowverload.part1());
    }
}

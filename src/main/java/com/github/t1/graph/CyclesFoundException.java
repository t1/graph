package com.github.t1.graph;

import static com.github.t1.graph.Graph.*;
import static java.util.stream.Collectors.*;

import java.util.List;

import lombok.Getter;

public class CyclesFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> CyclesFoundException(List<List<Node<T>>> cycles) {
        this.cycles = (List) cycles;
    }

    @Getter
    private final List<List<Node<?>>> cycles;

    @Override
    public String getMessage() {
        return "found " + cycles.size() + " cycle(s) in graph:\n  " +
                cycles.stream()
                        .map(cycle -> listToString(" -> ", cycle, node -> node.getValue().toString()))
                        .collect(joining("\n  "));
    }
}

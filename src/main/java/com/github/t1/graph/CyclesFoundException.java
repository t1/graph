package com.github.t1.graph;

import java.util.List;

import com.github.t1.graph.Graph.Node;

import lombok.Getter;

public class CyclesFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> CyclesFoundException(List<Node<T>> cycleNodes) {
        this.cycleNodes = (List) cycleNodes;
    }

    @Getter
    private final List<Node<?>> cycleNodes;

    @Override
    public String getMessage() {
        return "found " + cycleNodes.size() + " cycle(s) in graph, containing at least: " + cycleNodes;
    }
}

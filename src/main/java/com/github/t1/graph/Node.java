package com.github.t1.graph;

import static com.github.t1.graph.Graph.*;

import java.util.*;
import java.util.function.Consumer;

import lombok.Data;

@Data
public class Node<T> {
    private T value;
    private List<Node<T>> links = new ArrayList<>();
    private List<Mark> marks = new ArrayList<>();

    Node(T value) {
        this.value = value;
    }

    public Node<T> linkedTo(Node<T> target) {
        links.add(target);
        return this;
    }

    public boolean isLinkedTo(Collection<Node<T>> nodes) {
        return this.links.containsAll(nodes);
    }

    public void forEachLink(Consumer<? super Node<T>> consumer) {
        links.forEach(consumer);
    }

    public Node<T> marked(Mark mark) {
        mark(mark);
        return this;
    }

    public boolean mark(Mark mark) {
        if (marks.contains(mark))
            return false;
        marks.add(mark);
        return true;
    }

    /** @return was it marked as this? */
    public boolean unmark(Mark mark) {
        return marks.remove(mark);
    }

    public boolean isMarked(Mark mark) {
        return marks.contains(mark);
    }

    @Override
    public String toString() {
        return value + (marks.isEmpty() ? "" : marks.toString()) + " -> {" + nodeNames(links) + "}";
    }
}


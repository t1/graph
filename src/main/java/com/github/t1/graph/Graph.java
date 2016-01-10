package com.github.t1.graph;

import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;

import java.util.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;

import lombok.*;

@Data
public class Graph<T> {
    @SafeVarargs
    public static <T> String nodeNames(Node<T>... nodes) {
        return nodeNames(asList(nodes));
    }

    public static <T> String nodeNames(List<Node<T>> nodes) {
        return listToString(", ", nodes, node -> node.getValue().toString());
    }

    private static String listToString(String delimiter, List<?> list) {
        return listToString(delimiter, list, Object::toString);
    }

    private static <T> String listToString(String delimiter, List<T> list, Function<T, String> toString) {
        return String.join(delimiter, list.stream().map(toString).collect(toList()));
    }

    @Value
    public static class StringMark implements Mark {
        String string;

        @Override
        public String toString() {
            return string;
        }
    }

    private final List<Node<T>> nodes = new ArrayList<>();

    public Node<T> createNode(T value) {
        Node<T> node = new Node<>(value);
        nodes.add(node);
        return node;
    }

    /**
     * @see <a href="https://en.wikipedia.org/wiki/Topological_sorting#Tarjan.27s_algorithm">wikipedia</a>
     */
    public void toplologicalSort() {
        Mark temp = new StringMark("temporary");
        Mark seen = new StringMark("seen");
        Mark cycle = new StringMark("cycle");
        List<Node<T>> sortedNodes = new LinkedList<>();

        visit(new Consumer<Node<T>>() {
            @Override
            public void accept(Node<T> node) {
                if (node.isMarked(seen))
                    return;
                if (node.isMarked(temp)) {
                    node.mark(cycle);
                } else {
                    node.marked(temp);
                    node.forEachLink(this);
                    node.unmark(temp);
                    sortedNodes.add(0, node);
                }
                node.mark(seen);
            }
        });

        replaceNodes(sortedNodes);
        unmark(seen);
        List<Node<T>> cycleNodes = find(cycle);
        cycleNodes.forEach(cycle::unmark);
        if (!cycleNodes.isEmpty())
            throw new CyclesFoundException(cycleNodes);
    }

    private void replaceNodes(List<Node<T>> nodes) {
        this.nodes.clear();
        this.nodes.addAll(nodes);
    }

    public int mark(Mark mark) {
        return countingVisit(mark::mark);
    }

    public int unmark(Mark mark) {
        return countingVisit(mark::unmark);
    }

    public int countingVisit(Function<Node<T>, Boolean> visitor) {
        AtomicInteger count = new AtomicInteger(0);
        visit(node -> {
            if (visitor.apply(node))
                count.incrementAndGet();
        });
        return count.get();
    }

    public List<Node<T>> find(Mark mark) {
        return find(mark::isMarked);
    }

    public List<Node<T>> find(Predicate<Node<T>> predicate) {
        List<Node<T>> found = new ArrayList<>();
        visit(node -> {
            if (predicate.test(node))
                found.add(node);
        });
        return found;
    }

    public void visit(Consumer<Node<T>> visitor) {
        nodes.forEach(visitor);
    }

    public int size() {
        return nodes.size();
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    @Override
    public String toString() {
        return listToString("\n", nodes);
    }

}

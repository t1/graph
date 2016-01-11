package com.github.t1.graph;

import static java.lang.Math.min;
import static java.util.Arrays.*;
import static java.util.Collections.*;
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

    public static String listToString(String delimiter, List<?> list) {
        return listToString(delimiter, list, Object::toString);
    }

    public static <T> String listToString(String delimiter, List<T> list, Function<T, String> toString) {
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

    public Node<T> findOrCreateNode(T value) {
        return findNode(value).orElseGet(() -> createNode(value));
    }

    public Optional<Node<T>> findNode(T value) {
        List<Node<T>> found = find(node -> node.getValue().equals(value));
        if (found.isEmpty())
            return Optional.empty();
        else if (found.size() == 1)
            return Optional.of(found.get(0));
        else
            throw new IllegalStateException("multiple nodes with the same value to search for: " + value);
    }

    public Node<T> createNode(T value) {
        Node<T> node = new Node<>(value);
        nodes.add(node);
        return node;
    }

    @Value
    private static class IndexMark implements Mark {
        public static int of(Node<?> node) {
            return node.getMark(IndexMark.class).get().getIndex();
        }

        int index;
    }

    @Value
    private static class Lowlink implements Mark {
        public static int removeLowlinkIndex(Node<?> v) {
            return v.unmark(Lowlink.class).get().getIndex();
        }

        public static int of(Node<?> v) {
            return v.getMark(Lowlink.class).get().getIndex();
        }

        int index;
    }

    private static Mark onStack = new StringMark("onStack");

    /**
     * @see <a href="https://en.wikipedia.org/wiki/Tarjan%27s_strongly_connected_components_algorithm"> Tarjan's
     *      strongly connected components algorithm</a>
     */
    public void topologicalSort() {
        List<List<Node<T>>> stronglyConnectedComponents = new ArrayList<>();

        AtomicInteger index = new AtomicInteger(0);
        Stack<Node<T>> S = new Stack<>();
        for (Node<T> v : nodes)
            if (!v.getMark(IndexMark.class).isPresent())
                strongconnect(v, stronglyConnectedComponents, S, index);

        unmark(Lowlink.class);
        unmark(IndexMark.class);

        List<Node<T>> sorted = new ArrayList<>();
        List<List<Node<T>>> cycles = new ArrayList<>();
        stronglyConnectedComponents.forEach(scc -> {
            if (scc.size() > 1)
                cycles.add(scc);
            Node<T> node = scc.iterator().next();
            if (node.isLinkedTo(node))
                cycles.add(scc);
            sorted.add(node);
        });
        if (!cycles.isEmpty())
            throw new CyclesFoundException(cycles);

        reverse(sorted);
        replaceNodes(sorted);
    }

    private void strongconnect(Node<T> v, List<List<Node<T>>> stronglyConnectedComponents, Stack<Node<T>> S,
            AtomicInteger index) {
        // Set the depth index for v to the smallest unused index
        int indexValue = index.getAndIncrement();
        v.mark(new IndexMark(indexValue));
        v.mark(new Lowlink(indexValue));
        S.push(v);
        v.mark(onStack);

        // Consider successors of v
        for (Node<T> w : v.getLinks()) {
            if (!w.isMarked(IndexMark.class)) {
                // Successor w has not yet been visited; recurse on it
                strongconnect(w, stronglyConnectedComponents, S, index);
                int vLowlink = Lowlink.removeLowlinkIndex(v);
                int wLowlink = Lowlink.of(w);
                v.mark(new Lowlink(min(vLowlink, wLowlink)));
            } else if (w.isMarked(onStack)) {
                // Successor w is in stack S and hence in the current SCC
                int vLowlink = Lowlink.removeLowlinkIndex(v);
                int wIndex = IndexMark.of(w);
                v.mark(new Lowlink(min(vLowlink, wIndex)));
            }
        }

        // If v is a root node, pop the stack and generate an SCC
        if (Lowlink.of(v) == IndexMark.of(v)) {
            List<Node<T>> stronglyConnectedComponent = new ArrayList<>();
            Node<T> w;
            do {
                w = S.pop();
                w.unmark(onStack);
                stronglyConnectedComponent.add(w);
            } while (w != v);
            stronglyConnectedComponents.add(stronglyConnectedComponent);
        }
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

    public int unmark(Class<? extends Mark> type) {
        return countingVisit(node -> node.unmark(type).isPresent());
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

    public List<Node<T>> remove(Predicate<Node<T>> predicate) {
        List<Node<T>> found = find(predicate);
        found.forEach(node -> remove(node));
        return found;
    }

    public boolean remove(Node<T> node) {
        return nodes.remove(node);
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

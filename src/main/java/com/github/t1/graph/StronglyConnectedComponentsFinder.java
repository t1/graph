package com.github.t1.graph;

import static java.lang.Math.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import com.github.t1.graph.Mark.Index;

import lombok.*;

/**
 * @see <a href="https://en.wikipedia.org/wiki/Tarjan%27s_strongly_connected_components_algorithm">Tarjan's Strongly
 *      Connected Components Algorithm</a>
 */
class StronglyConnectedComponentsFinder<T> implements Consumer<Node<T>> {
    @Value
    static class Root implements Mark {
        public static int removeRoot(Node<?> node) {
            return node.unmark(Root.class).get().getIndex();
        }

        public static int of(Node<?> node) {
            return node.getMark(Root.class).get().getIndex();
        }

        int index;
    }

    @Getter
    private final List<List<Node<T>>> stronglyConnectedComponents = new ArrayList<>();

    private final AtomicInteger index = new AtomicInteger(0);
    private final Stack<Node<T>> stack = new Stack<>();

    @Override
    public void accept(Node<T> node) {
        if (!node.isMarked(Index.class))
            strongconnect(node);
    }

    private void strongconnect(Node<T> node) {
        int indexValue = index.getAndIncrement();
        node.mark(new Index(indexValue));
        node.mark(new Root(indexValue));
        stack.push(node);

        for (Node<T> successor : node.getLinks()) {
            if (!successor.isMarked(Index.class)) {
                strongconnect(successor);
                setRoot(node, Root.of(successor));
            } else if (stack.contains(successor)) {
                // -> it's in the current SCC
                setRoot(node, Index.of(successor));
            }
        }

        if (isRoot(node))
            stronglyConnectedComponents.add(popSCC(stack, node));
    }

    private void setRoot(Node<T> node, int min) {
        node.mark(new Root(min(Root.removeRoot(node), min)));
    }

    private List<Node<T>> popSCC(Stack<Node<T>> stack, Node<T> node) {
        List<Node<T>> scc = new ArrayList<>();
        Node<T> member;
        do {
            member = stack.pop();
            scc.add(member);
        } while (member != node);
        return scc;
    }

    private boolean isRoot(Node<T> node) {
        return Root.of(node) == Index.of(node);
    }
}

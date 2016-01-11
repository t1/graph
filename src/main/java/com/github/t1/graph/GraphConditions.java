package com.github.t1.graph;

import static com.github.t1.graph.Graph.*;
import static java.util.Arrays.*;

import java.util.List;

import org.assertj.core.api.Condition;

/**
 * <a href="http://joel-costigliola.github.io/assertj/index.html">AssertJ</a> conditions for graphs. They are so helpful
 * for testing graphs, that we don't hide them in our own tests but put them into the jar, while keeping the dependency
 * on AssertJ optional.
 */
public class GraphConditions {
    public static Condition<Graph<?>> empty = new Condition<>(Graph::isEmpty, "empty");

    @SafeVarargs
    public static <T> Condition<Graph<T>> nodes(Node<T>... nodes) {
        return nodes(asList(nodes));
    }

    public static <T> Condition<Graph<T>> nodes(List<Node<T>> nodes) {
        return new Condition<>(graph -> graph.getNodes().equals(nodes), "nodes: " + nodeNames(nodes));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final Condition<Node<?>> noLinks = (Condition) linksTo();

    @SafeVarargs
    public static <T> Condition<Node<T>> linksTo(Node<T>... nodes) {
        return linksTo(asList(nodes));
    }

    public static <T> Condition<Node<T>> linksTo(List<Node<T>> nodes) {
        return new Condition<>(node -> node.isLinkedTo(nodes), "links to: " + nodeNames(nodes));
    }

    public static final Condition<? super Graph<String>> noMarks =
            new Condition<>(graph -> graph.find(node -> node.isMarked(Mark.class)).isEmpty(), "no marks");

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final Condition<Node<?>> unmarked = (Condition) marked();

    @SafeVarargs
    public static <T> Condition<Node<T>> marked(Mark... marks) {
        return marked(asList(marks));
    }

    public static <T> Condition<Node<T>> marked(List<Mark> marks) {
        return new Condition<>(node -> node.getMarks().equals(marks), "marked as: " + marks);
    }

    @SafeVarargs
    public static <T> Condition<Throwable> cycle(Node<T>... nodes) {
        return cyclesFoundExceptionWithNodes(asList(nodes));
    }

    public static <T> Condition<Throwable> cyclesFoundExceptionWithNodes(List<Node<T>> nodes) {
        return new Condition<>(
                exception -> exception != null && ((CyclesFoundException) exception).getCycles().contains(nodes),
                "cycle nodes: " + nodeNames(nodes));
    }
}

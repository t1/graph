package com.github.t1.graph;

import static com.github.t1.graph.GraphConditions.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import com.github.t1.graph.Graph.StringMark;

public class NodeMarkingTest {
    private static final Mark m = new StringMark("m");

    @Test
    public void shouldMark() {
        Graph<String> graph = new Graph<>();
        Node<String> A = graph.createNode("A").marked(m);
        Node<String> B = graph.createNode("B").linkedTo(A);

        assertThat(A).is(marked(m));
        assertThat(B).is(unmarked);
        assertThat(graph).hasToString("A[m] -> {}\nB -> {A}");
    }

    @Test
    public void shouldMarkOnlyOnce() {
        Graph<String> graph = new Graph<>();
        Node<String> A = graph.createNode("A").marked(m);

        A.marked(m);

        assertThat(A).is(marked(m));
        assertThat(graph).hasToString("A[m] -> {}");
    }

    @Test
    public void shouldUnmarkMarked() {
        Graph<String> graph = new Graph<>();
        Node<String> A = graph.createNode("A").marked(m).marked(m);
        Node<String> B = graph.createNode("B").marked(m).linkedTo(A);

        boolean wasMarked = A.unmark(m);

        assertThat(wasMarked).isTrue();
        assertThat(A).is(unmarked);
        assertThat(B).is(marked(m));
        assertThat(graph).hasToString("A -> {}\nB[m] -> {A}");
    }

    @Test
    public void shouldUnmarkUnmarked() {
        Graph<String> graph = new Graph<>();
        Node<String> A = graph.createNode("A");
        Node<String> B = graph.createNode("B").marked(m).linkedTo(A);

        boolean wasMarked = A.unmark(m);

        assertThat(wasMarked).isFalse();
        assertThat(A).is(unmarked);
        assertThat(B).is(marked(m));
        assertThat(graph).hasToString("A -> {}\nB[m] -> {A}");
    }

    @Test
    public void shouldMarkGraph() {
        Graph<String> graph = new Graph<>();
        Node<String> A = graph.createNode("A");
        Node<String> B = graph.createNode("B").marked(m).linkedTo(A);

        int marksAdded = graph.mark(m);

        assertThat(A).is(marked(m));
        assertThat(B).is(marked(m));
        assertThat(marksAdded).isEqualTo(1);
    }

    @Test
    public void shouldUnmarkGraph() {
        Graph<String> graph = new Graph<>();
        Node<String> A = graph.createNode("A").marked(m);
        Node<String> B = graph.createNode("B").marked(m).linkedTo(A);
        Node<String> C = graph.createNode("C").linkedTo(A);

        int marksRemoved = graph.unmark(m);

        assertThat(A).is(unmarked);
        assertThat(B).is(unmarked);
        assertThat(C).is(unmarked);
        assertThat(marksRemoved).isEqualTo(2);
    }
}

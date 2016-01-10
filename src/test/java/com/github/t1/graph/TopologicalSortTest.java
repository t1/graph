package com.github.t1.graph;

import static com.github.t1.graph.GraphConditions.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class TopologicalSortTest {
    @Test
    public void shouldSortEmptyGraph() {
        Graph<?> graph = new Graph<>();

        graph.toplologicalSort();

        assertThat(graph).is(empty);
    }

    @Test
    public void shouldSortSingleNode() {
        Graph<String> graph = new Graph<>();
        Node<String> A = graph.createNode("A");

        graph.toplologicalSort();

        assertThat(graph).has(nodes(A));
    }

    @Test
    public void shouldSortTwoNodes() {
        Graph<String> graph = new Graph<>();
        Node<String> B = graph.createNode("B");
        Node<String> A = graph.createNode("A").linkedTo(B);
        assertThat(graph).as("before").has(nodes(B, A));

        graph.toplologicalSort();

        assertThat(graph).as("sorted").has(nodes(A, B));
    }

    @Test
    public void shouldSortThreeNodesReverse() {
        Graph<String> graph = new Graph<>();
        Node<String> C = graph.createNode("C");
        Node<String> B = graph.createNode("B").linkedTo(C);
        Node<String> A = graph.createNode("A").linkedTo(B);
        assertThat(graph).as("before").has(nodes(C, B, A));

        graph.toplologicalSort();

        assertThat(graph).as("sorted").has(nodes(A, B, C));
        assertThat(graph).hasToString("A -> {B}\nB -> {C}\nC -> {}");
    }

    @Test
    public void shouldSortPartiallySortedThreeNodes() {
        Graph<String> graph = new Graph<>();
        Node<String> A = graph.createNode("A");
        Node<String> C = graph.createNode("C");
        Node<String> B = graph.createNode("B").linkedTo(C);
        A.linkedTo(B);
        assertThat(graph).as("before").has(nodes(A, C, B));

        graph.toplologicalSort();

        assertThat(graph).as("sorted").has(nodes(A, B, C));
        assertThat(graph).hasToString("A -> {B}\nB -> {C}\nC -> {}");
    }

    @Test
    public void shouldFindCycleInThree() {
        Graph<String> graph = new Graph<>();
        Node<String> C = graph.createNode("C");
        Node<String> B = graph.createNode("B").linkedTo(C);
        Node<String> A = graph.createNode("A").linkedTo(B);
        C.linkedTo(A);
        assertThat(graph).as("before").has(nodes(C, B, A));

        Throwable throwable = catchThrowable(() -> graph.toplologicalSort());

        assertThat(throwable).is(cyclesFoundExceptionWithNodes(C));
        assertThat(graph).as("after")
                .has(nodes(C, A, B))
                .hasToString("C -> {A}\nA -> {B}\nB -> {C}");
    }

    @Test
    public void shouldFindCycleInOne() {
        Graph<String> graph = new Graph<>();
        Node<String> A = graph.createNode("A");
        A.linkedTo(A);
        assertThat(graph).as("before")
                .has(nodes(A))
                .hasToString("A -> {A}");

        Throwable throwable = catchThrowable(() -> graph.toplologicalSort());

        assertThat(throwable).is(cyclesFoundExceptionWithNodes(A));
        assertThat(graph).as("after")
                .has(nodes(A))
                .hasToString("A -> {A}");
    }

    @Test
    public void shouldFindTwoCyclesInFive() {
        Graph<String> graph = new Graph<>();
        Node<String> E = graph.createNode("E");
        Node<String> D = graph.createNode("D").linkedTo(E);
        Node<String> C = graph.createNode("C").linkedTo(D);
        Node<String> B = graph.createNode("B").linkedTo(C);
        Node<String> A = graph.createNode("A").linkedTo(B);
        C.linkedTo(A);
        E.linkedTo(B);
        assertThat(graph).as("before")
                .has(nodes(E, D, C, B, A))
                .hasToString("E -> {B}\nD -> {E}\nC -> {D, A}\nB -> {C}\nA -> {B}");

        Throwable throwable = catchThrowable(() -> graph.toplologicalSort());

        assertThat(throwable).is(cyclesFoundExceptionWithNodes(E, B));
        assertThat(graph).as("after")
                .has(nodes(E, B, C, A, D))
                .hasToString("E -> {B}\nB -> {C}\nC -> {D, A}\nA -> {B}\nD -> {E}");
    }
}

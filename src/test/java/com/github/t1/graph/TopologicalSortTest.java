package com.github.t1.graph;

import static com.github.t1.graph.GraphConditions.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.*;

public class TopologicalSortTest {

    private final Graph<String> graph = new Graph<>();

    @After
    public void after() {
        assertThat(graph).has(noMarks);
    }

    @Test
    public void shouldSortEmptyGraph() {
        Graph<?> graph = new Graph<>();

        graph.topologicalSort();

        assertThat(graph).is(empty);
    }

    @Test
    public void shouldSortSingleNode() {
        Node<String> A = graph.createNode("A");

        graph.topologicalSort();

        assertThat(graph).has(nodes(A));
    }

    @Test
    public void shouldSortTwoNodes() {
        Node<String> B = graph.createNode("B");
        Node<String> A = graph.createNode("A").linkedTo(B);
        assertThat(graph).as("before").has(nodes(B, A));

        graph.topologicalSort();

        assertThat(graph).as("sorted").has(nodes(A, B));
    }

    @Test
    public void shouldSortThreeNodesReverse() {
        Node<String> C = graph.createNode("C");
        Node<String> B = graph.createNode("B").linkedTo(C);
        Node<String> A = graph.createNode("A").linkedTo(B);
        assertThat(graph).as("before").has(nodes(C, B, A));

        graph.topologicalSort();

        assertThat(graph).as("sorted").has(nodes(A, B, C));
        assertThat(graph).hasToString("A -> {B}\nB -> {C}\nC -> {}");
    }

    @Test
    public void shouldSortPartiallySortedThreeNodes() {
        Node<String> A = graph.createNode("A");
        Node<String> C = graph.createNode("C");
        Node<String> B = graph.createNode("B").linkedTo(C);
        A.linkedTo(B);
        assertThat(graph).as("before").has(nodes(A, C, B));

        graph.topologicalSort();

        assertThat(graph).as("sorted").has(nodes(A, B, C));
        assertThat(graph).hasToString("A -> {B}\nB -> {C}\nC -> {}");
    }

    @Test
    public void shouldFindCycleInThree() {
        Node<String> C = graph.createNode("C");
        Node<String> B = graph.createNode("B").linkedTo(C);
        Node<String> A = graph.createNode("A").linkedTo(B);
        C.linkedTo(A);
        assertThat(graph).as("before").has(nodes(C, B, A));

        Throwable throwable = catchThrowable(graph::topologicalSort);

        assertThat(throwable).has(cycle(B, A, C));
        assertThat(graph).as("after")
                .has(nodes(C, B, A))
                .hasToString("C -> {A}\nB -> {C}\nA -> {B}");
    }

    @Test
    public void shouldFindCycleInOne() {
        Node<String> A = graph.createNode("A");
        A.linkedTo(A);
        assertThat(graph).as("before")
                .has(nodes(A))
                .hasToString("A -> {A}");

        Throwable throwable = catchThrowable(graph::topologicalSort);

        assertThat(throwable).has(cycle(A));
        assertThat(graph).as("after")
                .has(nodes(A))
                .hasToString("A -> {A}");
    }

    @Test
    public void shouldFindTwoCyclesInSixNodes() {
        Node<String> F = graph.createNode("F");
        Node<String> E = graph.createNode("E").linkedTo(F);
        Node<String> D = graph.createNode("D").linkedTo(E);
        Node<String> C = graph.createNode("C").linkedTo(D);
        Node<String> B = graph.createNode("B").linkedTo(C);
        Node<String> A = graph.createNode("A").linkedTo(B);
        C.linkedTo(A);
        E.linkedTo(D);
        F.linkedTo(F);
        assertThat(graph).as("before")
                .has(nodes(F, E, D, C, B, A))
                .hasToString("F -> {F}\nE -> {F, D}\nD -> {E}\nC -> {D, A}\nB -> {C}\nA -> {B}");

        Throwable throwable = catchThrowable(graph::topologicalSort);

        assertThat(throwable)
                .has(cycle(F))
                .has(cycle(D, E))
                .has(cycle(B, A, C))
                .hasMessage("found 3 cycle(s) in graph:\n" //
                        + "  F\n" //
                        + "  D -> E\n" //
                        + "  B -> A -> C");
        assertThat(graph).as("after")
                .has(nodes(F, E, D, C, B, A))
                .hasToString("F -> {F}\nE -> {F, D}\nD -> {E}\nC -> {D, A}\nB -> {C}\nA -> {B}");
    }

    @Test
    public void shouldFindTheLargerOfTwoCyclesInFive() {
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

        Throwable throwable = catchThrowable(graph::topologicalSort);

        assertThat(throwable).has(cycle(A, D, C, B, E));
        assertThat(graph).as("after")
                .has(nodes(E, D, C, B, A))
                .hasToString("E -> {B}\nD -> {E}\nC -> {D, A}\nB -> {C}\nA -> {B}");
    }
}

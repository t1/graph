package com.github.t1.graph;

import static com.github.t1.graph.GraphConditions.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import com.github.t1.graph.Graph.Node;

public class GraphBuildingTest {
    @Test
    public void shouldBuildEmpthGraph() {
        Graph<String> graph = new Graph<>();

        assertThat(graph).hasToString("");
        assertThat(graph).is(empty);
    }

    @Test
    public void shouldBuildGraphOfOne() {
        Graph<String> graph = new Graph<>();
        Node<String> A = graph.createNode("A");

        assertThat(A).has(noLinks);
        assertThat(graph).has(nodes(A));
        assertThat(graph).hasToString("A -> {}");
    }

    @Test
    public void shouldBuildGraphOfOneWithLinkToSelf() {
        Graph<String> graph = new Graph<>();
        Node<String> A = graph.createNode("A");
        A.linkedTo(A);

        assertThat(A).has(linksTo(A));
        assertThat(graph).has(nodes(A));
        assertThat(graph).hasToString("A -> {A}");
    }

    @Test
    public void shouldBuildGraphOfTwo() {
        Graph<String> graph = new Graph<>();
        Node<String> A = graph.createNode("A");
        Node<String> B = graph.createNode("B");
        A.linkedTo(B);

        assertThat(graph).hasToString("A -> {B}\nB -> {}");
    }

    @Test
    public void shouldBuildGraphOfTwoWithDoubleLink() {
        Graph<String> graph = new Graph<>();
        Node<String> A = graph.createNode("A");
        Node<String> B = graph.createNode("B");
        A.linkedTo(A);
        A.linkedTo(B);

        assertThat(graph).hasToString("A -> {A, B}\nB -> {}");
    }

    @Test
    public void shouldBuildGraphOfTwoWithCycle() {
        Graph<String> graph = new Graph<>();
        Node<String> A = graph.createNode("A");
        Node<String> B = graph.createNode("B").linkedTo(A);
        A.linkedTo(B);

        assertThat(graph).hasToString("A -> {B}\nB -> {A}");
    }

    @Test
    public void shouldBuildGraphOfThree() {
        Graph<String> graph = new Graph<>();
        Node<String> A = graph.createNode("A");
        Node<String> B = graph.createNode("B");
        Node<String> C = graph.createNode("C");
        A.linkedTo(B).linkedTo(C);

        assertThat(A).has(linksTo(B, C));
        assertThat(B).has(noLinks);
        assertThat(C).has(noLinks);
        assertThat(graph).has(nodes(A, B, C));
        assertThat(graph).hasToString("A -> {B, C}\nB -> {}\nC -> {}");
    }

    @Test
    public void shouldBuildGraphOfThreeWithCycle() {
        Graph<String> graph = new Graph<>();
        Node<String> A = graph.createNode("A");
        Node<String> B = graph.createNode("B");
        Node<String> C = graph.createNode("C").linkedTo(A);
        A.linkedTo(B);
        B.linkedTo(C);

        assertThat(graph).hasToString("A -> {B}\nB -> {C}\nC -> {A}");
    }
}

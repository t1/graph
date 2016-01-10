package com.github.t1.graph;

/** This is an interface, so you can define them, e.g., in an enum */
public interface Mark {
    default <T> boolean mark(Node<T> node) {
        return node.mark(this);
    }

    default <T> boolean isMarked(Node<T> node) {
        return node.isMarked(this);
    }

    default <T> boolean unmark(Node<T> node) {
        return node.unmark(this);
    }
}


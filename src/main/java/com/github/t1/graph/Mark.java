package com.github.t1.graph;

import lombok.Value;

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

    @Value
    public static class StringMark implements Mark {
        String string;

        @Override
        public String toString() {
            return string;
        }
    }

    @Value
    public static class Index implements Mark {
        public static int of(Node<?> node) {
            return node.getMark(Index.class).get().getIndex();
        }

        int index;
    }

}

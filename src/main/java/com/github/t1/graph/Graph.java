package com.github.t1.graph;

import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;

import java.util.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;

import com.github.t1.graph.Graph.Node.Mark;

import lombok.*;

@Data
public class Graph<T> {
    @SafeVarargs
    public static <T> String nodeNames(Node<T>... nodes) {
        return nodeNames(asList(nodes));
    }

    public static <T> String nodeNames(List<Node<T>> nodes) {
        return listToString(", ", nodes, node -> node.value.toString());
    }

    private static String listToString(String delimiter, List<?> list) {
        return listToString(delimiter, list, Object::toString);
    }

    private static <T> String listToString(String delimiter, List<T> list, Function<T, String> toString) {
        return String.join(delimiter, list.stream().map(toString).collect(toList()));
    }

    @Data
    public static class Node<T> {
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

        private T value;
        private List<Node<T>> links = new ArrayList<>();
        private List<Mark> marks = new ArrayList<>();

        private Node(T value) {
            this.value = value;
        }

        public Node<T> linkedTo(Node<T> target) {
            links.add(target);
            return this;
        }

        public boolean isLinkedTo(Collection<Node<T>> nodes) {
            return this.links.containsAll(nodes);
        }

        public Node<T> marked(Mark mark) {
            mark(mark);
            return this;
        }

        public boolean mark(Mark mark) {
            if (marks.contains(mark))
                return false;
            marks.add(mark);
            return true;
        }

        /** @return was it marked as this? */
        public boolean unmark(Mark mark) {
            return marks.remove(mark);
        }

        public boolean isMarked(Mark mark) {
            return marks.contains(mark);
        }

        @Override
        public String toString() {
            return value + (marks.isEmpty() ? "" : marks.toString()) + " -> {" + nodeNames(links) + "}";
        }
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
                    node.links.forEach(this);
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

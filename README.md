# Graph ![latest-version](https://maven-badges.herokuapp.com/maven-central/com.github.t1/graph/badge.svg) [![Build Status](https://github.com/t1/graph/actions/workflows/maven.yml/badge.svg)](https://github.com/t1/graph/actions/workflows/maven.yml)

A straight forward Graph class with Nodes that support custom Marks.

The most interesting part is the Graph#topologicalSort() method, which uses [Tarjan's Strongly Connected Components Algorithm](https://en.wikipedia.org/wiki/Tarjan%27s_strongly_connected_components_algorithm), so it can report exactly which cycles where found.

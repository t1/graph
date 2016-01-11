# Graph [![Download](https://api.bintray.com/packages/t1/javaee-helpers/graph/images/download.svg) ](https://bintray.com/t1/javaee-helpers/graph/_latestVersion) [![Build Status](https://travis-ci.org/t1/graph.svg)](https://travis-ci.org/t1/graph)

A straight forward Graph class with Nodes that support custom Marks.

The most interesting part is the Graph#topologicalSort() method, which uses [Tarjan's Strongly Connected Components Algorithm](https://en.wikipedia.org/wiki/Tarjan%27s_strongly_connected_components_algorithm), so it can report exactly which cycles where found.

# Shortest Path Map Engine

Finds the shortest route between nodes in a weighted directed graph
using Dijkstra's algorithm.

## How it works

- Represents a map as a weighted directed graph — nodes are locations,
  edges are paths with distances
- Uses a priority queue to drive Dijkstra's, giving O((E+V) log V) time
- Handles disconnected nodes and unreachable destinations

## Tech stack

- Java
- JUnit 5

## Running it

1. Clone the repo
2. Open in IntelliJ or Eclipse
3. Run `src/Main.java`

## Tests

Covers disconnected nodes, single-node graphs, and unreachable
destinations.

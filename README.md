# Shortest Path Map Engine

A graph-based navigation system that finds the most efficient route between nodes using Dijkstra's Algorithm.

## How It Works
- Models a map as a **Weighted Directed Graph** where nodes are locations and edges are paths with distances
- Uses a **Priority Queue** to optimize Dijkstra's, achieving O((E+V) log V) time complexity
- Handles edge cases including disconnected nodes and unreachable destinations

## Tech Stack
- Java
- JUnit 5 (unit testing)

## Running the Project
1. Clone the repo
2. Open in IntelliJ or Eclipse
3. Run `src/Main.java`

## Testing
JUnit 5 test suites cover:
- Disconnected nodes
- Single-node graphs
- Unreachable destinations

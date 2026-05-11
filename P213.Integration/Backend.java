import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Backend implementation for the project.
 * This class is in the default package as required.
 */
public class Backend implements BackendInterface {

    // Graph object passed in through the constructor
    private GraphADT<String, Double> graph;

    // Stores all locations read from the most recently loaded dot file
    private Set<String> locations;

    /**
     * Constructor required by BackendInterface comments.
     *
     * @param graph graph object used by the backend
     */
    public Backend(GraphADT<String, Double> graph) {
        this.graph = graph;
        this.locations = new LinkedHashSet<String>();
    }

    /**
     * Loads graph data from a dot file.
     * If older data was loaded before, clear the old tracked locations first.
     *
     * Note:
     * This backend tracks locations itself, because the provided placeholder
     * graph is not a fully functional graph implementation.
     *
     * @param filename path to the dot file
     * @throws IOException if the file cannot be read
     */
    @Override
    public void loadGraphData(String filename) throws IOException {
        clearTrackedData();

        Pattern edgePattern = Pattern.compile(
                "^\\s*\"([^\"]+)\"\\s*->\\s*\"([^\"]+)\"\\s*\\[minutes=(\\d+(?:\\.\\d+)?)\\];\\s*$"
        );

        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                // Ignore blank lines and generic digraph wrapper lines
                if (line.length() == 0 || line.startsWith("digraph") || line.equals("}")) {
                    continue;
                }

                Matcher matcher = edgePattern.matcher(line);
                if (matcher.matches()) {
                    String pred = matcher.group(1);
                    String succ = matcher.group(2);
                    Double weight = Double.valueOf(matcher.group(3));

                    // Track locations ourselves
                    locations.add(pred);
                    locations.add(succ);

                    // Try to keep graph somewhat in sync too
                    if (!graph.containsNode(pred)) {
                        graph.insertNode(pred);
                    }
                    if (!graph.containsNode(succ)) {
                        graph.insertNode(succ);
                    }

                    // With the provided placeholder, this may fail silently,
                    // but calling it still matches the intended backend behavior
                    graph.insertEdge(pred, succ, weight);
                }
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Unable to read graph data from file: " + filename, e);
        }
    }

    /**
     * Returns all locations read from the most recently loaded graph file.
     *
     * @return list of all location names
     */
    @Override
    public List<String> getListOfAll() {
        return new ArrayList<String>(locations);
    }

    /**
     * Returns the locations on the shortest path from start to end.
     * Returns an empty list if no such path exists.
     *
     * @param start start location
     * @param end end location
     * @return list of locations on shortest path, or empty list if none
     */
    @Override
    public List<String> findLocationsOnShortestPath(String start, String end) {
        if (start == null || end == null) {
            return new ArrayList<String>();
        }

        if (!graph.containsNode(start) || !graph.containsNode(end)) {
            return new ArrayList<String>();
        }

        try {
            List<String> path = graph.shortestPathData(start, end);
            if (path == null) {
                return new ArrayList<String>();
            }
            return new ArrayList<String>(path);
        } catch (NoSuchElementException e) {
            return new ArrayList<String>();
        }
    }

    /**
     * Returns the times between each pair of consecutive nodes
     * on the shortest path from start to end.
     * Returns an empty list if no such path exists.
     *
     * @param start start location
     * @param end end location
     * @return list of edge weights along the shortest path
     */
    @Override
    public List<Double> findTimesOnShortestPath(String start, String end) {
        List<Double> times = new ArrayList<Double>();

        if (start == null || end == null) {
            return times;
        }

        try {
            List<String> path = graph.shortestPathData(start, end);

            if (path == null || path.size() < 2) {
                return times;
            }

            for (int i = 0; i < path.size() - 1; i++) {
                times.add(graph.getEdge(path.get(i), path.get(i + 1)));
            }
        } catch (NoSuchElementException e) {
            return new ArrayList<Double>();
        }

        return times;
    }

    /**
     * Returns up to ten closest reachable locations from the given start.
     *
     * @param start start location
     * @return up to ten closest reachable locations
     * @throws NoSuchElementException if start is not in the graph, or if
     *         no other location is reachable
     */
    @Override
    public List<String> getTenClosestLocations(String start) throws NoSuchElementException {
        if (start == null) {
            throw new NoSuchElementException("Start location not found.");
        }

        if (!locations.contains(start)) {
            throw new NoSuchElementException("Start location not found.");
        }

        List<LocationDistance> reachable = new ArrayList<LocationDistance>();

        for (String location : locations) {
            if (location.equals(start)) {
                continue;
            }

            try {
                // Only keep locations where an actual path exists
                List<String> path = graph.shortestPathData(start, location);
                if (path != null && !path.isEmpty() && path.get(0).equals(start)) {
                    double cost = 0.0;
                    for (int i = 0; i < path.size() - 1; i++) {
                        cost += graph.getEdge(path.get(i), path.get(i + 1));
                    }
                    reachable.add(new LocationDistance(location, cost));
                }
            } catch (NoSuchElementException e) {
                // Ignore unreachable locations
            }
        }

        if (reachable.isEmpty()) {
            throw new NoSuchElementException("No reachable locations found.");
        }

        Collections.sort(reachable);

        List<String> result = new ArrayList<String>();
        int limit = Math.min(10, reachable.size());
        for (int i = 0; i < limit; i++) {
            result.add(reachable.get(i).location);
        }

        return result;
    }

    /**
     * Clears the locations tracked by the backend.
     * Also tries to remove them from the graph.
     */
    private void clearTrackedData() {
        List<String> oldLocations = new ArrayList<String>(locations);
        for (String location : oldLocations) {
            graph.removeNode(location);
        }
        locations.clear();
    }

    /**
     * Small helper object for sorting locations by distance.
     */
    private static class LocationDistance implements Comparable<LocationDistance> {
        private String location;
        private double distance;

        private LocationDistance(String location, double distance) {
            this.location = location;
            this.distance = distance;
        }

        @Override
        public int compareTo(LocationDistance other) {
            int byDistance = Double.compare(this.distance, other.distance);
            if (byDistance != 0) {
                return byDistance;
            }
            return this.location.compareTo(other.location);
        }
    }
}
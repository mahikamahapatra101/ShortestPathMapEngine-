import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This is the interface that a backend developer will implement, so that a
 * frontend developer's code can make use of this functionality. It makes use of
 * a GraphADT to perform shortest path computations.
 */
public interface BackendInterface {

    /*
    * Implementing classes should support the constructor below.
    * @param graph object to store the backend's graph data
    */
    // public Backend(GraphADT<String,Double> graph);

    /**
     * Loads graph data from a dot file. If a graph was previously loaded, this
     * method should first delete the contents (nodes and edges) of the existing
     * graph before loading a new one.
     * @param filename the path to a dot file to read graph data from
     * @throws IOException if there was any problem reading from this file
     */
    public void loadGraphData(String filename) throws IOException;

    /**
     * Returns a list of all locations in the graph.
     * @return list of all location names
     */
    public List<String> getListOfAll();

    /**
     * Return the sequence of locations along the shortest path from start to
     * end, or an empty list if no such path exists.
     * @param start the start of the path
     * @param end the end of the path
     * @return a list with the nodes along the shortest path from start to end,
     *         or an empty list if no such path exists
     */
    public List<String> findLocationsOnShortestPath(String start, String end);

    /**
     * Return the times in minutes between each two nodes on the shortest path
     * from start to end, or an empty list if no such path exists.
     * @param start the start of the path
     * @param end the end of the path
     * @return a list with the times in minutes between two nodes along the
     * shortest path from start to end, or an empty list if no such path exists
     */
    public List<Double> findTimesOnShortestPath(String start, String end);

    /**
     * Returns a list of the ten closest locations from the specified start.
     * @param start the location to find the closest locations from
     * @return the ten closest locations from the specified start
     * @throws NoSuchElementException if start does not exist, or if there are
     *         no other locations that can be reached from the start
     */
    public List<String> getTenClosestLocations(String start) throws NoSuchElementException;
}
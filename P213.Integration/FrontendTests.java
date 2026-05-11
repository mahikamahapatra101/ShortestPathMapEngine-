/* 
 * Author: Mahika Mahapatra 
 * Email: mmahapatra2@wisc.edu 
 * Assignment: Program P209.RoleCode
 * Course: Compsci400 Date: 3/22/2026
 * Citations: (worked on by self)
 */

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

/**
 * JUnit test class for the Frontend implementation.
 */
public class FrontendTests {

    /**
     * This tests generateShortestPathPromptHTML and generateTenClosestLocationsPromptHTML.
     * Checks that the shortest path prompt contains: an input with id="start", an input with id="end", and a button labelled "Find Shortest Path".
     * Checks that the ten closest locations prompt contains: an input with id="from", and a button labelled "Ten Closest Locations"
     */
    @Test
    public void roleTest1() {
        Graph_Placeholder graph = new Graph_Placeholder();
        Backend_Placeholder backend = new Backend_Placeholder(graph);
        Frontend frontend = new Frontend(backend);

        //check shortest path prompt
        String spPrompt = frontend.generateShortestPathPromptHTML();
        assertNotNull(spPrompt); //should not be null
        assertTrue(spPrompt.contains("id=\"start\"")); //must have start input
        assertTrue(spPrompt.contains("id=\"end\"")); ///must have end input
        assertTrue(spPrompt.contains("Find Shortest Path"));// must have correct button label

        // check ten closest locations prompt
        String tclPrompt = frontend.generateTenClosestLocationsPromptHTML();
        assertNotNull(tclPrompt); //should not be null
        assertTrue(tclPrompt.contains("id=\"from\"")); //must have from inputt
        assertTrue(tclPrompt.contains("Ten Closest Locations")); //must have correct button label
    }

    /**
     * This tests generateShortestPathResponseHTML with a valid path,
     * uses the placeholder graph's pre-loaded nodes to request the path from "Union South" to "Weeks Hall for Geological Sciences".
     * Checks that the response HTML contains: an ordered list of stops, both the start and end city names, 
     * the intermediate stop "Computer Sciences and Statistics", and total travel time in minutes.
     */
    @Test
    public void roleTest2() {
        Graph_Placeholder graph = new Graph_Placeholder();
        Backend_Placeholder backend = new Backend_Placeholder(graph);
        Frontend frontend = new Frontend(backend);

        String start = "Union South";
        String end = "Weeks Hall for Geological Sciences";
        String response = frontend.generateShortestPathResponseHTML(start, end);

        assertNotNull(response); // should not be null
        assertTrue(response.contains("<ol>")); // must have ordered list
        assertTrue(response.contains(start)); // must show start city
        assertTrue(response.contains(end)); // must show end city
        assertTrue(response.contains("Computer Sciences and Statistics")); // must show intermediate stop
        assertTrue(response.contains("minutes")); // must show total travel time
    }

   /**
    * This tests generateTenClosestLocationsResponseHTML with a valid start.
    * Uses the placeholder graph's pre-loaded nodes to request the ten closest locations from "Union South".
    * Checks that the response HTML contains: an unordered list, the start city name, at least one list item, and a known city from the placeholder graph.
    */
    @Test
    public void roleTest3() {
        Graph_Placeholder graph = new Graph_Placeholder();
        Backend_Placeholder backend = new Backend_Placeholder(graph);
        Frontend frontend = new Frontend(backend);

        String start = "Union South";
        String response = frontend.generateTenClosestLocationsResponseHTML(start);

        assertNotNull(response); // should not be null
        assertTrue(response.contains("<ul>")); // must have unordered list
        assertTrue(response.contains(start)); // must show start city
        assertTrue(response.contains("<li>")); // must have at least one list item
        assertTrue(response.contains("Computer Sciences and Statistics")); // must show a known city
    }




     /**
     * This test verifies that the generateShortestPathResponseHTML method works
     * correctly when it's connected to the real Backend and DijkstraGraph loaded with
     * actual europeanRail.dot data. It also checks that the response contains the
     * start and end city names and travel time.
     */
    @Test
    public void testShortestPathIntegration() throws Exception {
        
        //this sets up real graph, backend, and frontend
        GraphADT<String, Double> graph = new DijkstraGraph<>();
        BackendInterface backend = new Backend(graph);
        backend.loadGraphData("./europeanRail.dot");
        Frontend frontend = new Frontend(backend); //frontend wired to backend

        //requestas shortest path btw 2 real stations
        String start = "Amsterdam";
        String end = "Cologne";
        String response = frontend.generateShortestPathResponseHTML(start, end);

       
        assertNotNull(response); //response not null
        assertTrue(response.contains(start)); //response has start
        assertTrue(response.contains(end)); //response has end
        assertTrue(response.contains("minutes")); //response has word minutes in it
    }

    /**
     * This test verifies that the generateTenClosestLocationsResponseHTML method
     * works correctly with the real Backend and DijkstraGraph loaded with
     * the actual europeanRail.dot data.
     */
    @Test
    public void testTenClosestLocationsIntegration() throws Exception {
        
        //this sets up real graph, backend, and frontend
        GraphADT<String, Double> graph = new DijkstraGraph<>();
        BackendInterface backend = new Backend(graph);
        backend.loadGraphData("./europeanRail.dot");
        Frontend frontend = new Frontend(backend);

        //requests ten closest locations from a real station (takes a start and returns nearby locations)
        String start = "Amsterdam";
        String response = frontend.generateTenClosestLocationsResponseHTML(start); 

        //this checks that the response is in the correect/expected html structure
        assertNotNull(response); //response not null
        assertTrue(response.contains("<ul>")); //response has unordered list
        assertTrue(response.contains("<li>")); //response has at least one item in list
        assertTrue(response.contains(start)); //response has start
    }

    /**
     * This test  verifies that the shortest path btw Amsterdam and
     * Frankfurt using the real Backend and DijkstraGraph contains both endpoints
     * and is not empty.
     */
     @Test
     public void testShortestPathContainsEndpointsIntegration() throws Exception {
        
       //this sets up real graph, backend, and frontend
       GraphADT<String, Double> graph = new DijkstraGraph<>();
       BackendInterface backend = new Backend(graph);
       backend.loadGraphData("./europeanRail.dot");

       //find shortest path locations between two real stations
       List<String> path = backend.findLocationsOnShortestPath("Amsterdam", "Frankfurt");

       
       assertNotNull(path); //path not null
       assertFalse(path.isEmpty()); //pat not empty
       assertEquals("Amsterdam", path.get(0)); //first element in path must be the start
       assertEquals("Frankfurt", path.get(path.size() - 1)); //last element in path must be the destination
    }

    /**
     * This test verifies that the getTenClosestLocations method returns at most 10
     * results and does not include the start location itself, using real data.
     */
    @Test
    public void testTenClosestSizeIntegration() throws Exception {   

        //this sets up real graph, backend, and frontend
        GraphADT<String, Double> graph = new DijkstraGraph<>();
        BackendInterface backend = new Backend(graph);
        backend.loadGraphData("./europeanRail.dot");

        //get closest locations from a real station
        List<String> closest = backend.getTenClosestLocations("Amsterdam");

    
        assertNotNull(closest); //result should not be null
        assertFalse(closest.isEmpty()); //check found at least one nearby location
        assertTrue(closest.size() <= 10); //check does not return more than 10 locations
        assertFalse(closest.contains("Amsterdam")); // make sure start city does not appear in its own closest list
    }



}
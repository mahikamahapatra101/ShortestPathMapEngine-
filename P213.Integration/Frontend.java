/*
 * Author: Mahika Mahapatra
 * Email: mmahapatra2@wisc.edu
 * Assignment: Program P209.RoleCode
 * Course: Compsci400 Date: 3/22/2026
 * Citations: (worked on by self)
 */

import java.util.List;
import java.util.NoSuchElementException;

/**
 * This is a class for the frontend implementation for the European train route shortest path webapp,
 * it generates HTML fragments for user interaction with the backend.
 */
public class Frontend implements FrontendInterface {

    private BackendInterface backend;

    /**
     * Constructs a frontend with the given backend for shortest path computations
     * @param backend is used for shortest path computations.
     */
    public Frontend(BackendInterface backend) {
        this.backend = backend;
    }

    /**
     * Returns an HTML fragment with input controls that are for requesting a shortest path, and
     * includes labeled text fields for both start and end locations and a submit button.
     * @return HTML string with input controls for a shortest path computation.
     */
    @Override
    public String generateShortestPathPromptHTML() {
        return "<div>\n" +
            "  <label for=\"start\">Start Location:</label>\n" +
            "  <input type=\"text\" id=\"start\" name=\"start\" " +
            "placeholder=\"Enter start city\" />\n" +
            "  <label for=\"end\">End Location:</label>\n" +
            "  <input type=\"text\" id=\"end\" name=\"end\" " +
            "placeholder=\"Enter end city\" />\n" +
            "  <button type=\"button\">Find Shortest Path</button>\n" +
            "</div>";
    }

    /**
     * Returns an HTML fragment showing the shortest path result between two locations.
     * Displays start/end info, ordered list of stops, and total travel time.
     * If inputs are null, returns an error. If no path exists or inputs are
     * invalid, returns an error message instead.
     * @param start is the starting location to find a shortest path from
     * @param end is the end location that this shortest path should end at
     * @return HTML string for the shortest path between these two locations
     */
    @Override
    public String generateShortestPathResponseHTML(String start, String end) {
        if (start == null || end == null) {
            return "<p>Error: start and end locations must not be null.</p>";
        }
        try {
            List<String> locations = backend.findLocationsOnShortestPath(start, end);
            List<Double> times = backend.findTimesOnShortestPath(start, end);

            if (locations == null || locations.isEmpty()) {
                return "<p>No path found between <em>" + start + "</em> and <em>" + end + "</em>.</p>";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("<p>Shortest path from <strong>").append(start)
              .append("</strong> to <strong>").append(end).append("</strong></p>\n");
            sb.append("<ol>\n");

            for (int i = 0; i < locations.size(); i++) {
                sb.append("  <li>").append(locations.get(i)).append("</li>\n");
            }
            sb.append("</ol>\n");

            double total = 0.0;
            for (int i = 0; i < times.size(); i++) {
                total += times.get(i);
            }

            sb.append("<p>Total travel time: <strong>").append(total).append(" minutes</strong></p>");
            return sb.toString();

        } catch (NoSuchElementException e) {
            return "<p>Error: could not find path, one or both locations may not exist in the graph</p>";
        } catch (Exception e) {
            return "<p>Error: " + e.getMessage() + "</p>";
        }
    }

    /**
     * Returns an HTML fragment with input controls for requesting the ten closest locations.
     * Includes a labeled text field for the start location and a submit button.
     * @return HTML string with input controls for a ten closest locations request
     */
    @Override
    public String generateTenClosestLocationsPromptHTML() {
        return "<div>\n" +
            "  <label for=\"from\">Start Location:</label>\n" +
            "  <input type=\"text\" id=\"from\" name=\"from\" " +
            "placeholder=\"Enter a city\" />\n" +
            "  <button type=\"button\">Ten Closest Locations</button>\n" +
            "</div>";
    }

    /**
     * Returns an HTML fragment showing the ten closest locations from a start city.
     * Displays the start location and an unordered list of the closest cities.
     * If start is null, returns an error. If no locations are found or the start
     * is invalid, returns an error message.
     * @param start is the location to find close locations from
     * @return HTML string for the closest locations from the specified start
     */
    @Override
    public String generateTenClosestLocationsResponseHTML(String start) {
        if (start == null) {
            return "<p>Error: start location must not be null.</p>";
        }
        try {
            List<String> closest = backend.getTenClosestLocations(start);

            if (closest == null || closest.isEmpty()) {
                return "<p>No nearby locations found from <em>" + start + "</em>.</p>";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("<p>Ten closest locations from <strong>").append(start).append("</strong></p>\n");
            sb.append("<ul>\n");

            for (int i = 0; i < closest.size(); i++) {
                sb.append("  <li>").append(closest.get(i)).append("</li>\n");
            }
            sb.append("</ul>");

            return sb.toString();
        } catch (NoSuchElementException e) {
            return "<p>Error: Location <em>" + start + "</em> was not found in the graph.</p>";
        } catch (Exception e) {
            return "<p>Error: " + e.getMessage() + "</p>";
        }
    }
   
   

}
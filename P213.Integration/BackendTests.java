import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

/**
 * JUnit tests for the Backend class.
 * This file is in the default package as required.
 */
public class BackendTests {

    /**
     * This test checks that loadGraphData() reads locations from a dot file
     * and that getListOfAll() returns the locations that were loaded.
     *
     * Because the provided Graph_Placeholder is not fully functional, this test
     * focuses on the backend's file parsing and internal location tracking.
     */
    @Test
    public void roleTest1() throws IOException {
        Backend backend = new Backend(new Graph_Placeholder());

        Path file = Files.createTempFile("backendTest1", ".dot");
        Files.writeString(file,
                "digraph europeanRail {\n"
                        + "\"Union South\" -> \"Computer Sciences and Statistics\" [minutes=1];\n"
                        + "\"Computer Sciences and Statistics\" -> \"Weeks Hall for Geological Sciences\" [minutes=2];\n"
                        + "}\n");

        backend.loadGraphData(file.toString());
        List<String> locations = backend.getListOfAll();

        assertEquals(3, locations.size());
        assertTrue(locations.contains("Union South"));
        assertTrue(locations.contains("Computer Sciences and Statistics"));
        assertTrue(locations.contains("Weeks Hall for Geological Sciences"));
    }

    /**
     * This test checks shortest path behavior using the provided
     * Graph_Placeholder's built-in hardcoded path.
     *
     * It verifies that findLocationsOnShortestPath() returns the expected list
     * of locations and that findTimesOnShortestPath() returns the matching
     * edge weights from that placeholder path.
     */
    @Test
    public void roleTest2() {
        Backend backend = new Backend(new Graph_Placeholder());

        assertIterableEquals(
                Arrays.asList(
                        "Union South",
                        "Computer Sciences and Statistics",
                        "Weeks Hall for Geological Sciences"
                ),
                backend.findLocationsOnShortestPath(
                        "Union South",
                        "Weeks Hall for Geological Sciences"
                )
        );

        assertIterableEquals(
                Arrays.asList(1.0, 2.0),
                backend.findTimesOnShortestPath(
                        "Union South",
                        "Weeks Hall for Geological Sciences"
                )
        );
    }

    /**
     * This test checks getTenClosestLocations() using the provided
     * Graph_Placeholder.
     *
     * Since the placeholder only has a small hardcoded path, the result will
     * contain fewer than ten locations. This test also checks that a missing
     * start location throws NoSuchElementException.
     */
    @Test
    public void roleTest3() throws IOException {
        Backend backend = new Backend(new Graph_Placeholder());

        // Load matching locations so backend's internal location set is populated
        Path file = Files.createTempFile("backendTest3", ".dot");
        Files.writeString(file,
                "digraph europeanRail {\n"
                        + "\"Union South\" -> \"Computer Sciences and Statistics\" [minutes=1];\n"
                        + "\"Computer Sciences and Statistics\" -> \"Weeks Hall for Geological Sciences\" [minutes=2];\n"
                        + "}\n");

        backend.loadGraphData(file.toString());

        assertIterableEquals(
                Arrays.asList(
                        "Computer Sciences and Statistics",
                        "Weeks Hall for Geological Sciences"
                ),
                backend.getTenClosestLocations("Union South")
        );

        assertThrows(
                NoSuchElementException.class,
                () -> backend.getTenClosestLocations("Missing Location")
        );
    }
}
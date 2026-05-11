import java.util.PriorityQueue;
import java.util.List;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/*
 * Author: Mahika Mahapatra
 * Email: mmahapatra2@wisc.edu
 * Assignment: Program P210.ShortestPath
 * Course: Compsci400 Date: 4/2/2026
 * Citations: NA- (Worked on by myself)
 */


/**
 * This class extends the BaseGraph data structure with additional methods for
 * computing the total cost and list of node data along the shortest path
 * connecting a provided starting to ending nodes. This class makes use of
 * Dijkstra's shortest path algorithm.
 */
public class DijkstraGraph<NodeType, EdgeType extends Number>
        extends BaseGraph<NodeType, EdgeType>
        implements GraphADT<NodeType, EdgeType> {

    /**
     * While searching for the shortest path between two nodes, a SearchNode
     * contains data about one specific path between the start node and another
     * node in the graph. The final node in this path is stored in its node
     * field. The total cost of this path is stored in its cost field. And the
     * predecessor SearchNode within this path is referenced by the predecessor
     * field (this field is null within the SearchNode containing the starting
     * node in its node field).
     *
     * SearchNodes are Comparable and are sorted by cost so that the lowest cost
     * SearchNode has the highest priority within a java.util.PriorityQueue.
     */
    protected class SearchNode implements Comparable<SearchNode> {
        public Node node;
        public double cost;
        public SearchNode pred;

        public SearchNode(Node startNode) {
            this.node = startNode;
            this.cost = 0;
            this.pred = null;
        }

        public SearchNode(SearchNode pred, Edge newEdge) {
            this.node = newEdge.succ;
            this.cost = pred.cost + newEdge.data.doubleValue();
            this.pred = pred;
        }

        public int compareTo(SearchNode other) {
            if (cost > other.cost)
                return +1;
            if (cost < other.cost)
                return -1;
            return 0;
        }
    }

    /**
     * Constructor that sets the map that the graph uses.
     */
    public DijkstraGraph() {
        super(new HashTableMap<>());
    }

    /**
     * Insert a new directed edge with a non-negative weight into the graph. If
     * an edge between pred and succ already exists, update the data stored in
     * that edge to the new weight.
     *
     * @param pred is the data contained in the new edge's predecesor node
     * @param succ is the data contained in the new edge's succ node
     * @param weight is the non-negative data to be stored in the new edge
     * @return true if the edge could be inserted or updated, or false if the
     * pred or succ data are not found in any graph nodes or the weight
     * specified is negative.
     */
    @Override
    public boolean insertEdge(NodeType pred, NodeType succ, EdgeType weight) {
        if (weight.doubleValue() < 0)
            return false;
        return super.insertEdge(pred, succ, weight);
    }

    /**
     * This helper method creates a network of SearchNodes while computing the
     * shortest path between the provided start and end locations. The
     * SearchNode that is returned by this method represents the end of the
     * shortest path that is found: it's cost is the cost of that shortest path,
     * and the nodes linked together through predecessor references represent
     * all of the nodes along that shortest path (ordered from end to start).
     *
     * @param start the starting node for the path
     * @param end   the destination node for the path
     * @return SearchNode for the final end node within the shortest path
     * @throws NoSuchElementException if either the start or the end node
     * cannot be found, or there is no path from start node to end node
     * @throws NullPointerException if the start or end node are null
     */
    protected SearchNode computeShortestPath(Node start, Node end) {
        //shortestPathData and shortestPathCost do null check before calling

        // pq = new PriorityQueue() —>> this orders SearchNodes by cost inc.
        PriorityQueue<SearchNode> pq = new PriorityQueue<>();

        //1st Node = the key type(the node we're tracking)
        //2nd Node = the value type(dummy dupe of same node)
        HashTableMap<Node, Node> visited = new HashTableMap<>(); //stores visited nodes

        pq.add(new SearchNode(start)); //cost set to 0 and pred set to null in constr. for startNode

        while (!pq.isEmpty()) {
            //dest = the current cheapest node
            SearchNode dest = pq.poll();//poll -> built in method,removes and returns cheapest
            if (!visited.containsKey(dest.node)) { //check if dest is unvisited
                visited.put(dest.node, dest.node); // adds the dest node to visited map (put() is method from HashTableMap)
                if (dest.node == end) {
                    return dest; // can do dest.cost to find total cost from start to end
                }
                //iterates once for each neighbor that dest points to
                for (int i = 0; i < dest.node.edgesLeaving.size(); i++) { //edgesLeaving -> path btw nodes that current node points to
                    //get each edge one by one of the nodes that dest points to
                    Edge edge = dest.node.edgesLeaving.get(i);
                    if (!visited.containsKey(edge.succ)) { //check if node that dest points to is visited or not
                        //creates new SearchNode for neighbor and adds to pq
                        pq.add(new SearchNode(dest, edge));
                    }
                }
            }
        }
        //if start or end does not exsist is taken care of in shortestPathCost and shortestPathData
         throw new NoSuchElementException("no path exists btw start and end node"); //if while loop finishes w/o finding end node

    }

    /**
     * Returns the list of data values from nodes along the shortest path
     * from the node with the provided start value through the node with the
     * provided end value. This list of data values starts with the start
     * value, ends with the end value, and contains intermediary values in the
     * order they are encountered while traversing this shortest path. This
     * method uses Dijkstra's shortest path algorithm to find this solution.
     *
     * @param start the data item in the starting node for the path
     * @param end   the data item in the destination node for the path
     * @return list of data item from nodes along this shortest path
     * @throws NoSuchElementException if either the start or the end node
     * cannot be found, or there is no path from start node to end node
     * @throws NullPointerException if the start or end node are null
     */
    public List<NodeType> shortestPathData(NodeType start, NodeType end) {
        //null check
        if (start == null || end == null) {
            throw new NullPointerException("start or end should not be null");
        }

        //nodes.get() throws NoSuchElementException
        Node startNode = nodes.get(start);
        Node endNode = nodes.get(end);


        SearchNode endSearch = computeShortestPath(startNode, endNode);
        LinkedList<NodeType> path = new LinkedList<>();
        SearchNode current = endSearch; // important to not lose original endSearch

        //add to list
        while (current != null) {
            path.addFirst(current.node.data);
            current = current.pred; //pred is predecessor of linked list
        }
        return path;


    }

    /**
     * Returns the cost of the path (sum over edge weights) of the shortest
     * path from the node containing the start data to the node containing the
     * end data. This method uses Dijkstra's shortest path algorithm to find
     * this solution.
     *
     * @param start the data item in the starting node for the path
     * @param end   the data item in the destination node for the path
     * @return the cost of the shortest path between these nodes
     * @throws NoSuchElementException if either the start or the end node
     * cannot be found, or there is no path from start node to end node
     * @throws NullPointerException if the start or end node are null
     */
    public double shortestPathCost(NodeType start, NodeType end) {
        //null check
        if (start == null || end == null) {
            throw new NullPointerException("start or end should not be null");
        }
        //nodes.get() throws NoSuchElementException
        Node startNode = nodes.get(start);
        Node endNode = nodes.get(end);

        SearchNode endSearch = computeShortestPath(startNode, endNode);
        return endSearch.cost;


    }
    /**
    * This test tests the lecture example graph shortest path from A to H and the shortest path
    * of a different start and end (C to E),
    * test also verifies that both the cost and exact node sequence do match the lecture table for A to H
    */
    @Test
    public void testLectureExampleShortestPath() {

        DijkstraGraph<String, Integer> graph = new DijkstraGraph<>(); // node->string and edge->integer
        //lecture graph, insert nodes
        graph.insertNode("A");
        graph.insertNode("B");
        graph.insertNode("C");
        graph.insertNode("D");
        graph.insertNode("E");
        graph.insertNode("F");
        graph.insertNode("G");
        graph.insertNode("H");

        //add edges from lecture
        graph.insertEdge("A", "B", 4);
        graph.insertEdge("A", "E", 15);
        graph.insertEdge("A", "C", 2);
        graph.insertEdge("B", "E", 10);
        graph.insertEdge("B", "D", 1);
        graph.insertEdge("C", "D", 5);
        graph.insertEdge("D", "E", 3);
        graph.insertEdge("D", "F", 0);
        graph.insertEdge("F", "D", 2);
        graph.insertEdge("F", "H", 4);
        graph.insertEdge("G", "H", 4);

        //lecture ex A->H
        assertEquals(9, (int) graph.shortestPathCost("A", "H")); //typecast + check 9 is shortest path cost from A to H
        List<String> pathAH = graph.shortestPathData("A", "H"); //get actual path returned by method
        //check that our path returned by method does equal path from lecture
        assertEquals(List.of("A", "B", "D", "F", "H"), pathAH);//compares list we have to expected list created by built in List.of fuct.

        //different start and end C->E
        assertEquals(8, (int) graph.shortestPathCost("C", "E")); //C->D->E, shortest path cost 5+3=8
        List<String> pathCE = graph.shortestPathData("C", "E"); //act. path returned by method
        assertEquals(List.of("C", "D", "E"), pathCE);//check method returned path = expected path

    }

    /**
    * This test tests that a NoSuchElementException is thrown when no path
    * exists between two nodes, uses lecture ex A->G (G is in the graph but unreachable from A)
    */
    @Test
    public void testNoPathExists() {
        DijkstraGraph<String, Integer> graph = new DijkstraGraph<>();

        //lecture graph, insert nodes
        graph.insertNode("A");
        graph.insertNode("B");
        graph.insertNode("C");
        graph.insertNode("D");
        graph.insertNode("E");
        graph.insertNode("F");
        graph.insertNode("G");
        graph.insertNode("H");

        //add edges from lecture
        graph.insertEdge("A", "B", 4);
        graph.insertEdge("A", "E", 15);
        graph.insertEdge("A", "C", 2);
        graph.insertEdge("B", "E", 10);
        graph.insertEdge("B", "D", 1);
        graph.insertEdge("C", "D", 5);
        graph.insertEdge("D", "E", 3);
        graph.insertEdge("D", "F", 0);
        graph.insertEdge("F", "D", 2);
        graph.insertEdge("F", "H", 4);
        graph.insertEdge("G", "H", 4);


        //.class needed to show type of exept. over actual obj
        //lambda, telling compiler to run graph.shortestPathCost("A", "G") to check that it throws no such element excpetion
        assertThrows(NoSuchElementException.class,() -> graph.shortestPathCost("A", "G"));
        assertThrows(NoSuchElementException.class,() -> graph.shortestPathData("A", "G"));
}



    /**
    * This test tests that  NoSuchElementException is thrown when either the start
    * or end node does not exist in the graph,the node M is never inserted so NoSuchElementException should be thrown
    */
    @Test
    public void testMissingNodes() {
        DijkstraGraph<String, Integer> graph = new DijkstraGraph<>();
        //lecture graph, insert nodes
        graph.insertNode("A");
        graph.insertNode("B");
        graph.insertNode("C");
        graph.insertNode("D");
        graph.insertNode("E");
        graph.insertNode("F");
        graph.insertNode("G");
        graph.insertNode("H");

        //add edges from lecture
        graph.insertEdge("A", "B", 4);
        graph.insertEdge("A", "E", 15);
        graph.insertEdge("A", "C", 2);
        graph.insertEdge("B", "E", 10);
        graph.insertEdge("B", "D", 1);
        graph.insertEdge("C", "D", 5);
        graph.insertEdge("D", "E", 3);
        graph.insertEdge("D", "F", 0);
        graph.insertEdge("F", "D", 2);
        graph.insertEdge("F", "H", 4);
        graph.insertEdge("G", "H", 4);

        // M does not exist in graph —> nodes.get() ( get() is from Map ADT) throws NoSuchElementException
        assertThrows(NoSuchElementException.class, () -> graph.shortestPathCost("A", "M")); //end node missing
        assertThrows(NoSuchElementException.class,() -> graph.shortestPathCost("Z", "M")); //start node missing
        assertThrows(NoSuchElementException.class,() -> graph.shortestPathData("A", "M")); //end node missing
        assertThrows(NoSuchElementException.class,() -> graph.shortestPathData("M", "A")); //start node missing
}


}
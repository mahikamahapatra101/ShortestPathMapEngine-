import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.io.File;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Stream;

/**
 * Web application integrating Frontend, Backend, DijkstraGraph, and
 * HashtableMap implementations to serve a navigation interface over HTTP.
 *
 * On your GoogleVM (after setting the firewall to allow http traffic):
 *     - compile your WebApp in that location with make compileServer
 *     - run your webserver using the command: sudo java WebApp 80
 *     - then direct your browser to http://EXTERNAL_IP/
 *
 * On the Department (CSL) Linux Machines:
 *     - copy all your P213 files to /afs/cs.wisc.edu/p/cs400-web/CS_LOGIN/
 *     - compile your WebApp in that location with make compileServer
 *     - there is no need to run your server, the provided index.cgi handles this
 *     - then direct your browser to https://cs400-web.cs.wisc.edu/CS_LOGIN/
 */
public class WebApp {

    /**
     * Starts the HTTP server on the specified port, or processes a single query
     * string when a non-numeric argument is provided.
     *
     * @param args command line argument a port number to bind the server to,
     *     or a URL-encoded query string to process directly to stdout
     * @throws IOException if the server cannot be created on the given port
     */
    public static void main(String[] args) throws IOException {
                if(args.length != 1) {
                        throw new IllegalArgumentException("You must pass a command line" +
                                " argument representing the port that this server should be" +
                                " bound to when running this program.  Or a Query string.");
                }
                int portNumber = -1;
                try {
                        portNumber = Integer.parseInt(args[0]);
                } catch(NumberFormatException e) {
                        // non-numeric argument signals CGI mode used by index.cgi on CSL
                        handleSingleResponse(args[0]);
                        return;
                }

                InetSocketAddress address = new InetSocketAddress(portNumber);
                HttpServer server = HttpServer.create(address,8);
                HttpContext context = server.createContext("/");
                context.setHandler( WebApp::requestHandler );
                System.out.println("Starting European Rail Navigator Server...");
                server.start();
    }

    /**
     * Handles an incoming HTTP request by generating and returning a response.
     *
     * @param exchange the HTTP exchange containing the request URI and response
     */
    public static void requestHandler(HttpExchange exchange) {
                try {
                        String query = exchange.getRequestURI().getQuery();
                        System.out.println("Received Request with query: " + query);
                        Map<String,String> keyValuePairs = parseQuery(
                                                                exchange.getRequestURI().getQuery());
                        System.out.println("Query includes args: " + keyValuePairs);

                        // build complete HTML page from query parameters
                        FrontendInterface frontend = createWorkingFrontend("./europeanRail.dot");
                        String response = generateResponseHTML(keyValuePairs,frontend);
                        String prompts = generatePromptHTML(frontend);
                        String html = composeHTML(response,prompts);

                        // content-length must be known before sending response headers
                        byte[] bytes = html.getBytes();
                        exchange.sendResponseHeaders(200,bytes.length);
                        OutputStream out = exchange.getResponseBody();
                        out.write(bytes);
                        out.close();
                } catch (Exception e) {
                        System.out.println("Exception Thrown: " + e.toString());
                        e.printStackTrace();

                        // attempt to send 500 Server Error Response to client
                        try {
                                exchange.sendResponseHeaders(500,-1);
                        }
                        catch(IOException i){} // do nothing when this fails
                }
    }

    /**
     * Parses a URI query string into a map of key-value pairs. Empty values are
     * silently ignored.
     *
     * @param query the raw query string from a request URI, may be null
     * @return a map of non-empty parameter names to their values
     * @throws IllegalArgumentException if an argument cannot be split
     */
    private static Map<String,String> parseQuery(String query) {
                HashMap<String,String> map = new HashMap<>();
                if(query != null && query.contains("="))
                        Stream.of(query.split("&")).forEach(arg -> {
                                String[] pair = arg.split("=", 2);
                                if(pair.length != 2)
                                throw new IllegalArgumentException("Unable to split " +
                                        "arg: " + arg + " into a key value pair around a " +
                                        "single = delimiter.");
                                if(!pair[1].isEmpty())
                                map.put(pair[0],pair[1]);
                        });
                return map;
    }

    /**
     * Instantiates and wires together a Frontend, Backend, DijkstraGraph, and
     * HashtableMap, loading graph data from the given file.
     *
     * @param filename path to the .dot file containing the graph data
     * @return a fully initialized FrontendInterface
     * @throws IOException if the graph data file cannot be read
     */
    private static FrontendInterface createWorkingFrontend(String filename) throws IOException {
                GraphADT<String,Double> graph = new DijkstraGraph<>();
                BackendInterface backend = new Backend(graph);
                backend.loadGraphData(filename);
                FrontendInterface frontend = new Frontend(backend);
                return frontend;
    }

    /**
     * Generates the HTML response div for the query described by the given
     * parameters and returns an empty response div if no recognized query
     * parameters are present.
     *
     * @param keyValuePairs parameters parsed from the request URI query string
     * @param frontend the frontend used to generate the response HTML
     * @return an HTML string containing the response div
     */
    private static String generateResponseHTML(Map<String,String> keyValuePairs, FrontendInterface frontend) {
                String response = "<div id=\"response\">";
                if(keyValuePairs.containsKey("start") &&
                keyValuePairs.containsKey("end")) {
                        response += frontend.generateShortestPathResponseHTML(
                                keyValuePairs.get("start"),
                                keyValuePairs.get("end")) + "</div>";
                } else if(keyValuePairs.containsKey("from")) {
                        response += frontend.generateTenClosestLocationsResponseHTML(
                                keyValuePairs.get("from")) + "</div>";
                } else
                        // otherwise, leave response div blank
                        response += "</div>";

                return response;
    }

    /**
     * Generates the HTML prompt divs for each type of supported request.
     *
     * @param frontend the frontend used to generate the prompt HTML
     * @return an HTML string containing the first and second prompt divs
     */
    private static String generatePromptHTML(FrontendInterface frontend) {
                String firstPrompt = "<div id=\"firstPrompt\">" +
                        frontend.generateShortestPathPromptHTML() +
                        "</div>";
                String secondPrompt = "<div id=\"secondPrompt\">" +
                        frontend.generateTenClosestLocationsPromptHTML() +
                        "</div>";
                return firstPrompt + secondPrompt;
    }

    /**
     * Reads the HTML template file and substitutes placeholders
     *
     * @param response HTML string for the response div
     * @param prompts HTML string for the prompt divs
     * @return the complete HTML page as a string
     * @throws IOException if the template file cannot be read
     */
    private static String composeHTML(String response, String prompts) throws IOException {
                String html = "";
                Scanner in = new Scanner(new File("template.html"));
                while(in.hasNextLine()) html += in.nextLine() + "\n";
                html = html.replaceFirst("<!-- RESPONSE GOES HERE -->",response);
                html = html.replaceFirst("<!-- PROMPTS GO HERE -->",prompts);

                return html;
    }

    /**
     * Processes a URL-encoded query string and prints the resulting HTML to
     * stdout. Used by index.cgi on CSL department machines where a public web
     * server cannot run.
     *
     * @param query a URL-encoded query string representing a single user request
     */
    public static void handleSingleResponse(String query) {
                try {
                        query = URLDecoder.decode(query, StandardCharsets.UTF_8);
                        Map<String,String> keyValuePairs = parseQuery(query);
                        FrontendInterface frontend = createWorkingFrontend("./europeanRail.dot");
                        String response = generateResponseHTML(keyValuePairs,frontend);
                        String prompts = generatePromptHTML(frontend);
                        String html = composeHTML(response,prompts);
                        System.out.println(html);
                } catch (Exception e) {
                        System.out.println("Exception Thrown: " + e.toString());
                        e.printStackTrace();
                }
    }
}
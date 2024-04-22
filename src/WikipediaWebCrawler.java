
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.*;

import java.util.HashMap;

public class WikipediaWebCrawler {


    //BFS; Linked list since we don't know nodes ahead of time.
    //for each of the links present in source file, make a thing that logs each of the pages that you get from
    //clicking on that link. Proceed exponentially, branching out as a tree until the target page is found. Once that
    //target is found, trace backwards to source and output length of path that is shortest. Node should go under first
    //link to discover it because it is guaranteed to be the shortest path.
    private static String extractTitle(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    public static List<String> bfs(String startUrl, String endUrl) {
        Queue<String> queue = new LinkedList<>();
        Map<String, String> visited = new HashMap<>();
        queue.add(startUrl);
        visited.put(startUrl, null);

        while (!queue.isEmpty()) {
            String currentUrl = queue.poll();
            if (currentUrl.equals(endUrl)) {
                return reconstructPath(currentUrl, visited);
            }

            try {
                Document doc = Jsoup.connect(currentUrl).get();
                Elements links = doc.select("a[href]");
                for (Element link : links) {
                    String absHref = link.attr("abs:href");
                    if (absHref.startsWith("https://en.wikipedia.org/wiki/") && !visited.containsKey(absHref)) {
                        visited.put(absHref, currentUrl);
                        queue.add(absHref);
                    }
                }
            } catch (IOException e) {
                System.err.println("Failed to retrieve content of: " + currentUrl);
            }
        }

        return Collections.emptyList();
    }

    private static List<String> reconstructPath(String endUrl, Map<String, String> visited) {
        LinkedList<String> path = new LinkedList<>();
        String step = endUrl;
        while (step != null) {
            path.addFirst(extractTitle(step));
            step = visited.get(step);
        }
        return path;
    }

    public static void main(String[] args) {
        System.out.println("test");
        String startUrl = "https://en.wikipedia.org/wiki/Java_(programming_language)";
        String endUrl = "https://en.wikipedia.org/wiki/Artificial_intelligence";

        List<String> path = bfs(startUrl, endUrl);
        if (path.isEmpty()) {
            System.out.println("No path found!");
        } else {
            System.out.println("Shortest path: " + String.join(" -> ", path));
        }
    }




//pages = nodes; links = edges

    //six degrees of wikipedia website as framework inspo

    //main function that takes in user input of targer and source via scanner

}

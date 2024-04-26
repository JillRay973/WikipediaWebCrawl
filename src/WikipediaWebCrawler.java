import java.io.IOException;
import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WikipediaWebCrawler {
    private static final String BASE_URL = "https://en.wikipedia.org/wiki/";
    private static String endWikiUrl;
    private static Set<String> visited = new HashSet<>();

    public static void main(String[] args) {
        // Example: Scraping the Wikipedia article for "Java (programming language)"
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the start page topic");
        String startPage = scanner.nextLine();
        String startWikiUrl = BASE_URL + startPage;

        System.out.println("Enter the end page topic");
        String endPage = scanner.nextLine();
        endWikiUrl = BASE_URL + endPage;

        System.out.println("Running...");
        List<String> shortestPath = findShortestPath(startWikiUrl);
        if (shortestPath != null) {
            System.out.println("Shortest path: ");
            for (int i = 0; i < shortestPath.size() - 1; i++) {
                System.out.print(shortestPath.get(i) + " -> ");
            }
            System.out.print(shortestPath.get(shortestPath.size() - 1));
        } else {
            System.out.println("No path found.");
        }
    }

    private static List<String> findShortestPath(String startWikiUrl) {
        Queue<String> queue = new LinkedList<>();
        Map<String, String> parentMap = new HashMap<>();

        queue.add(startWikiUrl);
        visited.add(startWikiUrl);
        parentMap.put(startWikiUrl, null);

        while (!queue.isEmpty()) {
            String currentUrl = queue.poll();
            if (currentUrl.equals(endWikiUrl)) {
                return reconstructPath(parentMap);
            }

            List<String> childUrls;
            try {
                childUrls = scrapeWikiArticleLinks(currentUrl);
                if (childUrls != null) {
                    for (String childUrl : childUrls) {
                        if (!visited.contains(childUrl)) {
                            queue.add(childUrl);
                            visited.add(childUrl);
                            parentMap.put(childUrl, currentUrl);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error fetching childUrls: " + e.getMessage());
            }
        }

        return null;
    }

    private static List<String> scrapeWikiArticleLinks(String url) throws IOException {
        // Fetch the document from the URL
        Document document = Jsoup.connect(url).get();
        Element paragraphs = document.selectFirst("#mw-content-text > div.mw-content-ltr.mw-parser-output");
        // Select all link elements
        if (paragraphs == null) {
            return null;
        }
        Elements links = paragraphs.select("p > a[href]");

        // Create a list to store the URLs
        List<String> urls = new ArrayList<>();

        // Iterate over all links and add their 'href' attribute to the list
        for (Element link : links) {
            String href = link.attr("abs:href"); // Use 'abs:href' to get absolute URL
            if (href.startsWith("https://")) {   // Filter to include only valid HTTP URLs
                urls.add(href);
            }
        }

        return urls;
    }

    private static List<String> reconstructPath(Map<String, String> visited) {
        LinkedList<String> path = new LinkedList<>();
        String step = endWikiUrl;
        while (step != null) {
            path.addFirst(extractTitle(step));
            step = visited.get(step);
        }
        return path;
    }

    private static String extractTitle(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

}

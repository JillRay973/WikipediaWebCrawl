import java.io.IOException;
import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WikipediaWebCrawler {
    private static final String BASE_URL = "https://en.wikipedia.org/wiki/";

    public static List<String> scrapeWikiArticleLinks(String url) throws IOException {
        // Fetch the document from the URL
        Document document = Jsoup.connect(url).get();
        Element paragraphs = document.selectFirst("#mw-content-text > div.mw-content-ltr.mw-parser-output");
        // Select all link elements
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

    private Map<String, int> lowestDistance (List<String> urls) {
        Map<String, int> lowestUrl = new HashMap<>();
        int min = Integer.MAX_VALUE;
        for (String url : urls) {
            List<String> path = bfs(url, endUrl);
            if (path.size() < min) {
                min = path.size();
                lowestUrl.put(url, min);
            }
        }
        return lowestUrl;
    }


    public static void main(String[] args) {
        try {
            // Example: Scraping the Wikipedia article for "Java (programming language)"
            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter the start page topic");
            String startPage = scanner.nextLine();
            String wikiUrl = BASE_URL + startPage;
            List<String> links = scrapeWikiArticleLinks(wikiUrl);
            links.forEach(System.out::println); // Print each URL
        } catch (IOException e) {
            System.out.println("Error fetching article: " + e.getMessage());
        }
    }

}

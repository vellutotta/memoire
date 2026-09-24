package api;

import model.Book;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class OpenLibraryClient {

    private static final String SEARCH_URL = "https://openlibrary.org/search.json?q=";

    public List<Book> searchBooks(String query) {
        List<Book> books = new ArrayList<>();
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            URI uri = URI.create(SEARCH_URL + encodedQuery);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("User-Agent", "Progetto-SWE-Java/1.0")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject jsonResponse = new JSONObject(response.body());
            JSONArray docs = jsonResponse.getJSONArray("docs");

            int limit = Math.min(docs.length(), 10);
            for (int i = 0; i < limit; i++) {
                JSONObject doc = docs.getJSONObject(i);

                String id = doc.has("key") ? doc.getString("key").replace("/works/", "") : "OL-" + System.currentTimeMillis();
                String title = doc.has("title") ? doc.getString("title") : "Titolo Sconosciuto";

                String author = "Autore Sconosciuto";
                if (doc.has("author_name") && !doc.getJSONArray("author_name").isEmpty()) {
                    author = doc.getJSONArray("author_name").getString(0);
                }

                String coverUrl = null;
                if (doc.has("cover_i")) {
                    int coverId = doc.getInt("cover_i");
                    coverUrl = "https://covers.openlibrary.org/b/id/" + coverId + "-L.jpg";
                }

                books.add(new Book(id, title, author, coverUrl));
            }
        } catch (Exception e) {
            System.err.println("Errore di comunicazione con Open Library: " + e.getMessage());
        }
        return books;
    }
}
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
    // Endpoint base per la ricerca generale di Open Library
    private static final String SEARCH_URL = "https://openlibrary.org/search.json?q=";

    public List<Book> searchBooks(String query) {
        List<Book> books = new ArrayList<>();
        try {
            // 1. Prepariamo l'URL gestendo gli spazi (es. "Harry Potter" -> "Harry+Potter")
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            URI uri = URI.create(SEARCH_URL + encodedQuery);

            // 2. Creiamo la richiesta HTTP (User-Agent serve per non far bloccare la richiesta da Open Library)
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("User-Agent", "Progetto-SWE-Java/1.0")
                    .GET()
                    .build();

            // 3. Inviamo la richiesta e riceviamo la risposta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 4. Analizziamo il file JSON ricevuto
            JSONObject jsonResponse = new JSONObject(response.body());
            JSONArray docs = jsonResponse.getJSONArray("docs");

            // Prendiamo al massimo i primi 10 risultati per non appesantire la lista
            int limit = Math.min(docs.length(), 10);
            for (int i = 0; i < limit; i++) {
                JSONObject doc = docs.getJSONObject(i);

                // Estrazione sicura dei dati: i database open source a volte hanno campi mancanti
                // Open Library usa identificativi tipo "/works/OL12345W", togliamo la parte iniziale
                String id = doc.has("key") ? doc.getString("key").replace("/works/", "") : "OL-" + System.currentTimeMillis();
                String title = doc.has("title") ? doc.getString("title") : "Titolo Sconosciuto";

                // L'autore è un array, prendiamo il primo se esiste
                String author = "Autore Sconosciuto";
                if (doc.has("author_name") && !doc.getJSONArray("author_name").isEmpty()) {
                    author = doc.getJSONArray("author_name").getString(0);
                }

                // Costruiamo l'URL della copertina ad alta risoluzione (-L)
                String coverUrl = null;
                if (doc.has("cover_i")) {
                    int coverId = doc.getInt("cover_i");
                    coverUrl = "https://covers.openlibrary.org/b/id/" + coverId + "-L.jpg";
                }

                // Creiamo l'oggetto Book e lo aggiungiamo alla lista
                books.add(new Book(id, title, author, coverUrl));
            }
        } catch (Exception e) {
            System.err.println("Errore di comunicazione con Open Library: " + e.getMessage());
        }
        return books;
    }
}
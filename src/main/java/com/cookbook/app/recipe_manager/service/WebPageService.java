package com.cookbook.app.recipe_manager.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;


@Service
public class WebPageService {

    private final WebClient webClient;
    //private final String url = "https://www.baeldung.com/java-download-file";

    public WebPageService(WebClient universalWebClient) {
        this.webClient = universalWebClient;
    }

    public String getPage(String url) {
        return this.webClient.get()
                // URI.create stellt sicher, dass die absolute URL korrekt interpretiert wird
                .uri(URI.create(url))
                // Der User-Agent tarnt die Anfrage als normalen Browser-Aufruf
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0.0.0")
                .retrieve()
                // Wandelt den gesamten HTML-Quelltext der Seite in einen Java-String um
                .bodyToMono(String.class)
                // Blockiert synchron, bis die Seite vollständig geladen ist
                .block();
    }
    //    public void openWebPageInBrowser(String url) {
//            // Prüfen, ob das Betriebssystem das Öffnen von Browsern unterstützt
//        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
//            try {
//                Desktop.getDesktop().browse(new URI(url));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            System.out.println("Browser öffnen wird auf diesem System nicht unterstützt.");
//        }
//    }
//
//        }

//    import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import java.net.URI;
//
//    @GetMapping("/redirect-to-page")
//    public ResponseEntity<Void> redirectToPage() {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setLocation(URI.create("https://bee.com"));
//        // 302 Found leitet den Browser sofort an die neue Adresse weiter
//        return new ResponseEntity<>(headers, HttpStatus.FOUND);
//    }
}

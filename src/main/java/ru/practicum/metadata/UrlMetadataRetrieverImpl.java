package ru.practicum.metadata;

import lombok.Builder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;

@Component
public class UrlMetadataRetrieverImpl implements UrlMetadataRetriever {
    private final HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .connectTimeout(Duration.ofSeconds(120))
            .build();


    @lombok.Value
    @Builder(toBuilder = true)
    static class UrlMetadataImpl implements UrlMetadata {
        String normalUrl;
        String resolvedUrl;
        String mimeType;
        String title;
        boolean hasImage;
        boolean hasVideo;
        Instant dateResolved;
    }

    @Override
    public UrlMetadata retrieve(String urlString) {
        final URI uri;
        try {
            uri = new URI(urlString);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("The URL is malformed: " + urlString, e);
        }

        HttpResponse<Void> response = connect(uri, "HEAD", HttpResponse.BodyHandlers.discarding());

        String contentType = response.headers()
                .firstValue(HttpHeaders.CONTENT_TYPE)
                .orElse("*");

        MediaType mediaType = MediaType.parseMediaType(contentType);

        final UrlMetadataImpl result;

        if (mediaType.isCompatibleWith(MimeType.valueOf("text/*"))) {
            result = handleText(response.uri());
        } else if (mediaType.isCompatibleWith(MimeType.valueOf("image/*"))) {
            result = handleImage(response.uri());
        } else if (mediaType.isCompatibleWith(MimeType.valueOf("video/*"))) {
            result = handleVideo(response.uri());
        } else {
            throw new IllegalArgumentException("The content type [" + mediaType
                    + "] at the specified URL is not supported.");
        }

        return result.toBuilder()
                .normalUrl(urlString)
                .resolvedUrl(response.uri().toString())
                .mimeType(mediaType.getType())
                .dateResolved(Instant.now())
                .build();
    }

    private <T> HttpResponse<T> connect(URI url,
                                        String method,
                                        HttpResponse.BodyHandler<T> responseBodyHandler) {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .method(method, HttpRequest.BodyPublishers.noBody())
                .build();

        final HttpResponse<T> response;
        try {
            response = client.send(request, responseBodyHandler);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot retrieve data from the URL: " + url, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Cannot get the metadata for url: " + url
                    + " because the thread was interrupted.", e);
        }

        HttpStatus status = HttpStatus.resolve(response.statusCode());
        if(status == null) {
            throw new IllegalArgumentException("The server returned an unknown status code: " + response.statusCode());
        }

        if(status.equals(HttpStatus.UNAUTHORIZED) || status.equals(HttpStatus.FORBIDDEN)) {
            throw new IllegalArgumentException("There is no access to the resource at the specified URL: " + url);
        }
        if(status.isError()) {
            throw new IllegalArgumentException("Cannot get the data on the item because the server returned an error."
                    + "Response status: " + status);
        }

        return response;
    }

    private UrlMetadataImpl handleText(URI url) {
        HttpResponse<String> response = connect(url, "GET", HttpResponse.BodyHandlers.ofString());
        Document document = Jsoup.parse(response.body());

        Elements imgElements = document.getElementsByTag("img");
        Elements videoElements = document.getElementsByTag("video");

        return UrlMetadataImpl.builder()
                .title(document.title())
                .hasImage(!imgElements.isEmpty())
                .hasVideo(!videoElements.isEmpty())
                .build();
    }

    private UrlMetadataImpl handleVideo(URI url) {
        String name = new File(url).getName();
        return UrlMetadataImpl.builder()
                .title(name)
                .hasVideo(true)
                .build();
    }

    private UrlMetadataImpl handleImage(URI url) {
        String name = new File(url).getName();
        return UrlMetadataImpl.builder()
                .title(name)
                .hasImage(true)
                .build();
    }
}

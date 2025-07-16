package ru.practicum.metadata;

import java.time.Instant;

public interface UrlMetadataRetriever {

    UrlMetadata retrieve(String url);

    interface UrlMetadata {
        String getNormalUrl();
        String getResolvedUrl();
        String getMimeType();
        String getTitle();
        boolean isHasImage();
        boolean isHasVideo();
        Instant getDateResolved();
    }
}

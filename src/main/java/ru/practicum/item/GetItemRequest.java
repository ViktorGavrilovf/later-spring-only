package ru.practicum.item;

import lombok.Data;

import java.util.List;

@Data
public class GetItemRequest {
    private Long userId;
    private State state = State.UNREAD;
    private ContentType contentType = ContentType.ALL;
    private Sort sort = Sort.NEWEST;
    private int limit = 10;
    private List<String> tags;

    public enum State {
        ALL, UNREAD, READ
    }

    public enum ContentType {
        ALL, ARTICLE, IMAGE, VIDEO
    }

    public enum Sort {
        NEWEST, OLDEST, TITLE
    }

    public boolean hasTags() {
        return tags != null && !tags.isEmpty();
    }
}



package ru.practicum.itemnote;


import ru.practicum.item.Item;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ItemNoteMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd, HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    public static ItemNoteDto toDto(ItemNote note) {
        return new ItemNoteDto(
                note.getId(),
                note.getItem().getItemId(),
                note.getNoteText(),
                FORMATTER.format(note.getCreatedAt()),
                note.getItem().getUrl()
        );
    }

    public static ItemNote fromDto(ItemNoteDto dto, Item item) {
        ItemNote note = new ItemNote();
        note.setItem(item);
        note.setId(dto.getId());
        note.setCreatedAt(Instant.now());
        return note;
    }
}


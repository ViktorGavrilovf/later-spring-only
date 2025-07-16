package ru.practicum.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateItemRequest {
    private Boolean unread;
    private List<String> tags;
    private Boolean replaceTags = false;
}

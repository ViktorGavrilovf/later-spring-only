package ru.practicum.item;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItems(Long userId);

    List<ItemDto> getItems(GetItemRequest request);
    ItemDto addNewItem(long userId, Item item);
    void deleteItem(long userId, long itemId);
}

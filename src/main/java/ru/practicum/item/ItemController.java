package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto add(@RequestHeader("X-Later-User-Id") Long userId,
                    @RequestBody Item item) {
        return itemService.addNewItem(userId, item);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Later-User-Id") long userId,
                           @PathVariable(name="itemId") long itemId) {
        itemService.deleteItem(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> get(
            @RequestHeader("X-Later-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "unread") String state,
            @RequestParam(name = "contentType", defaultValue = "all") String contentType,
            @RequestParam(name = "sort", defaultValue = "newest") String sort,
            @RequestParam(name = "limit", defaultValue = "10") int limit,
            @RequestParam(name = "tags", required = false) List<String> tags
    ) {
        GetItemRequest request = new GetItemRequest();
        request.setUserId(userId);
        request.setState(GetItemRequest.State.valueOf(state.toUpperCase()));
        request.setContentType(GetItemRequest.ContentType.valueOf(contentType.toUpperCase()));
        request.setSort(GetItemRequest.Sort.valueOf(sort.toUpperCase()));
        request.setLimit(limit);
        request.setTags(tags);

        return itemService.getItems(request);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Later-User-Id") long userId,
                              @PathVariable long itemId,
                              @RequestBody UpdateItemRequest request) {
        return itemService.updateItem(userId, itemId, request);
    }
}
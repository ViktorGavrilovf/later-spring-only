package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.metadata.UrlMetadataRetriever;
import ru.practicum.metadata.UrlMetadataRetrieverImpl;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UrlMetadataRetrieverImpl metadataRetriever;
    private final UserRepository userRepository;

    @Override
    public List<ItemDto> getItems(Long userId) {
        return ItemMapper.mapToItemDto(itemRepository.findByUserId(userId));
    }

    @Override
    public ItemDto addNewItem(long userId, Item item) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        item.setUserId(user.getId());
        item.setUser(user);

        UrlMetadataRetriever.UrlMetadata metadata = metadataRetriever.retrieve(item.getUrl());
        item.setResolvedUrl(metadata.getResolvedUrl());
        item.setMimeType(metadata.getMimeType());
        item.setTitle(metadata.getTitle());
        item.setHasVideo(metadata.isHasVideo());
        item.setHasImage(metadata.isHasImage());
        item.setDateResolved(metadata.getDateResolved());

        return ItemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        itemRepository.deleteByUserIdAndItemId(userId, itemId);
    }
}

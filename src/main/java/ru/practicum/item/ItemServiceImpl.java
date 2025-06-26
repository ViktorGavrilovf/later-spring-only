package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<ItemDto> getItems(Long userId) {
        return ItemMapper.mapToItemDto(itemRepository.findByUserId(userId));
    }

    @Override
    public ItemDto addNewItem(long userId, Item item) {
        item.setUserId(userId);
        return ItemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        itemRepository.deleteByUserIdAndItemId(userId, itemId);
    }
}

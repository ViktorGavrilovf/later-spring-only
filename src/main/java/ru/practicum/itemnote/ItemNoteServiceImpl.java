package ru.practicum.itemnote;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.item.Item;
import ru.practicum.item.ItemRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemNoteServiceImpl implements ItemNoteService {

    private final ItemNoteRepository itemNoteRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemNoteDto addNewItemNote(long userId, ItemNoteDto itemNoteDto) {
        Item item = itemRepository.findById(itemNoteDto.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        ItemNote note = ItemNoteMapper.fromDto(itemNoteDto, item);
        return ItemNoteMapper.toDto(itemNoteRepository.save(note));
    }

    @Override
    public List<ItemNoteDto> searchNotesByUrl(String url, Long userId) {
        return itemNoteRepository.findByItemUserIdAndItemUrlContainingIgnoreCase(userId, url)
                .stream()
                .map(ItemNoteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemNoteDto> searchNotesByTag(long userId, String tag) {
        return itemNoteRepository.findByUserIdAndTag(userId, tag)
                .stream()
                .map(ItemNoteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemNoteDto> listAllItemsWithNotes(long userId, int from, int size) {
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size);
        return itemNoteRepository.findAllByItemUserId(userId, page)
                .stream()
                .map(ItemNoteMapper::toDto)
                .collect(Collectors.toList());
    }
}

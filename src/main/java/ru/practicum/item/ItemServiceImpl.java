package ru.practicum.item;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.metadata.UrlMetadataRetriever;
import ru.practicum.metadata.UrlMetadataRetrieverImpl;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.util.ArrayList;
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

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getItems(GetItemRequest request) {
        QItem item = QItem.item;
        List<BooleanExpression> conditions = new ArrayList<>();

        conditions.add(item.user.id.eq(request.getUserId()));

        GetItemRequest.State state = request.getState();
        if (!state.equals(GetItemRequest.State.ALL)) {
            conditions.add(makeStateCondition(state));
        }

        GetItemRequest.ContentType contentType = request.getContentType();
        if (!contentType.equals(GetItemRequest.ContentType.ALL)) {
            conditions.add(makeContentTypeCondition(contentType));
        }

        if (request.hasTags()) {
            conditions.add(item.tags.any().in(request.getTags()));
        }

        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();

        Sort sort = makeOrderByClause(request.getSort());
        PageRequest pageRequest = PageRequest.of(0, request.getLimit(), sort);

        Iterable<Item> items = itemRepository.findAll(finalCondition, pageRequest);
        return ItemMapper.mapToItemDto(items);
    }

    private BooleanExpression makeStateCondition(GetItemRequest.State state) {
        if (state.equals(GetItemRequest.State.READ)) {
            return QItem.item.unread.isFalse();
        } else {
            return QItem.item.unread.isTrue();
        }
    }

    private BooleanExpression makeContentTypeCondition(GetItemRequest.ContentType contentType) {
        if (contentType.equals(GetItemRequest.ContentType.ARTICLE)) {
            return QItem.item.mimeType.eq("text");
        } else if (contentType.equals(GetItemRequest.ContentType.IMAGE)) {
            return QItem.item.mimeType.eq("image");
        } else {
            return QItem.item.mimeType.eq("video");
        }
    }

    private Sort makeOrderByClause(GetItemRequest.Sort sort) {
        switch (sort) {
            case TITLE -> {
                return Sort.by("title").ascending();
            }
            case OLDEST -> {
                return Sort.by("dateResolved").ascending();
            }
            default -> {
                return Sort.by("dateResolved").descending();
            }
        }
    }
}

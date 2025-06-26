package ru.practicum.itemnote;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemNoteRepository extends JpaRepository<ItemNote, Long> {

    List<ItemNote> findByItemUserIdAndItemUrlContainingIgnoreCase(Long userId, String urlPart);

    @Query("SELECT n FROM ItemNote n JOIN n.item i WHERE i.user.id = :userId AND :tag member OF i.tags")
    List<ItemNote> findByUserIdAndTag(Long userId, String tag);

    Page<ItemNote> findAllByItemUserId(Long userId, Pageable pageable);
}

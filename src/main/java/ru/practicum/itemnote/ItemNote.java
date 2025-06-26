package ru.practicum.itemnote;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.item.Item;

import java.time.Instant;

@Entity
@Table(name = "item_notes")
@Getter @Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor

public class ItemNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "note_text", length = 2000, nullable = false)
    private String noteText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}

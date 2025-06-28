package ru.practicum.shareit.item.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Репозиторий для работы с комментариями к предметам.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
    /**
     * Поиск всех комментариев по идентификатору предмета.
     *
     * @param itemId идентификатор предмета
     * @return список комментариев для указанного предмета
     */
    List<Comment> findByItemId(Long itemId);
}
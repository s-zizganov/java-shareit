package ru.practicum.shareit.item.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Добавлен метод для поиска комментариев по itemId
    List<Comment> findByItemId(Long itemId);
}
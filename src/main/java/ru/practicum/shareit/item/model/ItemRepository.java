package ru.practicum.shareit.item.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    // Изменение: Добавлен метод для поиска вещей по ownerId
    List<Item> findByOwnerId(Long ownerId);
}
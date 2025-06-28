package ru.practicum.shareit.item.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Репозиторий для управления предметами аренды.
 */
public interface ItemRepository extends JpaRepository<Item, Long> {
    /**
     * Поиск всех предметов по идентификатору владельца.
     *
     * @param ownerId идентификатор владельца
     * @return список предметов принадлежащих указанному владельцу
     */
    List<Item> findByOwnerId(Long ownerId);
}
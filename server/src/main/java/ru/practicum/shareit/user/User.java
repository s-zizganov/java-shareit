package ru.practicum.shareit.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * Класс, представляющий пользователя системы.
 * Используется для хранения информации о пользователях в базе данных.
 */
@Data
@Entity
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class User {
    /**
     * Уникальный идентификатор пользователя.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    /**
     * Имя пользователя. Не может быть null.
     */
    @Column(nullable = false)
    String name;
    /**
     * Электронная почта пользователя. Должна быть уникальной и не может быть null.
     */
    @Column(nullable = false, unique = true)
    String email;

    public User() {
    }
}
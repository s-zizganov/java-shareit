package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для работы с сущностью {@link User}.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Проверяет существование пользователя с указанным email, исключая пользователя с заданным id.
     *
     * @param email email для проверки.
     * @param id    id пользователя, которого нужно исключить из проверки.
     * @return true, если пользователь с таким email существует (и это не пользователь с указанным id), иначе false.
     */
    boolean existsByEmailAndIdNot(String email, Long id);

    /**
     * Проверяет существование пользователя с указанным email.
     *
     * @param email email для проверки.
     * @return true, если пользователь с таким email существует, иначе false.
     */
    boolean existsByEmail(String email);
}
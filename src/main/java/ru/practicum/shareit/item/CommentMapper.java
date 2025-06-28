package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

/**
 * Маппер для преобразования между сущностью Comment и DTO.
 */
public class CommentMapper {
    /**
     * Преобразование сущности комментария в DTO с именем автора.
     *
     * @param comment сущность комментария
     * @param authorName имя автора комментария
     * @return DTO комментария
     */
    public static CommentDto toCommentDto(Comment comment, String authorName) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setItemId(comment.getItemId());
        dto.setAuthorId(comment.getAuthorId());
        dto.setAuthorName(authorName);
        dto.setCreated(comment.getCreated());
        return dto;
    }

    /**
     * Преобразование DTO комментария в сущность.
     *
     * @param commentDto DTO комментария
     * @return сущность комментария
     */
    public static Comment toComment(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setText(commentDto.getText());
        comment.setItemId(commentDto.getItemId());
        comment.setAuthorId(commentDto.getAuthorId());
        comment.setCreated(commentDto.getCreated());
        return comment;
    }
}
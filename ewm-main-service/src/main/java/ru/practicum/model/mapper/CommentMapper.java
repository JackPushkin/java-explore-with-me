package ru.practicum.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.model.Comment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    Comment toCommentFromNewDto(NewCommentDto commentDto);

    @Mapping(target = "userId", source = "creator.id")
    @Mapping(target = "eventId", source = "event.id")
    CommentDto toCommentDto(Comment comment);

    @Mapping(target = "userId", source = "creator.id")
    @Mapping(target = "eventId", source = "event.id")
    List<CommentDto> toCommentDtoList(List<Comment> comment);
}

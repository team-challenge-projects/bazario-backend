package org.cyberrealm.tech.bazario.backend.mapper;

import org.cyberrealm.tech.bazario.backend.config.MapperConfig;
import org.cyberrealm.tech.bazario.backend.dto.CommentDto;
import org.cyberrealm.tech.bazario.backend.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CommentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "evaluator", ignore = true)
    @Mapping(target = "evaluated", ignore = true)
    @Mapping(target = "reviewText", source = "description")
    Review toReview(CommentDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "evaluator", ignore = true)
    @Mapping(target = "evaluated", ignore = true)
    @Mapping(target = "reviewText", source = "description")
    void updateReviewFromDto(CommentDto dto, @MappingTarget Review review);

    @Mapping(target = "description", source = "reviewText")
    CommentDto toDto(Review review);
}

package com.julia.taskmanagementapp.mapper;

import com.julia.taskmanagementapp.config.MapperConfig;
import com.julia.taskmanagementapp.dto.label.CreateLabelRequestDto;
import com.julia.taskmanagementapp.dto.label.LabelDto;
import com.julia.taskmanagementapp.dto.label.UpdateLabelRequestDto;
import com.julia.taskmanagementapp.model.Label;
import com.julia.taskmanagementapp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface LabelMapper {
    Label toModel(CreateLabelRequestDto requestDto);

    @Mapping(source = "creator", target = "creatorId", qualifiedByName = "getCreatorId")
    LabelDto toDto(Label label);

    void update(@MappingTarget Label label, UpdateLabelRequestDto requestDto);

    @Mapping(source = "name", target = "name", qualifiedByName = "trim")
    @Mapping(source = "color", target = "color", qualifiedByName = "trim")
    CreateLabelRequestDto trimCreateRequest(CreateLabelRequestDto requestDto);

    @Mapping(source = "name", target = "name", qualifiedByName = "trim")
    @Mapping(source = "color", target = "color", qualifiedByName = "trim")
    UpdateLabelRequestDto trimUpdateRequest(UpdateLabelRequestDto requestDto);

    @Named("trim")
    default String trim(String string) {
        if (string != null && !string.isBlank()) {
            return string.trim();
        }
        return null;
    }

    @Named("getCreatorId")
    default Long getCreatorId(User creator) {
        return creator.getId();
    }
}

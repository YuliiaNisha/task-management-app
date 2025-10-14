package com.julia.taskmanagementapp.mapper;

import com.julia.taskmanagementapp.config.MapperConfig;
import com.julia.taskmanagementapp.dto.label.CreateLabelRequestDto;
import com.julia.taskmanagementapp.dto.label.LabelDto;
import com.julia.taskmanagementapp.model.Label;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface LabelMapper {
    Label toModel(CreateLabelRequestDto requestDto);

    LabelDto toDto(Label label);
}

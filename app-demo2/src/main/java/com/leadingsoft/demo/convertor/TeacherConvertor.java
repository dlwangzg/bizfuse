package com.leadingsoft.demo.convertor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.leadingsoft.bizfuse.common.web.dto.AbstractConvertor;
import com.leadingsoft.demo.dto.TeacherDTO;
import com.leadingsoft.demo.model.Teacher;
import com.leadingsoft.demo.service.TeacherService;

import lombok.NonNull;

/**
 * TeacherConvertor
 */
@Component
public class TeacherConvertor extends AbstractConvertor<Teacher, TeacherDTO> {

    @Autowired
    private TeacherService teacherService;

    @Override
    public Teacher toModel(@NonNull final TeacherDTO dto) {
        if (dto.isNew()) {//新增
            return this.constructModel(dto);
        } else {//更新
            return this.updateModel(dto);
        }
    }

    @Override
    public TeacherDTO toDTO(@NonNull final Teacher model, final boolean forListView) {
        final TeacherDTO dto = new TeacherDTO();
        dto.setId(model.getId());
        dto.setName(model.getName());

        return dto;
    }

    // 构建新Model
    private Teacher constructModel(final TeacherDTO dto) {
        final Teacher model = new Teacher();
        model.setName(dto.getName());

        return model;
    }

    // 更新Model
    private Teacher updateModel(final TeacherDTO dto) {
        final Teacher model = this.teacherService.get(dto.getId());
        model.setName(dto.getName());

        return model;
    }
}

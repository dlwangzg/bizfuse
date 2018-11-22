package com.leadingsoft.demo.convertor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.leadingsoft.bizfuse.common.web.dto.AbstractConvertor;
import com.leadingsoft.demo.dto.StudentDTO;
import com.leadingsoft.demo.model.Student;
import com.leadingsoft.demo.service.StudentService;

import lombok.NonNull;

/**
 * StudentConvertor
 */
@Component
public class StudentConvertor extends AbstractConvertor<Student, StudentDTO> {

    @Autowired
    private StudentService studentService;

    @Override
    public Student toModel(@NonNull final StudentDTO dto) {
        if (dto.isNew()) {//新增
            return this.constructModel(dto);
        } else {//更新
            return this.updateModel(dto);
        }
    }

    @Override
    public StudentDTO toDTO(@NonNull final Student model, final boolean forListView) {
        final StudentDTO dto = new StudentDTO();
        dto.setId(model.getId());
        dto.setName(model.getName());

        return dto;
    }

    // 构建新Model
    private Student constructModel(final StudentDTO dto) {
        final Student model = new Student();
        model.setName(dto.getName());

        return model;
    }

    // 更新Model
    private Student updateModel(final StudentDTO dto) {
        final Student model = this.studentService.get(dto.getId());
        model.setName(dto.getName());

        return model;
    }
}

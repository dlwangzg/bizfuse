package com.leadingsoft.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leadingsoft.bizfuse.common.web.exception.CustomRuntimeException;
import com.leadingsoft.demo.model.Teacher;
import com.leadingsoft.demo.repository.TeacherRepository;
import com.leadingsoft.demo.service.TeacherService;

import lombok.NonNull;

/**
 * TeacherService 实现类
 */
@Service
@Transactional
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Override
    @Transactional(readOnly = true)
    public Teacher get(@NonNull final Long id) {
        final Teacher model = this.teacherRepository.findOne(id);
        if (model == null) {
            throw new CustomRuntimeException("404", String.format("查找的资源[%s]不存在.", id));
        }
        return model;
    }

    @Override
    public Teacher create(final Teacher model) {
        // TODO: 业务逻辑
        return this.teacherRepository.save(model);
    }

    @Override
    public Teacher update(final Teacher model) {
        // TODO: 业务逻辑
        return this.teacherRepository.save(model);
    }

    @Override
    public void delete(@NonNull final Long id) {
        // TODO: 业务逻辑
        this.teacherRepository.delete(id);
    }
}

package com.leadingsoft.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leadingsoft.bizfuse.common.web.exception.CustomRuntimeException;
import com.leadingsoft.demo.model.Student;
import com.leadingsoft.demo.repository.StudentRepository;
import com.leadingsoft.demo.service.StudentService;

import lombok.NonNull;

/**
 * StudentService 实现类
 */
@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    @Transactional(readOnly = true)
    public Student get(@NonNull final Long id) {
        final Student model = this.studentRepository.findOne(id);
        if (model == null) {
            throw new CustomRuntimeException("404", String.format("查找的资源[%s]不存在.", id));
        }
        return model;
    }

    @Override
    public Student create(final Student model) {
        // TODO: 业务逻辑
        return this.studentRepository.save(model);
    }

    @Override
    public Student update(final Student model) {
        // TODO: 业务逻辑
        return this.studentRepository.save(model);
    }

    @Override
    public void delete(@NonNull final Long id) {
        // TODO: 业务逻辑
        this.studentRepository.delete(id);
    }
}

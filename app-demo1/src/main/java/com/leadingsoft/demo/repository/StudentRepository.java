package com.leadingsoft.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import com.leadingsoft.demo.model.Student;

/**
 * StudentRepository
 */
public interface StudentRepository extends Repository<Student, Long> {

    Page<Student> findAll(Pageable pageable);

    Student findOne(Long id);

    Student save(Student model);

    void delete(Long id);

}

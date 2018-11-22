package com.leadingsoft.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.validator.constraints.NotBlank;

import com.leadingsoft.bizfuse.common.jpa.model.AbstractAuditModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Student extends AbstractAuditModel {

    private static final long serialVersionUID = 1L;

    @NotBlank
    @Column(nullable = false)
    private String name;
}

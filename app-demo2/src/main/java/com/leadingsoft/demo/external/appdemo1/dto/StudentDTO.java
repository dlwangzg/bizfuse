package com.leadingsoft.demo.external.appdemo1.dto;

import org.hibernate.validator.constraints.NotBlank;

import com.leadingsoft.bizfuse.common.web.dto.AbstractDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentDTO extends AbstractDTO {

    private static final long serialVersionUID = 1L;

    @NotBlank
    private String name;
}

package com.leadingsoft.demo.external.appdemo1;

import javax.validation.Valid;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.leadingsoft.bizfuse.common.web.dto.result.ResultDTO;
import com.leadingsoft.demo.external.appdemo1.dto.StudentDTO;

@FeignClient(serviceId = "app-demo1")
@RequestMapping("/w/students")
public interface FeignStudentRestAPI {

    /**
     * 取得详细数据
     *
     * @param id 资源ID
     * @return 资源详细
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<StudentDTO> get(@PathVariable("id") final Long id);

    /**
     * 新建操作
     *
     * @param studentDTO 新建资源的DTO
     * @return 新建资源
     */
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<StudentDTO> create(@RequestBody @Valid final StudentDTO studentDTO);
}

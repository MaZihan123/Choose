package com.xuecheng.content.model.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EditCourseDto extends AddCourseDto{

    @ApiModelProperty(value="课程id",required=true)//Swagger文档
    private Long id;
}

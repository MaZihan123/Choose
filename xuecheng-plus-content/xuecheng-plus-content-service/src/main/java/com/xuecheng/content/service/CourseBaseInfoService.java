package com.xuecheng.content.service;


import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;

//课程信息管理接口
public interface CourseBaseInfoService {
    //新增课程
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);

    /*
    分页查询
     */
    //课程分页查询
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

    public CourseBaseInfoDto getCourseBaseInfo(Long courseId);

    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto);

}

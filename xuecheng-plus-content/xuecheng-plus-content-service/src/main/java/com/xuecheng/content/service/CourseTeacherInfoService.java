package com.xuecheng.content.service;

import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

public interface CourseTeacherInfoService {
    List<CourseTeacher> getCourseTeacherInfo(Long courseId);


    void addTeacher(CourseTeacher courseTeacher);

    void updateTeacher(CourseTeacher courseTeacher);

    void deleteTeacher(Long courseId, Long id);
}

package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CourseTeacherInfoServiceImpl implements CourseTeacherInfoService {
    @Autowired
    CourseTeacherMapper courseTeacherMapper;

    @Override
    public List<CourseTeacher> getCourseTeacherInfo(Long courseId) {
        //TODO 通过id来查询teacher的全部信息，然后返回teacher
        // sql:SELECT teacher_name FROM course_teacher WHERE course_id=??;
        LambdaQueryWrapper<CourseTeacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseTeacher::getCourseId, courseId);
        List<CourseTeacher> courseTeachers = courseTeacherMapper.selectList(wrapper);

        if(courseTeachers==null)
        {
            XueChengPlusException.cast("教师信息不存在！！！");
        }
        return courseTeachers;
    }

    @Override
    public void addTeacher(CourseTeacher courseTeacher) {
        courseTeacherMapper.insert(courseTeacher);
    }

    @Override
    public void updateTeacher(CourseTeacher courseTeacher) {

        Long id = courseTeacher.getId();
        CourseTeacher courseTeacherTarget = courseTeacherMapper.selectById(id);

        if(courseTeacherTarget==null)
        {
            XueChengPlusException.cast("异常查询");
        }

        BeanUtils.copyProperties(courseTeacher, courseTeacherTarget);
        courseTeacherMapper.updateById(courseTeacherTarget);

    }

    @Override
    public void deleteTeacher(Long courseId, Long id)
    {
        if (courseId == null || id == null) {
            XueChengPlusException.cast("参数不能为空");
        }
        LambdaQueryWrapper<CourseTeacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseTeacher::getCourseId, courseId);
        wrapper.eq(CourseTeacher::getId, id);
        int delete = courseTeacherMapper.delete(wrapper);
        if(delete==0)
        {
            XueChengPlusException.cast("删除失败，未找到对应教师信息！");
        }
    }
}

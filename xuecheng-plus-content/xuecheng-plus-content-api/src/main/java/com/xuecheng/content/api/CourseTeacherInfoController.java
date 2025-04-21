package com.xuecheng.content.api;

import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherInfoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courseTeacher")
public class CourseTeacherInfoController {

    @Autowired
    CourseTeacherInfoService courseTeacherInfoService;

    @ApiOperation("查询教师")
    @GetMapping("/list/{courseId}")
    public List<CourseTeacher> getCourseTeacherInfo(@PathVariable Long courseId)
    {
        return courseTeacherInfoService.getCourseTeacherInfo(courseId);
    }

    @ApiOperation("添加教师请求")
    @PostMapping()
    public void addTeacher(@RequestBody CourseTeacher courseTeacher)
    {
        courseTeacherInfoService.addTeacher(courseTeacher);
    }

    @ApiOperation("修改教师请求")
    @PutMapping()
    public void updateTeacher(@RequestBody CourseTeacher courseTeacher)
    {
        courseTeacherInfoService.updateTeacher(courseTeacher);
    }
    @ApiOperation("删除教师请求")
    @DeleteMapping("/course/{courseId}/{id}")
    public void deleteTeacher(@PathVariable Long courseId,@PathVariable Long id)
    {
        courseTeacherInfoService.deleteTeacher(courseId,id);
    }


    @GetMapping("/test")
    public String test() {
        return "ok";
    }

}

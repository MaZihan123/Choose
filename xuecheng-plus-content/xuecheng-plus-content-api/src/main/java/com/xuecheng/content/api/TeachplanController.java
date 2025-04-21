package com.xuecheng.content.api;


import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//课程计划管理接口
@Api(value = "课程计划接口",tags="课程计划编辑接口")
@RestController("/content")
public class TeachplanController {

    @Autowired
    TeachplanService teachplanService;

    //查询课程计划-----GET /teachplan/22/tree-nodes
    @GetMapping("teachplan/{courseId}/tree-nodes")
    @ApiOperation("查询课程计划树形图")
    @ApiImplicitParam(value="courseId",name="课程Id",required=true,dataType = "Long",paramType = "path")
    public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId)
    {
        return teachplanService.findTeachplanTree(courseId);
    }

    @ApiOperation("课程计划创建或修改")
    @PostMapping("/teachplan")
    public void saveTeachplan(@RequestBody TeachplanDto teachplanDto)
    {
        //保存的时候有id，新增的是时候没有id
        teachplanService.saveTeachplan(teachplanDto);
    }

    @ApiOperation("删除课程计划")
    @DeleteMapping("/teachplan/{id}")
    ///content/teachplan/43"
    public void deleteTeachplan(@PathVariable Long id)
    {
        teachplanService.deleteTeachplan(id);
    }

    @ApiOperation("课程计划排序")
    @PostMapping("/teachplan/{method}/{id}")
    ///content/teachplan/43"
    public void sortTeachplan(@PathVariable String method, @PathVariable Long id)
    {
        teachplanService.sortTeachplan(method,id);
    }

}

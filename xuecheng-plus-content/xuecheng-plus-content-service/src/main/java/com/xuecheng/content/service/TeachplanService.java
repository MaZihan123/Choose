package com.xuecheng.content.service;


import com.xuecheng.content.model.dto.TeachplanDto;

import java.util.List;

public interface TeachplanService {
    /**
     * 根据课程id查询课程计划
     * @param courseId 课程id
     * @return
     */
    public List<TeachplanDto> findTeachplanTree(Long courseId);
    public void saveTeachplan(TeachplanDto teachplanDto);

    void deleteTeachplan(Long courseid);

    void sortTeachplan(String method, Long id);
}

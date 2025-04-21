package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeachplanServiceImpl implements TeachplanService {
    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;
    final private String MOVE_DOWN="movedown";

    @Override
    public List<TeachplanDto> findTeachplanTree(Long courseId) {

        List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(courseId);


        return teachplanDtos;
    }

    private int getTeachplanCount(Long courseId, Long parentId)//寻找有几个子节点
    {
        //寻找有几个子节点
        LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teachplan::getParentid, parentId);
        wrapper.eq(Teachplan::getCourseId, courseId);
        Integer count = teachplanMapper.selectCount(wrapper);
        return count + 1;
    }

    @Override
    public void saveTeachplan(TeachplanDto teachplanDto) {
        //通过课程计划的id判断时新增还是修改
        Long Id = teachplanDto.getId();
        if (Id == null) {//为空是新增
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(teachplanDto, teachplan);

            //orderby
            //确定排序字段，然后找到他同级个数，给同级个数加1
            //select count(1) form teachplan where course_id=117 and parentid=0;
            Long parentId = teachplan.getParentid();
            Long courseId = teachplan.getCourseId();

            teachplan.setOrderby(getTeachplanCount(courseId, parentId));
            teachplanMapper.insert(teachplan);

        } else {
            //修改
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(teachplanDto, teachplan);//现在teachplan是前端dto的数据
            teachplanMapper.updateById(teachplan);

        }
    }

    @Override
    public void deleteTeachplan(Long id) {
        //判断是大章节还是小章节
        //判断方法，看一下他的parentId
        LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teachplan::getParentid, id);
        //查询所有父节点为id的个数
        Integer count = teachplanMapper.selectCount(wrapper);

        Teachplan teachplan = teachplanMapper.selectById(id);
        if (teachplan == null) {
            XueChengPlusException.cast("课程计划不存在！");
        }


        if (teachplan.getParentid() == 0) //此时是大章节
        {
            //删除第一级别大章节时：
            //TODO 要求大章节下边没有小章节时 才能被删除
            //查找所有课程计划中，有没有parentid==上面的parentid个数

            if (count == 0) {
                //没有子章节，可以 直接 删除
                teachplanMapper.deleteById(id);
            }
            else{
                XueChengPlusException.cast("请先删除这些小章节，再进行大章节删除操作");
            }

        } else {
            //删除第二级别小章节时：
            //TODO 同时需要将teachplan_media表关联的信息也删除
            teachplanMapper.deleteById(id);
            LambdaQueryWrapper<TeachplanMedia> wrapper1 = new LambdaQueryWrapper<>();
            wrapper1.eq(TeachplanMedia::getTeachplanId, id);
            teachplanMediaMapper.delete(wrapper1);
        }


    }

    @Override
    public void sortTeachplan(String method, Long id) {
        Teachplan teachplanSource = teachplanMapper.selectById(id);
        if(teachplanSource==null){
            XueChengPlusException.cast("课程计划不存在，无法排序");
        }
        Integer orderSource = teachplanSource.getOrderby();
        if(method.equals(MOVE_DOWN))
        {//下移操作
            //TODO 查询数据库的orderby 然后和后面的交换一下
            //1.获取当前自己的orderby

            //2.查看自己后面的orderby存不存在
            //通过自己的同级parent id 中 找 orderSource+1 的
            //sql：SELECT * FROM teachplan WHERE parentid= AND orderby=ORDERBY+1 ;
            LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Teachplan::getParentid, teachplanSource.getParentid());
            wrapper.eq(Teachplan::getOrderby, orderSource+1);
            Teachplan teachplanBehind = teachplanMapper.selectOne(wrapper);
            if(teachplanBehind==null)
            {
                XueChengPlusException.cast("向下移动失败，这已经是最后一个了");
            }
            else
            {
                teachplanBehind.setOrderby(orderSource);
                teachplanSource.setOrderby(orderSource+1);
                teachplanMapper.updateById(teachplanSource);
                teachplanMapper.updateById(teachplanBehind);
            }
        }
        else
        {//上升操作
            //TODO 查询数据库的orderby 然后和后面的交换一下
            //1.获取当前自己的orderby

            if(orderSource==1)
            {//如果我的orderby==1，肯定不用管了
                XueChengPlusException.cast("向上移动失败，这已经是第一个了");
                return;
            }
            //2.查看自己 前 面的orderby存不存在
            //通过自己的同级parent id 中 找 orderSource-1 的
            //sql：SELECT * FROM teachplan WHERE parentid= AND orderby=ORDERBY-1 ;
            LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Teachplan::getParentid, teachplanSource.getParentid());
            wrapper.eq(Teachplan::getOrderby, orderSource-1);
            Teachplan teachplanBefore = teachplanMapper.selectOne(wrapper);
            if(teachplanBefore==null)
            {
                XueChengPlusException.cast("前面的没有了哦~");
            }
            else
            {
                teachplanBefore.setOrderby(orderSource);
                teachplanSource.setOrderby(orderSource-1);
                teachplanMapper.updateById(teachplanBefore);
                teachplanMapper.updateById(teachplanSource);

            }
        }

    }

}

package com.xuecheng.content.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {

    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Autowired
    CourseMarketMapper courseMarketMapper;
    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Transactional
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {
        //参数校验
        //合法性校验
        //throw new XueChengPlusException("");
        /*if (StringUtils.isBlank(dto.getName())) {
            XueChengPlusException.cast("课程名称为空");
        }

        if (StringUtils.isBlank(dto.getMt())) {
            XueChengPlusException.cast("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getSt())) {
            XueChengPlusException.cast("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getGrade())) {
            XueChengPlusException.cast("课程等级为空");
        }

        if (StringUtils.isBlank(dto.getTeachmode())) {
            XueChengPlusException.cast("教育模式为空");
        }

        if (StringUtils.isBlank(dto.getUsers())) {
            XueChengPlusException.cast("适应人群为空");
        }

        if (StringUtils.isBlank(dto.getCharge())) {
            XueChengPlusException.cast("收费规则为空");
        }*/
        //向课程基本表写数据---course_base
        CourseBase courseBase = new CourseBase();
        //将传入页面的参数放入对象中
        /*courseBase.setCompanyId(companyId);
        courseBase.setName(dto.getName());
        courseBase.setMt(dto.getMt());
        courseBase.setSt(dto.getSt());
        courseBase.setGrade(dto.getGrade());
        courseBase.setTeachmode(dto.getTeachmode());
        courseBase.setUsers(dto.getUsers());*/

        BeanUtils.copyProperties(dto, courseBase);
        courseBase.setCompanyId(companyId);
        courseBase.setCreateDate(LocalDateTime.now());
        //默认状态---审核状态为未提交----发布状态为未发布
        courseBase.setAuditStatus("202002");
        courseBase.setStatus("203001");


        int insert = courseBaseMapper.insert(courseBase);
        if (insert <= 0) {
            throw new RuntimeException("添加课程失败");
        }
        QueryWrapper<CourseBase> queryWrapper = new QueryWrapper<>();


        //向课程营销表写数据---course_market
        CourseMarket courseMarket = new CourseMarket();
        //将页面输入的数据拷贝到courseMarketNew中
        BeanUtils.copyProperties(dto, courseMarket);
        //课程id
        courseMarket.setId(courseBase.getId());
        //courseMarket.setPrice(dto.getPrice());

        //保存营销信息
        saveCourseMarket(courseMarket);
        //从数据库中查询课程详细信息，包括
        CourseBaseInfoDto courseBaseInfo = getCourseBaseInfo(courseBase.getId());

        return courseBaseInfo;
    }

    //查询课程信息
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId) {
        //从课程基本信息表查询
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) return null;

        //课程营销表查询
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);

        //组装
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
        if (courseMarket != null) {
            BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
        }


        //todo:课程分类的名称设置到courseBaseInfo中

        CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBase.getSt());
        courseBaseInfoDto.setStName(courseCategoryBySt.getName());

        CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBase.getMt());
        courseBaseInfoDto.setMtName(courseCategoryByMt.getName());


        return courseBaseInfoDto;

    }

    @Override
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto) {
        Long courseId = editCourseDto.getId();
        CourseBase sourceCourseBase = courseBaseMapper.selectById(courseId);
        if (sourceCourseBase == null) {
            XueChengPlusException.cast("课程不存在");

        }
        //数据合法性校验
        //根据具体的业务逻辑去校验
        //本机构 只能修改 本机构CompanyId 的课程
        Long sourceCompanyId = sourceCourseBase.getCompanyId();
        if (!companyId.equals(sourceCompanyId)) {
            XueChengPlusException.cast("本机构只能修改本机构课程的内容！！");
        }

        //封装数据
        BeanUtils.copyProperties(editCourseDto, sourceCourseBase);
        //TODO 营销信息填入
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(editCourseDto, courseMarket);
        courseMarket.setId(courseId);
        saveCourseMarket(courseMarket);


        //更新数据库
        //修改时间
        sourceCourseBase.setChangeDate(LocalDateTime.now());
        //修改人
        sourceCourseBase.setChangePeople(editCourseDto.getUsers());

        //更新
        int i = courseBaseMapper.updateById(sourceCourseBase);
        if(i <= 0) {
            XueChengPlusException.cast("修改课程失败");
        }

        CourseBaseInfoDto courseBaseInfo = getCourseBaseInfo(courseId);

        return courseBaseInfo;
    }

    //保存营销信息的方法-----逻辑：存在就添加，不存在不添加
    private int saveCourseMarket(CourseMarket courseMarket) {
        //参数合法性校验
        String charge = courseMarket.getCharge();
        if (StringUtils.isBlank(charge)) {
            throw new RuntimeException("收费规则为空");
        }
        //如果课程收费，价格没有填写，也要抛出异常
        if (charge.equals("201001")) {
            Float price = courseMarket.getPrice();
            if (price == null || price <= 0) {
                XueChengPlusException.cast("课程价格不能为空且必须大于0");
            }
        }

        //从数据库查询营销信息，存在-->更新；不存在--->添加
        CourseMarket courseMarketObj = courseMarketMapper.selectById(courseMarket.getId());
        if (courseMarketObj == null) {
            //插入数据
            int insert = courseMarketMapper.insert(courseMarket);
            return insert;
        } else {
            //将courseMarket拷贝到courseMarket1中
            BeanUtils.copyProperties(courseMarket, courseMarketObj);
            courseMarketObj.setId(courseMarket.getId());
            //更新数据库
            int update = courseMarketMapper.updateById(courseMarketObj);
            return update;
        }

    }

    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto courseParamsDto) {

        //拼装查询条件
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        //名称模糊查询
        queryWrapper.like(StringUtils.isNotEmpty(courseParamsDto.getCourseName()),
                CourseBase::getName,
                courseParamsDto.getCourseName());

        //根据课程审核状态查询----->精确查询
        queryWrapper.eq(StringUtils.isNotEmpty(courseParamsDto.getAuditStatus()),
                CourseBase::getAuditStatus,
                courseParamsDto.getAuditStatus()
        );
        //todo:按课程发布状态进行查询
        queryWrapper.eq(StringUtils.isNotEmpty(courseParamsDto.getPublishStatus()),
                CourseBase::getStatus,
                courseParamsDto.getPublishStatus()
        );

        //分页
//        //创建分页对象
//        pageParams.setPageNo(1L);
//        pageParams.setPageSize(2L);
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);
        List<CourseBase> items = pageResult.getRecords();
        long total = pageResult.getTotal();

        PageResult<CourseBase> courseBasePageResult = new PageResult<CourseBase>(
                items,
                total,
                pageParams.getPageNo(),
                pageParams.getPageSize());

        System.out.println(courseBasePageResult);

        return courseBasePageResult;
    }


}

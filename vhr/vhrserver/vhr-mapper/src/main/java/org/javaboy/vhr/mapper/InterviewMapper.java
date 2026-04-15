package org.javaboy.vhr.mapper;

import org.apache.ibatis.annotations.Param;
import org.javaboy.vhr.model.Interview;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 面试Mapper接口
 */
public interface InterviewMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Interview record);

    int insertSelective(Interview record);

    Interview selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Interview record);

    int updateByPrimaryKey(Interview record);

    /**
     * 分页查询面试列表
     */
    List<Interview> getInterviewByPage(@Param("page") Integer page, @Param("size") Integer size,
                                        @Param("interview") Interview interview);

    /**
     * 获取面试总数
     */
    Long getTotal(@Param("interview") Interview interview);

    /**
     * 批量插入面试
     */
    Integer batchInsert(@Param("list") List<Interview> list);

    /**
     * 根据简历ID查询面试
     */
    List<Interview> getInterviewByResumeId(@Param("resumeId") Integer resumeId);

    /**
     * 统计面试数据
     */
    List<Map<String, Object>> getInterviewStatistics(@Param("startDate") Date startDate,
                                                      @Param("endDate") Date endDate);

    /**
     * 更新面试状态
     */
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
}

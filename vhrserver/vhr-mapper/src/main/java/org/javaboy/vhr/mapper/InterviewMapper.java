package org.javaboy.vhr.mapper;

import org.apache.ibatis.annotations.Param;
import org.javaboy.vhr.model.Interview;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface InterviewMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Interview record);

    int insertSelective(Interview record);

    Interview selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Interview record);

    int updateByPrimaryKey(Interview record);

    List<Interview> getInterviewByPage(@Param("page") Integer page, @Param("size") Integer size, @Param("interview") Interview interview);

    Long getTotal(@Param("interview") Interview interview);

    int batchInsert(@Param("list") List<Interview> list);

    List<Map<String, Object>> getInterviewStatistics(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}

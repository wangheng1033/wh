package org.javaboy.vhr.mapper;

import org.apache.ibatis.annotations.Param;
import org.javaboy.vhr.model.Resume;

import java.util.List;

/**
 * 简历Mapper接口
 */
public interface ResumeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Resume record);

    int insertSelective(Resume record);

    Resume selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Resume record);

    int updateByPrimaryKey(Resume record);

    /**
     * 分页查询简历列表
     */
    List<Resume> getResumeByPage(@Param("page") Integer page, @Param("size") Integer size,
                                  @Param("resume") Resume resume, @Param("keyword") String keyword);

    /**
     * 获取简历总数
     */
    Long getTotal(@Param("resume") Resume resume, @Param("keyword") String keyword);

    /**
     * 批量插入简历
     */
    Integer batchInsert(@Param("list") List<Resume> list);

    /**
     * 根据状态查询简历
     */
    List<Resume> getResumeByStatus(@Param("status") Integer status);

    /**
     * 更新简历状态
     */
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
}

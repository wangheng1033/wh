package org.javaboy.vhr.mapper;

import org.apache.ibatis.annotations.Param;
import org.javaboy.vhr.model.Resume;

import java.util.List;

public interface ResumeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Resume record);

    int insertSelective(Resume record);

    Resume selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Resume record);

    int updateByPrimaryKey(Resume record);

    List<Resume> getResumeByPage(@Param("page") Integer page, @Param("size") Integer size, @Param("resume") Resume resume);

    Long getTotal(@Param("resume") Resume resume);

    List<Resume> getResumesByIds(@Param("ids") List<Integer> ids);

    int updateStatus(@Param("id") Integer id, @Param("status") String status);

    List<Resume> getResumesForExport(@Param("resume") Resume resume);
}

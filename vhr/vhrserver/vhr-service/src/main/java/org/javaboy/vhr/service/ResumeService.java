package org.javaboy.vhr.service;

import org.javaboy.vhr.mapper.ResumeMapper;
import org.javaboy.vhr.model.RespPageBean;
import org.javaboy.vhr.model.Resume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 简历Service
 */
@Service
public class ResumeService {

    @Autowired
    private ResumeMapper resumeMapper;

    /**
     * 分页查询简历列表
     */
    public RespPageBean getResumeByPage(Integer page, Integer size, Resume resume, String keyword) {
        if (page != null && size != null) {
            page = (page - 1) * size;
        }
        List<Resume> data = resumeMapper.getResumeByPage(page, size, resume, keyword);
        Long total = resumeMapper.getTotal(resume, keyword);
        RespPageBean bean = new RespPageBean();
        bean.setData(data);
        bean.setTotal(total);
        return bean;
    }

    /**
     * 添加简历
     */
    public Integer addResume(Resume resume) {
        if (resume.getStatus() == null) {
            resume.setStatus(0);
        }
        if (resume.getSource() == null) {
            resume.setSource(0);
        }
        return resumeMapper.insertSelective(resume);
    }

    /**
     * 更新简历
     */
    public Integer updateResume(Resume resume) {
        return resumeMapper.updateByPrimaryKeySelective(resume);
    }

    /**
     * 删除简历
     */
    public Integer deleteResumeById(Integer id) {
        return resumeMapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据ID查询简历
     */
    public Resume getResumeById(Integer id) {
        return resumeMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量添加简历
     */
    public Integer batchAddResumes(List<Resume> list) {
        return resumeMapper.batchInsert(list);
    }

    /**
     * 根据状态查询简历
     */
    public List<Resume> getResumeByStatus(Integer status) {
        return resumeMapper.getResumeByStatus(status);
    }

    /**
     * 更新简历状态
     */
    public Integer updateResumeStatus(Integer id, Integer status) {
        return resumeMapper.updateStatus(id, status);
    }
}

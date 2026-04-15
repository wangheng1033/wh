package org.javaboy.vhr.service;

import org.javaboy.vhr.mapper.InterviewMapper;
import org.javaboy.vhr.mapper.ResumeMapper;
import org.javaboy.vhr.model.Interview;
import org.javaboy.vhr.model.RespPageBean;
import org.javaboy.vhr.model.Resume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 面试Service
 */
@Service
public class InterviewService {

    @Autowired
    private InterviewMapper interviewMapper;

    @Autowired
    private ResumeMapper resumeMapper;

    /**
     * 分页查询面试列表
     */
    public RespPageBean getInterviewByPage(Integer page, Integer size, Interview interview) {
        if (page != null && size != null) {
            page = (page - 1) * size;
        }
        List<Interview> data = interviewMapper.getInterviewByPage(page, size, interview);
        Long total = interviewMapper.getTotal(interview);
        RespPageBean bean = new RespPageBean();
        bean.setData(data);
        bean.setTotal(total);
        return bean;
    }

    /**
     * 添加面试安排
     */
    @Transactional
    public Integer addInterview(Interview interview) {
        if (interview.getStatus() == null) {
            interview.setStatus(0);
        }
        int result = interviewMapper.insertSelective(interview);
        if (result == 1) {
            resumeMapper.updateStatus(interview.getResumeId(), 2);
        }
        return result;
    }

    /**
     * 批量添加面试
     */
    @Transactional
    public Integer batchAddInterviews(List<Interview> list) {
        for (Interview interview : list) {
            if (interview.getStatus() == null) {
                interview.setStatus(0);
            }
        }
        int result = interviewMapper.batchInsert(list);
        if (result > 0) {
            for (Interview interview : list) {
                resumeMapper.updateStatus(interview.getResumeId(), 2);
            }
        }
        return result;
    }

    /**
     * 更新面试
     */
    public Integer updateInterview(Interview interview) {
        return interviewMapper.updateByPrimaryKeySelective(interview);
    }

    /**
     * 删除面试
     */
    public Integer deleteInterviewById(Integer id) {
        return interviewMapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据ID查询面试
     */
    public Interview getInterviewById(Integer id) {
        return interviewMapper.selectByPrimaryKey(id);
    }

    /**
     * 根据简历ID查询面试
     */
    public List<Interview> getInterviewByResumeId(Integer resumeId) {
        return interviewMapper.getInterviewByResumeId(resumeId);
    }

    /**
     * 更新面试状态
     */
    @Transactional
    public Integer updateInterviewStatus(Integer id, Integer status) {
        Interview interview = interviewMapper.selectByPrimaryKey(id);
        int result = interviewMapper.updateStatus(id, status);
        if (result == 1 && interview != null) {
            if (status == 1) {
                resumeMapper.updateStatus(interview.getResumeId(), 4);
            } else if (status == 2) {
                resumeMapper.updateStatus(interview.getResumeId(), 3);
            }
        }
        return result;
    }

    /**
     * 获取面试统计
     */
    public List<Map<String, Object>> getInterviewStatistics(Date startDate, Date endDate) {
        return interviewMapper.getInterviewStatistics(startDate, endDate);
    }
}

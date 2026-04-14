package org.javaboy.vhr.service;

import org.javaboy.vhr.mapper.InterviewMapper;
import org.javaboy.vhr.mapper.ResumeMapper;
import org.javaboy.vhr.model.Interview;
import org.javaboy.vhr.model.RespPageBean;
import org.javaboy.vhr.model.Resume;
import org.javaboy.vhr.utils.HrUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class InterviewService {
    @Autowired
    InterviewMapper interviewMapper;
    @Autowired
    ResumeMapper resumeMapper;
    @Autowired
    ResumeService resumeService;

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

    @Transactional
    public Integer addInterview(Interview interview) {
        interview.setCreateBy(HrUtils.getCurrentHr().getId());
        interview.setInterviewStatus("待面试");
        interview.setNotifyStatus("未通知");
        interview.setCreateTime(new Date());
        int result = interviewMapper.insertSelective(interview);
        if (result == 1) {
            resumeService.updateResumeStatus(interview.getResumeId(), "待面试");
        }
        return result;
    }

    @Transactional
    public Integer batchAddInterview(List<Integer> resumeIds, Interview interviewTemplate) {
        List<Resume> resumes = resumeMapper.getResumesByIds(resumeIds);
        List<Interview> interviews = new ArrayList<>();
        for (Resume resume : resumes) {
            Interview interview = new Interview();
            interview.setResumeId(resume.getId());
            interview.setCandidateName(resume.getName());
            interview.setPositionId(interviewTemplate.getPositionId());
            interview.setDepartmentId(interviewTemplate.getDepartmentId());
            interview.setInterviewTime(interviewTemplate.getInterviewTime());
            interview.setInterviewLocation(interviewTemplate.getInterviewLocation());
            interview.setInterviewerId(interviewTemplate.getInterviewerId());
            interview.setInterviewerName(interviewTemplate.getInterviewerName());
            interview.setInterviewType(interviewTemplate.getInterviewType());
            interview.setCreateBy(HrUtils.getCurrentHr().getId());
            interview.setInterviewStatus("待面试");
            interview.setNotifyStatus("未通知");
            interview.setCreateTime(new Date());
            interviews.add(interview);
            resumeService.updateResumeStatus(resume.getId(), "待面试");
        }
        return interviewMapper.batchInsert(interviews);
    }

    public Integer updateInterview(Interview interview) {
        return interviewMapper.updateByPrimaryKeySelective(interview);
    }

    public Integer deleteInterviewById(Integer id) {
        return interviewMapper.deleteByPrimaryKey(id);
    }

    public Interview getInterviewById(Integer id) {
        return interviewMapper.selectByPrimaryKey(id);
    }

    @Transactional
    public Integer submitInterviewResult(Interview interview) {
        int result = interviewMapper.updateByPrimaryKeySelective(interview);
        if (result == 1 && interview.getResult() != null) {
            if ("通过".equals(interview.getResult())) {
                resumeService.updateResumeStatus(interview.getResumeId(), "已筛选");
            } else if ("未通过".equals(interview.getResult())) {
                resumeService.updateResumeStatus(interview.getResumeId(), "已拒绝");
            }
        }
        return result;
    }

    public List<Map<String, Object>> getInterviewStatistics(Date startDate, Date endDate) {
        return interviewMapper.getInterviewStatistics(startDate, endDate);
    }
}

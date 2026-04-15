package org.javaboy.vhr.controller.recruit;

import org.javaboy.vhr.model.Interview;
import org.javaboy.vhr.model.RespBean;
import org.javaboy.vhr.model.RespPageBean;
import org.javaboy.vhr.service.InterviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 面试管理Controller
 */
@RestController
@RequestMapping("/recruit/interview")
public class InterviewController {

    @Autowired
    private InterviewService interviewService;

    /**
     * 分页查询面试列表
     */
    @GetMapping("/")
    public RespPageBean getInterviewByPage(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           Interview interview) {
        return interviewService.getInterviewByPage(page, size, interview);
    }

    /**
     * 添加面试安排
     */
    @PostMapping("/")
    public RespBean addInterview(@RequestBody Interview interview) {
        if (interviewService.addInterview(interview) == 1) {
            return RespBean.ok("面试安排成功!");
        }
        return RespBean.error("面试安排失败!");
    }

    /**
     * 批量添加面试
     */
    @PostMapping("/batch")
    public RespBean batchAddInterviews(@RequestBody List<Interview> list) {
        if (interviewService.batchAddInterviews(list) == list.size()) {
            return RespBean.ok("批量安排成功!");
        }
        return RespBean.error("批量安排失败!");
    }

    /**
     * 更新面试
     */
    @PutMapping("/")
    public RespBean updateInterview(@RequestBody Interview interview) {
        if (interviewService.updateInterview(interview) == 1) {
            return RespBean.ok("更新成功!");
        }
        return RespBean.error("更新失败!");
    }

    /**
     * 删除面试
     */
    @DeleteMapping("/{id}")
    public RespBean deleteInterviewById(@PathVariable Integer id) {
        if (interviewService.deleteInterviewById(id) == 1) {
            return RespBean.ok("删除成功!");
        }
        return RespBean.error("删除失败!");
    }

    /**
     * 根据ID查询面试
     */
    @GetMapping("/{id}")
    public Interview getInterviewById(@PathVariable Integer id) {
        return interviewService.getInterviewById(id);
    }

    /**
     * 根据简历ID查询面试
     */
    @GetMapping("/resume/{resumeId}")
    public List<Interview> getInterviewByResumeId(@PathVariable Integer resumeId) {
        return interviewService.getInterviewByResumeId(resumeId);
    }

    /**
     * 更新面试状态
     */
    @PutMapping("/status/{id}")
    public RespBean updateInterviewStatus(@PathVariable Integer id, @RequestParam Integer status) {
        if (interviewService.updateInterviewStatus(id, status) == 1) {
            return RespBean.ok("状态更新成功!");
        }
        return RespBean.error("状态更新失败!");
    }

    /**
     * 填写面试评价
     */
    @PutMapping("/evaluation/{id}")
    public RespBean updateInterviewEvaluation(@PathVariable Integer id, @RequestBody Interview interview) {
        interview.setId(id);
        if (interviewService.updateInterview(interview) == 1) {
            return RespBean.ok("评价提交成功!");
        }
        return RespBean.error("评价提交失败!");
    }

    /**
     * 面试统计
     */
    @GetMapping("/statistics")
    public List<Map<String, Object>> getInterviewStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return interviewService.getInterviewStatistics(startDate, endDate);
    }
}

package org.javaboy.vhr.controller.recruit;

import org.javaboy.vhr.model.RespBean;
import org.javaboy.vhr.model.RespPageBean;
import org.javaboy.vhr.model.Resume;
import org.javaboy.vhr.service.ResumeService;
import org.javaboy.vhr.utils.POIUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 简历管理Controller
 */
@RestController
@RequestMapping("/recruit/resume")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    /**
     * 分页查询简历列表
     */
    @GetMapping("/")
    public RespPageBean getResumeByPage(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer size,
                                        Resume resume,
                                        @RequestParam(required = false) String keyword) {
        return resumeService.getResumeByPage(page, size, resume, keyword);
    }

    /**
     * 添加简历
     */
    @PostMapping("/")
    public RespBean addResume(@RequestBody Resume resume) {
        if (resumeService.addResume(resume) == 1) {
            return RespBean.ok("添加成功!");
        }
        return RespBean.error("添加失败!");
    }

    /**
     * 更新简历
     */
    @PutMapping("/")
    public RespBean updateResume(@RequestBody Resume resume) {
        if (resumeService.updateResume(resume) == 1) {
            return RespBean.ok("更新成功!");
        }
        return RespBean.error("更新失败!");
    }

    /**
     * 删除简历
     */
    @DeleteMapping("/{id}")
    public RespBean deleteResumeById(@PathVariable Integer id) {
        if (resumeService.deleteResumeById(id) == 1) {
            return RespBean.ok("删除成功!");
        }
        return RespBean.error("删除失败!");
    }

    /**
     * 根据ID查询简历
     */
    @GetMapping("/{id}")
    public Resume getResumeById(@PathVariable Integer id) {
        return resumeService.getResumeById(id);
    }

    /**
     * 更新简历状态
     */
    @PutMapping("/status/{id}")
    public RespBean updateResumeStatus(@PathVariable Integer id, @RequestParam Integer status) {
        if (resumeService.updateResumeStatus(id, status) == 1) {
            return RespBean.ok("状态更新成功!");
        }
        return RespBean.error("状态更新失败!");
    }

    /**
     * 根据状态查询简历
     */
    @GetMapping("/status/{status}")
    public List<Resume> getResumeByStatus(@PathVariable Integer status) {
        return resumeService.getResumeByStatus(status);
    }

    /**
     * 导出简历数据
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportData() {
        List<Resume> list = (List<Resume>) resumeService.getResumeByPage(null, null, new Resume(), null).getData();
        return POIUtils.resume2Excel(list);
    }

    /**
     * 批量导入简历
     */
    @PostMapping("/import")
    public RespBean importData(MultipartFile file) throws IOException {
        List<Resume> list = POIUtils.excel2Resume(file);
        if (resumeService.batchAddResumes(list) == list.size()) {
            return RespBean.ok("导入成功");
        }
        return RespBean.error("导入失败");
    }
}

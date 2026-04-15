package org.javaboy.vhr.service;

import org.javaboy.vhr.mapper.ResumeMapper;
import org.javaboy.vhr.model.RespPageBean;
import org.javaboy.vhr.model.Resume;
import org.javaboy.vhr.utils.HrUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ResumeService {
    @Autowired
    ResumeMapper resumeMapper;

    public RespPageBean getResumeByPage(Integer page, Integer size, Resume resume) {
        if (page != null && size != null) {
            page = (page - 1) * size;
        }
        List<Resume> data = resumeMapper.getResumeByPage(page, size, resume);
        Long total = resumeMapper.getTotal(resume);
        RespPageBean bean = new RespPageBean();
        bean.setData(data);
        bean.setTotal(total);
        return bean;
    }

    public Integer addResume(Resume resume) {
        resume.setHrId(HrUtils.getCurrentHr().getId());
        resume.setStatus("待筛选");
        resume.setCreateTime(new Date());
        return resumeMapper.insertSelective(resume);
    }

    public Integer updateResume(Resume resume) {
        return resumeMapper.updateByPrimaryKeySelective(resume);
    }

    public Integer deleteResumeById(Integer id) {
        return resumeMapper.deleteByPrimaryKey(id);
    }

    public Resume getResumeById(Integer id) {
        return resumeMapper.selectByPrimaryKey(id);
    }

    public Integer updateResumeStatus(Integer id, String status) {
        return resumeMapper.updateStatus(id, status);
    }

    public List<Resume> getResumesForExport(Resume resume) {
        return resumeMapper.getResumesForExport(resume);
    }

    public String uploadAttachment(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return null;
        }
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + extension;
        String uploadPath = "uploads/resumes/";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        File destFile = new File(uploadPath + fileName);
        file.transferTo(destFile);
        return uploadPath + fileName;
    }

    public Integer importResumes(List<Resume> resumes) {
        for (Resume resume : resumes) {
            resume.setHrId(HrUtils.getCurrentHr().getId());
            resume.setStatus("待筛选");
            resume.setCreateTime(new Date());
            resumeMapper.insertSelective(resume);
        }
        return resumes.size();
    }
}

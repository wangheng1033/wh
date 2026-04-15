package org.javaboy.vhr.service;

import org.javaboy.vhr.mapper.InterviewMapper;
import org.javaboy.vhr.mapper.OfferMapper;
import org.javaboy.vhr.mapper.ResumeMapper;
import org.javaboy.vhr.model.Offer;
import org.javaboy.vhr.model.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Offer Service
 */
@Service
public class OfferService {

    @Autowired
    private OfferMapper offerMapper;

    @Autowired
    private ResumeMapper resumeMapper;

    @Autowired
    private InterviewMapper interviewMapper;

    /**
     * 分页查询Offer列表
     */
    public RespPageBean getOfferByPage(Integer page, Integer size, Offer offer) {
        if (page != null && size != null) {
            page = (page - 1) * size;
        }
        List<Offer> data = offerMapper.getOfferByPage(page, size, offer);
        Long total = offerMapper.getTotal(offer);
        RespPageBean bean = new RespPageBean();
        bean.setData(data);
        bean.setTotal(total);
        return bean;
    }

    /**
     * 添加Offer
     */
    public Integer addOffer(Offer offer) {
        if (offer.getStatus() == null) {
            offer.setStatus(0);
        }
        if (offer.getProbationPeriod() == null) {
            offer.setProbationPeriod(3);
        }
        return offerMapper.insertSelective(offer);
    }

    /**
     * 更新Offer
     */
    public Integer updateOffer(Offer offer) {
        return offerMapper.updateByPrimaryKeySelective(offer);
    }

    /**
     * 删除Offer
     */
    public Integer deleteOfferById(Integer id) {
        return offerMapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据ID查询Offer
     */
    public Offer getOfferById(Integer id) {
        return offerMapper.selectByPrimaryKey(id);
    }

    /**
     * 根据简历ID查询Offer
     */
    public Offer getOfferByResumeId(Integer resumeId) {
        return offerMapper.getOfferByResumeId(resumeId);
    }

    /**
     * 发送Offer
     */
    @Transactional
    public Integer sendOffer(Integer id) {
        Offer offer = new Offer();
        offer.setId(id);
        offer.setStatus(1);
        offer.setSendTime(new Date());
        return offerMapper.updateByPrimaryKeySelective(offer);
    }

    /**
     * 更新Offer状态
     */
    @Transactional
    public Integer updateOfferStatus(Integer id, Integer status, String rejectReason) {
        Offer offer = new Offer();
        offer.setId(id);
        offer.setStatus(status);
        if (status == 2) {
            offer.setConfirmTime(new Date());
        }
        if (rejectReason != null) {
            offer.setRejectReason(rejectReason);
        }
        int result = offerMapper.updateByPrimaryKeySelective(offer);
        if (result == 1) {
            Offer o = offerMapper.selectByPrimaryKey(id);
            if (o != null) {
                if (status == 2) {
                    resumeMapper.updateStatus(o.getResumeId(), 4);
                } else if (status == 3) {
                    resumeMapper.updateStatus(o.getResumeId(), 0);
                }
            }
        }
        return result;
    }

    /**
     * 更新查看状态
     */
    public Integer updateViewStatus(Integer id) {
        return offerMapper.updateViewStatus(id, 1, new Date());
    }

    /**
     * 查询即将过期的Offer
     */
    public List<Offer> getExpiringOffers(Date date) {
        return offerMapper.getExpiringOffers(date);
    }

    /**
     * 查询即将入职的Offer
     */
    public List<Offer> getUpcomingEntryOffers(Date startDate, Date endDate) {
        return offerMapper.getUpcomingEntryOffers(startDate, endDate);
    }
}

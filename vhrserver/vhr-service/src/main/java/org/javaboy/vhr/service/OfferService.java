package org.javaboy.vhr.service;

import org.javaboy.vhr.mapper.OfferMapper;
import org.javaboy.vhr.model.Offer;
import org.javaboy.vhr.model.RespPageBean;
import org.javaboy.vhr.utils.HrUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class OfferService {
    @Autowired
    OfferMapper offerMapper;
    @Autowired
    ResumeService resumeService;

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

    @Transactional
    public Integer addOffer(Offer offer) {
        offer.setCreateBy(HrUtils.getCurrentHr().getId());
        offer.setStatus("待发送");
        offer.setViewStatus("未查看");
        offer.setEntryReminderSent(false);
        offer.setCreateTime(new Date());
        int result = offerMapper.insertSelective(offer);
        if (result == 1) {
            resumeService.updateResumeStatus(offer.getResumeId(), "已录用");
        }
        return result;
    }

    public Integer updateOffer(Offer offer) {
        return offerMapper.updateByPrimaryKeySelective(offer);
    }

    public Integer deleteOfferById(Integer id) {
        return offerMapper.deleteByPrimaryKey(id);
    }

    public Offer getOfferById(Integer id) {
        return offerMapper.selectByPrimaryKey(id);
    }

    @Transactional
    public Integer sendOffer(Integer id) {
        Offer offer = new Offer();
        offer.setId(id);
        offer.setStatus("已发送");
        offer.setSendTime(new Date());
        return offerMapper.updateByPrimaryKeySelective(offer);
    }

    public Integer updateOfferStatus(Integer id, String status, String rejectReason) {
        Offer offer = new Offer();
        offer.setId(id);
        offer.setStatus(status);
        offer.setConfirmTime(new Date());
        if ("已拒绝".equals(status) && rejectReason != null) {
            offer.setRejectReason(rejectReason);
        }
        return offerMapper.updateByPrimaryKeySelective(offer);
    }

    public Integer markAsViewed(Integer id) {
        return offerMapper.updateViewStatus(id);
    }

    public List<Offer> getOffersNeedReminder() {
        return offerMapper.getOffersNeedReminder();
    }

    @Transactional
    public Integer sendEntryReminder(Integer offerId) {
        Offer offer = new Offer();
        offer.setId(offerId);
        offer.setEntryReminderSent(true);
        return offerMapper.updateByPrimaryKeySelective(offer);
    }
}

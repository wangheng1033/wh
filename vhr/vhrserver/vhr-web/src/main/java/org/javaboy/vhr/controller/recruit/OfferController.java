package org.javaboy.vhr.controller.recruit;

import org.javaboy.vhr.model.Offer;
import org.javaboy.vhr.model.RespBean;
import org.javaboy.vhr.model.RespPageBean;
import org.javaboy.vhr.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Offer管理Controller
 */
@RestController
@RequestMapping("/recruit/offer")
public class OfferController {

    @Autowired
    private OfferService offerService;

    /**
     * 分页查询Offer列表
     */
    @GetMapping("/")
    public RespPageBean getOfferByPage(@RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer size,
                                       Offer offer) {
        return offerService.getOfferByPage(page, size, offer);
    }

    /**
     * 添加Offer
     */
    @PostMapping("/")
    public RespBean addOffer(@RequestBody Offer offer) {
        if (offerService.addOffer(offer) == 1) {
            return RespBean.ok("创建成功!");
        }
        return RespBean.error("创建失败!");
    }

    /**
     * 更新Offer
     */
    @PutMapping("/")
    public RespBean updateOffer(@RequestBody Offer offer) {
        if (offerService.updateOffer(offer) == 1) {
            return RespBean.ok("更新成功!");
        }
        return RespBean.error("更新失败!");
    }

    /**
     * 删除Offer
     */
    @DeleteMapping("/{id}")
    public RespBean deleteOfferById(@PathVariable Integer id) {
        if (offerService.deleteOfferById(id) == 1) {
            return RespBean.ok("删除成功!");
        }
        return RespBean.error("删除失败!");
    }

    /**
     * 根据ID查询Offer
     */
    @GetMapping("/{id}")
    public Offer getOfferById(@PathVariable Integer id) {
        return offerService.getOfferById(id);
    }

    /**
     * 根据简历ID查询Offer
     */
    @GetMapping("/resume/{resumeId}")
    public Offer getOfferByResumeId(@PathVariable Integer resumeId) {
        return offerService.getOfferByResumeId(resumeId);
    }

    /**
     * 发送Offer
     */
    @PutMapping("/send/{id}")
    public RespBean sendOffer(@PathVariable Integer id) {
        if (offerService.sendOffer(id) == 1) {
            return RespBean.ok("发送成功!");
        }
        return RespBean.error("发送失败!");
    }

    /**
     * 更新Offer状态
     */
    @PutMapping("/status/{id}")
    public RespBean updateOfferStatus(@PathVariable Integer id,
                                      @RequestParam Integer status,
                                      @RequestParam(required = false) String rejectReason) {
        if (offerService.updateOfferStatus(id, status, rejectReason) == 1) {
            return RespBean.ok("状态更新成功!");
        }
        return RespBean.error("状态更新失败!");
    }

    /**
     * 更新查看状态
     */
    @PutMapping("/view/{id}")
    public RespBean updateViewStatus(@PathVariable Integer id) {
        if (offerService.updateViewStatus(id) == 1) {
            return RespBean.ok("更新成功!");
        }
        return RespBean.error("更新失败!");
    }

    /**
     * 查询即将过期的Offer
     */
    @GetMapping("/expiring")
    public List<Offer> getExpiringOffers(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        return offerService.getExpiringOffers(date);
    }

    /**
     * 查询即将入职的Offer
     */
    @GetMapping("/upcoming")
    public List<Offer> getUpcomingEntryOffers(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return offerService.getUpcomingEntryOffers(startDate, endDate);
    }
}

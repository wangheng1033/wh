package org.javaboy.vhr.mapper;

import org.apache.ibatis.annotations.Param;
import org.javaboy.vhr.model.Offer;

import java.util.Date;
import java.util.List;

/**
 * Offer Mapper接口
 */
public interface OfferMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Offer record);

    int insertSelective(Offer record);

    Offer selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Offer record);

    int updateByPrimaryKey(Offer record);

    /**
     * 分页查询Offer列表
     */
    List<Offer> getOfferByPage(@Param("page") Integer page, @Param("size") Integer size,
                                @Param("offer") Offer offer);

    /**
     * 获取Offer总数
     */
    Long getTotal(@Param("offer") Offer offer);

    /**
     * 根据简历ID查询Offer
     */
    Offer getOfferByResumeId(@Param("resumeId") Integer resumeId);

    /**
     * 更新Offer状态
     */
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    /**
     * 更新查看状态
     */
    int updateViewStatus(@Param("id") Integer id, @Param("viewStatus") Integer viewStatus,
                         @Param("viewTime") Date viewTime);

    /**
     * 查询即将过期的Offer
     */
    List<Offer> getExpiringOffers(@Param("date") Date date);

    /**
     * 查询即将入职的Offer
     */
    List<Offer> getUpcomingEntryOffers(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}

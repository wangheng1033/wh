package org.javaboy.vhr.mapper;

import org.apache.ibatis.annotations.Param;
import org.javaboy.vhr.model.Offer;

import java.util.List;

public interface OfferMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Offer record);

    int insertSelective(Offer record);

    Offer selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Offer record);

    int updateByPrimaryKey(Offer record);

    List<Offer> getOfferByPage(@Param("page") Integer page, @Param("size") Integer size, @Param("offer") Offer offer);

    Long getTotal(@Param("offer") Offer offer);

    int updateStatus(@Param("id") Integer id, @Param("status") String status);

    int updateViewStatus(@Param("id") Integer id);

    List<Offer> getOffersNeedReminder();
}

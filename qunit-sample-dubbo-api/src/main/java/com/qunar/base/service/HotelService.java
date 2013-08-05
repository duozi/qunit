package com.qunar.base.service;

import com.qunar.base.model.Hotel;

import java.util.List;

/**
 * User: zhaohuiyu
 * Date: 5/2/13
 * Time: 3:43 PM
 */
public interface HotelService {
    Hotel findById(Integer id);

    void save(Hotel hotel);

    void batchSave(List<Hotel> hotels);
}

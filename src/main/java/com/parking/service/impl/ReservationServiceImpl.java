package com.parking.service.impl;

import com.parking.entity.Reservation;
import com.parking.mapper.ReservationMapper;
import com.parking.service.IReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReservationServiceImpl implements IReservationService {

    @Autowired
    private ReservationMapper reservationMapper;

    @Override
    public int addReservation(Reservation reservation) {
        return reservationMapper.insert(reservation);
    }

    @Override
    public int cancelReservation(Long id) {
        return reservationMapper.updateStatus(id, "cancelled");
    }

    @Override
    public Reservation getReservation(Long id) {
        return reservationMapper.selectById(id);
    }

    @Override
    public List<Reservation> listAll() {
        return reservationMapper.selectAll();
    }
}
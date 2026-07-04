package com.parking.service;

import com.parking.entity.Reservation;
import java.util.List;

public interface IReservationService {
    int addReservation(Reservation reservation);
    int cancelReservation(Long id);
    Reservation getReservation(Long id);
    List<Reservation> listAll();
}
package com.parking.service;

import com.parking.entity.PaymentRecord;
import java.util.List;

public interface IPaymentRecordService {
    int addPayment(PaymentRecord payment);
    PaymentRecord getPayment(Long id);
    PaymentRecord getByRecordId(Long recordId);
    List<PaymentRecord> listAll();
}
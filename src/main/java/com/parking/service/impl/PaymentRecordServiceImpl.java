package com.parking.service.impl;

import com.parking.entity.PaymentRecord;
import com.parking.mapper.PaymentRecordMapper;
import com.parking.service.IPaymentRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PaymentRecordServiceImpl implements IPaymentRecordService {

    @Autowired
    private PaymentRecordMapper paymentRecordMapper;

    @Override
    public int addPayment(PaymentRecord payment) {
        return paymentRecordMapper.insert(payment);
    }

    @Override
    public PaymentRecord getPayment(Long id) {
        return paymentRecordMapper.selectById(id);
    }

    @Override
    public PaymentRecord getByRecordId(Long recordId) {
        return paymentRecordMapper.selectByRecordId(recordId);
    }

    @Override
    public List<PaymentRecord> listAll() {
        return paymentRecordMapper.selectAll();
    }
}
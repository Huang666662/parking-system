package com.parking.service.impl;

import com.parking.entity.UnloadingChannel;
import com.parking.mapper.UnloadingChannelMapper;
import com.parking.service.IUnloadingChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UnloadingChannelServiceImpl implements IUnloadingChannelService {

    @Autowired
    private UnloadingChannelMapper unloadingChannelMapper;

    @Override
    public UnloadingChannel getChannel(Integer id) {
        return unloadingChannelMapper.selectById(id);
    }

    @Override
    public List<UnloadingChannel> listEnabled() {
        return unloadingChannelMapper.selectEnabled();
    }
}
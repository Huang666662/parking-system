package com.parking.service;

import com.parking.entity.UnloadingChannel;
import java.util.List;

public interface IUnloadingChannelService {
    UnloadingChannel getChannel(Integer id);
    List<UnloadingChannel> listEnabled();
}
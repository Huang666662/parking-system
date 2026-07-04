package com.parking.service;

import com.parking.entity.SystemConfig;
import java.util.List;

public interface ISystemConfigService {
    List<SystemConfig> listAll();
    SystemConfig getConfig(Integer id);
    int addConfig(SystemConfig config);
    int updateConfig(SystemConfig config);
    SystemConfig getConfigByKey(String configKey);
    int deleteConfig(Integer id);
}

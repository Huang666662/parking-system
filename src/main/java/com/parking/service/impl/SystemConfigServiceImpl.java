package com.parking.service.impl;

import com.parking.entity.SystemConfig;
import com.parking.mapper.SystemConfigMapper;
import com.parking.service.ISystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SystemConfigServiceImpl implements ISystemConfigService {

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    @Override
    public List<SystemConfig> listAll() {
        return systemConfigMapper.selectAll();
    }

    @Override
    public SystemConfig getConfig(Integer id) {
        return systemConfigMapper.selectById(id);
    }

    @Override
    public int addConfig(SystemConfig config) {
        return systemConfigMapper.insert(config);
    }

    @Override
    public int updateConfig(SystemConfig config) {
        return systemConfigMapper.update(config);
    }

    @Override
    public SystemConfig getConfigByKey(String configKey) {
        return systemConfigMapper.selectByKey(configKey);
    }

    @Override
    public int deleteConfig(Integer id) {
        return systemConfigMapper.deleteById(id);
    }
}

package com.parking.util;

import com.parking.entity.OperationLog;
import com.parking.service.IOperationLogService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OperationLogHelper {

    @Autowired
    private IOperationLogService operationLogService;

    public void log(HttpSession session, String operationType, String operationContent) {
        try {
            OperationLog log = new OperationLog();
            log.setOperationType(operationType);
            log.setOperationContent(operationContent);
            // 尝试从 session 获取操作员信息
            Object adminObj = session.getAttribute("user");
            if (adminObj instanceof com.parking.entity.Admin admin) {
                log.setOperatorId(admin.getId());
                log.setOperatorName(admin.getUsername());
            } else if (adminObj instanceof com.parking.entity.User user) {
                log.setOperatorId(user.getId());
                log.setOperatorName(user.getUsername());
            }
            log.setIpAddress("127.0.0.1");
            operationLogService.addLog(log);
        } catch (Exception ignored) {
            // 日志记录失败不影响主流程
        }
    }
}

package com.parking.entity;

import lombok.Data;

@Data
public class RolePermission {
    private Long id;
    private Integer roleId;
    private Integer permissionId;
}
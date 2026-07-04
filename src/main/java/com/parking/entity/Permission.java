package com.parking.entity;

import lombok.Data;

@Data
public class Permission {
    private Integer id;
    private String permissionName;
    private String permissionCode;
    private Integer parentId;
    private Integer type;
    private Integer sortOrder;
}
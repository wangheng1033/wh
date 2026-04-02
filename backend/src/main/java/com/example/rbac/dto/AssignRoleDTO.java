package com.example.rbac.dto;

import lombok.Data;

import java.util.List;

@Data
public class AssignRoleDTO {
    private List<Long> roleIds;
}

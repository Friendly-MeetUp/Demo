package com.defect.defectTracker.service;
import com.defect.defectTracker.dto.RoleDto;
import java.util.List;


public interface RoleService {

    RoleDto createRole(RoleDto roleDto);
    RoleDto getRoleById(Long id);
    RoleDto updateRole(Long id, RoleDto roleDto);
    void deleteRole(Long id);
    List<RoleDto> getAllRoles();
}

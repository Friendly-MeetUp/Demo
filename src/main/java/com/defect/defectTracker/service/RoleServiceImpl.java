package com.defect.defectTracker.service;

import com.defect.defectTracker.dto.RoleDto;
import com.defect.defectTracker.entity.Role;
import com.defect.defectTracker.repository.RoleRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public RoleDto createRole(@Valid RoleDto roleDto) {

        // Normalize input to avoid duplicates like "Admin" vs "admin"
        String normalizedRoleName = roleDto.getRoleName().trim().toLowerCase();
        log.info("Creating role: {}", normalizedRoleName);

        if (roleRepository.existsByRoleNameIgnoreCase(normalizedRoleName)) {
            log.warn("Role creation failed - Duplicate roleName: {}", normalizedRoleName);
            throw new DataIntegrityViolationException("Role name already exists");
        }

        Role role = new Role();
        role.setRoleName(normalizedRoleName); // Save in lowercase for consistency

        Role savedRole = roleRepository.save(role);
        roleDto.setId(savedRole.getId());
        roleDto.setRoleName(savedRole.getRoleName()); // update dto with normalized name

        log.info("Role created successfully with ID: {}", savedRole.getId());
        return roleDto;
    }

    @Override
    public RoleDto getRoleById(Long id) {
        log.info("Fetching role with ID: {}", id);
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        RoleDto dto = new RoleDto();
        dto.setId(role.getId());
        dto.setRoleName(role.getRoleName());
        return dto;
    }

    @Override
    public RoleDto updateRole(Long id, RoleDto roleDto) {
        log.info("Updating role with ID: {}", id);

        // Find existing role by ID
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Normalize incoming name
        String normalizedNewName = roleDto.getRoleName().trim().toLowerCase();

        // Check if another role with the same name already exists (excluding this ID)
        Optional<Role> existing = roleRepository.findByRoleNameIgnoreCase(normalizedNewName);
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new RuntimeException("Role name already exists");
        }

        // Set the new name and save
        role.setRoleName(normalizedNewName);
        Role updated = roleRepository.save(role);

        roleDto.setId(updated.getId());
        roleDto.setRoleName(updated.getRoleName());

        log.info("Role updated with new name: {}", updated.getRoleName());
        return roleDto;
    }

    @Override
    public void deleteRole(Long id) {
        log.info("Deleting role with ID: {}", id);
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        roleRepository.delete(role);
        log.info("Role deleted successfully: {}", id);
    }

    @Override
    public List<RoleDto> getAllRoles() {
        log.info("Fetching all roles from database");

        List<Role> roles = roleRepository.findAll();
        List<RoleDto> roleDtos = new ArrayList<>();

        for (Role role : roles) {
            RoleDto dto = new RoleDto();
            dto.setId(role.getId());
            dto.setRoleName(role.getRoleName());
            roleDtos.add(dto);
        }

        return roleDtos;
    }
}

package com.defect.defectTracker.controller;
import org.springframework.validation.BindingResult;
import com.defect.defectTracker.dto.RoleDto;
import com.defect.defectTracker.service.RoleService;
import com.defect.defectTracker.utils.StandardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/role")
@RequiredArgsConstructor
@CrossOrigin("*")
public class RoleController {

    private final RoleService roleService;

    // 1. Create Role
    @PostMapping
    public ResponseEntity<StandardResponse> createRole(@RequestBody @Valid RoleDto dto, org.springframework.validation.BindingResult result) {
        log.info("Creating Role with details: {}", dto);

        if (result.hasErrors()) {
            String errorMessage;
            if (result.getFieldError() != null) {
                errorMessage = result.getFieldError().getDefaultMessage();
            } else {
                errorMessage = "Validation error";
            }
            log.warn("Validation failed: {}", errorMessage);
            return ResponseEntity.badRequest().body(
                    new StandardResponse("error", errorMessage, null, 4000)
            );
        }
        try {
            RoleDto created = roleService.createRole(dto);
            log.info("Role created successfully with ID: {}", created.getId());
            return ResponseEntity.ok(new StandardResponse("success", "created successfully", created, 2000));
        } catch (RuntimeException e) {
            log.error("Role creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    new StandardResponse("error", e.getMessage(), null, 4001)
            );
        } catch (Exception e) {
            log.error("Unexpected error while creating Role", e);
            return ResponseEntity.internalServerError().body(
                    new StandardResponse("error", "creation failed", null, 5000)
            );
        }
    }



    // 2. Get All Roles
    @GetMapping
    public ResponseEntity<StandardResponse> getAllRoles() {
        log.info("Fetching all roles");
        try {
            List<RoleDto> roles = roleService.getAllRoles();

            if (roles.isEmpty()) {
                return ResponseEntity.badRequest().body(new StandardResponse("error", "Data not found.", null, 4000));
            }

            return ResponseEntity.ok(new StandardResponse("success", "Retrieved Successfully.", roles, 2000));
        } catch (Exception e) {
            log.error("Error while fetching all roles", e);
            return ResponseEntity.internalServerError().body(
                    new StandardResponse("error", "Retrieve Failed.", null, 4000)
            );
        }
    }

    // 3. Get Role by ID
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse> getRoleById(@PathVariable Long id) {
        log.info("Fetching Role with ID: {}", id);

        // Inline validation
        if (id == null || id <= 0) {
            log.warn("Invalid ID provided: {}", id);
            return ResponseEntity.badRequest().body(
                    new StandardResponse("error", "Invalid ID. ID must be a positive number.", null, 4000)
            );
        }

        try {
            RoleDto role = roleService.getRoleById(id);
            return ResponseEntity.ok(new StandardResponse("success", "Retrieved Successfully.", role, 2000));
        } catch (RuntimeException e) {
            log.warn("Role ID not exist: {}", id);
            return ResponseEntity.badRequest().body(
                    new StandardResponse("error", "Id not exist.", null, 4000)
            );
        } catch (Exception e) {
            log.error("Error while fetching Role", e);
            return ResponseEntity.badRequest().body(
                    new StandardResponse("error", "Retrieve Failed.", null, 4000)
            );
        }
    }
    // 4. Update Role
    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleDto dto,
            org.springframework.validation.BindingResult result) {

        log.info("Updating Role with ID: {}", id);

        //  Validate ID
        if (id == null || id <= 0) {
            log.warn("Invalid ID provided: {}", id);
            return ResponseEntity.badRequest().body(
                    new StandardResponse("error", "Invalid ID. ID must be a positive number.", null, 4000)
            );
        }

        // Validate DTO using BindingResult
        if (result.hasErrors()) {
            String errorMessage = result.getFieldError().getDefaultMessage(); // get first field error
            log.warn("Validation failed while updating Role: {}", errorMessage);
            return ResponseEntity.badRequest().body(
                    new StandardResponse("error", errorMessage, null, 4000)
            );
        }

        try {
            RoleDto updated = roleService.updateRole(id, dto);
            return ResponseEntity.ok(new StandardResponse("success", "Updated Successfully.", updated, 2000));
        } catch (RuntimeException e) {
            log.warn("Role not found for update: {}", id);
            return ResponseEntity.badRequest().body(
                    new StandardResponse("error", "Id not exist.", null, 4000)
            );
        } catch (Exception e) {
            log.error("Error while updating Role", e);
            return ResponseEntity.badRequest().body(
                    new StandardResponse("error", "Update Failed.", null, 4000)
            );
        }
    }

    // 5. Delete Role
    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse> deleteRole(@PathVariable Long id) {
        log.info("Deleting Role with ID: {}", id);
        try {
            roleService.deleteRole(id);
            return ResponseEntity.ok(new StandardResponse("success", "Deleted successfully.", null, 2000));
        } catch (RuntimeException e) {
            log.warn("Role not found for deletion: {}", id);
            return ResponseEntity.badRequest().body(new StandardResponse("error", "Id not exist.", null, 4000));
        } catch (Exception e) {
            log.error("Error while deleting Role", e);
            return ResponseEntity.badRequest().body(new StandardResponse("error", "Delete Failed.", null, 4000));
        }
    }
}

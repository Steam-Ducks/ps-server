package pointsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pointsystem.dto.role.CreateRoleDto;
import pointsystem.dto.role.UpdateRoleDto;
import pointsystem.entity.Role;
import pointsystem.service.RoleService;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/{BusinessId}")
    public ResponseEntity<Integer> createRole(@PathVariable Integer BusinessId, @RequestBody CreateRoleDto createRoleDto)
    {
        int roleId = roleService.createRole(BusinessId, createRoleDto);
        return ResponseEntity.created(URI.create("/api/businesses/" + roleId)).build();
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<Role> getRoleById(@PathVariable int roleId) {
        try {
            Optional<Role> role = roleService.getRoleById(roleId);
            if (role.isPresent()) {
                return ResponseEntity.ok(role.get());
            }
            else
            {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        try {
            List<Role> roles = roleService.getAllRoles();
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<Role> updateRoleById(@PathVariable int roleId, @RequestBody UpdateRoleDto roleDto) {
        try {
            roleService.updateRoleById(roleId, roleDto);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<Role> deleteRoleById(@PathVariable int roleId) {
        try {
            roleService.deleteRoleById(roleId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }
}

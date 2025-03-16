package pointsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pointsystem.dto.role.CreateRoleDto;
import pointsystem.dto.role.UpdateRoleDto;
import pointsystem.entity.Business;
import pointsystem.entity.Role;
import pointsystem.repository.BusinessRepository;
import pointsystem.repository.RoleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final BusinessRepository businessRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository, BusinessRepository businessRepository) {
        this.roleRepository = roleRepository;
        this.businessRepository = businessRepository;
    }

    public Integer createRole(Integer businessId, CreateRoleDto createRoleDto) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Business not found"));

        Role role = new Role(0, createRoleDto.name(), business, createRoleDto.salary());

        Role savedRole = roleRepository.save(role);
        return savedRole.getId();
    }

    public Optional<Role> getRoleById(Integer roleId) {
        return roleRepository.findById(roleId);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public void updateRoleById(Integer roleId, UpdateRoleDto updateRoleDto) {
        Optional<Role> roleEntity = roleRepository.findById(roleId);

        if (roleEntity.isPresent()) {
            Role role = roleEntity.get();
            if(updateRoleDto.name() != null) {
                role.setName(updateRoleDto.name());
            }

            if(updateRoleDto.salary() != null) {
                role.setSalary(updateRoleDto.salary());
            }

            roleRepository.save(role);
        }
    }

    public void deleteRoleById(Integer roleId) {
        boolean exists = roleRepository.existsById(roleId);
        if (exists) {
            roleRepository.deleteById(roleId);
        }
    }
}

package pointsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pointsystem.dto.business.CreateBusinessDto;
import pointsystem.dto.business.UpdateBusinessDto;
import pointsystem.dto.role.CreateRoleDto;
import pointsystem.entity.Business;
import pointsystem.entity.Role;
import pointsystem.repository.BusinessRepository;
import pointsystem.repository.RoleRepository;

import java.util.List;
import java.util.Optional;


@Service
public class BusinessService {

    @Autowired
    private BusinessRepository businessRepository;
    private RoleRepository roleRepository;

    public BusinessService(BusinessRepository businessRepository, RoleRepository roleRepository) {
        this.businessRepository = businessRepository;
        this.roleRepository = roleRepository;
    }

    public Integer createBusiness(CreateBusinessDto createBusinessDto) {
        Business business = new Business(
                0,
                createBusinessDto.name(),
                createBusinessDto.cnpj(),
                createBusinessDto.contact()
        );
        Business savedBusiness = businessRepository.save(business);

        return savedBusiness.getBusinessId();
    }

    public Optional<Business> getBusinessById(Integer businessId) {
        return businessRepository.findById(businessId);
    }

    public List<Business> getAllBusinesses() {
        return businessRepository.findAll();
    }

    public void updatadeBusinessById(Integer businessId, UpdateBusinessDto updateBusinessDto) {
        Optional<Business> businessEntity = businessRepository.findById(businessId);

        if (businessEntity.isPresent()) {
            Business business = businessEntity.get();
            if(updateBusinessDto.name() != null) {
                business.setName(updateBusinessDto.name());
            }

            if(updateBusinessDto.cnpj() != null) {
                business.setCnpj(updateBusinessDto.cnpj());
            }

            if(updateBusinessDto.contact() != null) {
                business.setContact(updateBusinessDto.contact());
            }
            businessRepository.save(business);
        }
    }

    public void deleteBusinessById(Integer businessId) {
        boolean exists = businessRepository.existsById(businessId);
        if (exists) {
            businessRepository.deleteById(businessId);
        }
    }


}
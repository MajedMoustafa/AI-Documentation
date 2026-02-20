package ai.documentation.service;

import ai.documentation.domain.Role;
import ai.documentation.domain.User;
import ai.documentation.domain.UserRole;
import ai.documentation.events.BeforeDeleteRole;
import ai.documentation.events.BeforeDeleteUser;
import ai.documentation.model.UserRoleDTO;
import ai.documentation.repos.RoleRepository;
import ai.documentation.repos.UserRepository;
import ai.documentation.repos.UserRoleRepository;
import ai.documentation.util.NotFoundException;
import ai.documentation.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserRoleService(final UserRoleRepository userRoleRepository,
            final UserRepository userRepository, final RoleRepository roleRepository) {
        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public List<UserRoleDTO> findAll() {
        final List<UserRole> userRoles = userRoleRepository.findAll(Sort.by("id"));
        return userRoles.stream()
                .map(userRole -> mapToDTO(userRole, new UserRoleDTO()))
                .toList();
    }

    public UserRoleDTO get(final Long id) {
        return userRoleRepository.findById(id)
                .map(userRole -> mapToDTO(userRole, new UserRoleDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final UserRoleDTO userRoleDTO) {
        final UserRole userRole = new UserRole();
        mapToEntity(userRoleDTO, userRole);
        return userRoleRepository.save(userRole).getId();
    }

    public void update(final Long id, final UserRoleDTO userRoleDTO) {
        final UserRole userRole = userRoleRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(userRoleDTO, userRole);
        userRoleRepository.save(userRole);
    }

    public void delete(final Long id) {
        final UserRole userRole = userRoleRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        userRoleRepository.delete(userRole);
    }

    private UserRoleDTO mapToDTO(final UserRole userRole, final UserRoleDTO userRoleDTO) {
        userRoleDTO.setId(userRole.getId());
        userRoleDTO.setCreatedAt(userRole.getCreatedAt());
        userRoleDTO.setUser(userRole.getUser() == null ? null : userRole.getUser().getId());
        userRoleDTO.setRole(userRole.getRole() == null ? null : userRole.getRole().getId());
        return userRoleDTO;
    }

    private UserRole mapToEntity(final UserRoleDTO userRoleDTO, final UserRole userRole) {
        userRole.setCreatedAt(userRoleDTO.getCreatedAt());
        final User user = userRoleDTO.getUser() == null ? null : userRepository.findById(userRoleDTO.getUser())
                .orElseThrow(() -> new NotFoundException("user not found"));
        userRole.setUser(user);
        final Role role = userRoleDTO.getRole() == null ? null : roleRepository.findById(userRoleDTO.getRole())
                .orElseThrow(() -> new NotFoundException("role not found"));
        userRole.setRole(role);
        return userRole;
    }

    @EventListener(BeforeDeleteUser.class)
    public void on(final BeforeDeleteUser event) {
        final ReferencedException referencedException = new ReferencedException();
        final UserRole userUserRole = userRoleRepository.findFirstByUserId(event.getId());
        if (userUserRole != null) {
            referencedException.setKey("user.userRole.user.referenced");
            referencedException.addParam(userUserRole.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteRole.class)
    public void on(final BeforeDeleteRole event) {
        final ReferencedException referencedException = new ReferencedException();
        final UserRole roleUserRole = userRoleRepository.findFirstByRoleId(event.getId());
        if (roleUserRole != null) {
            referencedException.setKey("role.userRole.role.referenced");
            referencedException.addParam(roleUserRole.getId());
            throw referencedException;
        }
    }

}

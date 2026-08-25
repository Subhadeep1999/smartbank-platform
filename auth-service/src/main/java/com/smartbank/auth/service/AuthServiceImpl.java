package com.smartbank.auth.service;

import com.smartbank.auth.dto.LoginRequest;
import com.smartbank.auth.dto.LoginResponse;
import com.smartbank.auth.dto.UserRegistrationRequest;
import com.smartbank.auth.dto.UserRegistrationResponse;
import com.smartbank.auth.entity.*;
import com.smartbank.auth.exception.UsernameAlreadyExistsException;
import com.smartbank.auth.repository.*;
import com.smartbank.auth.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final BankRepository bankRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository, BankRepository bankRepository, RoleRepository roleRepository, UserRoleRepository userRoleRepository, PermissionRepository permissionRepository, PermissionRepository permissionRepository1, RolePermissionRepository rolePermissionRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.bankRepository = bankRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.permissionRepository = permissionRepository1;
        this.rolePermissionRepository = rolePermissionRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public UserRegistrationResponse register(UserRegistrationRequest request) {

        // 1. Username uniqueness
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException(
                    "Username already exists: " + request.username()
            );
        }

        // 2. Validate bank
        if (!bankRepository.existsByBankId(request.bankId())) {
            throw new IllegalArgumentException(
                    "Bank not found: " + request.bankId()
            );
        }

        // 3. Validate CUSTOMER-specific CIF
        validateCustomerCif(
                request.role(),
                request.customerCif()
        );

        // 4. Find role
        Role role = roleRepository
                .findByRoleCode(request.role())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Role not found: " + request.role()
                        )
                );

        // 5. Generate business user ID
        String userId = generateUserId();

        // 6. Hash password
        String passwordHash =
                passwordEncoder.encode(request.password());

        // 7. Create User
        User user = new User(
                userId,
                request.username(),
                passwordHash,
                request.bankId(),
                request.customerCif()
        );

        user = userRepository.save(user);

        // 8. Assign role
        UserRole userRole = new UserRole(
                user.getId(),
                role.getId(),
                "SYSTEM"
        );

        userRoleRepository.save(userRole);

        // 9. Build response
        return new UserRegistrationResponse(
                user.getId(),
                user.getUserId(),
                user.getUsername(),
                user.getBankId(),
                user.getCustomerCif(),
                role.getRoleCode(),
                user.getStatus().name()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest loginRequest) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username(),
                        loginRequest.password()
                )
        );

        User user = userRepository
                .findByUsername(loginRequest.username())
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found: "
                                        + loginRequest.username()
                        )
                );

        UserRole userRole = userRoleRepository
                .findByUserIdAndStatus(
                        user.getId(),
                        UserRoleStatus.ACTIVE
                )
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No active role assigned to user: "
                                        + user.getUsername()
                        )
                );

        Role role = roleRepository
                .findById(userRole.getRoleId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Role not found for user: "
                                        + user.getUsername()
                        )
                );

        List<String> permissions = rolePermissionRepository
                .findByRoleIdAndStatus(
                        role.getId(),
                        RolePermissionStatus.ACTIVE
                )
                .stream()
                .map(RolePermission::getPermissionId)
                .map(permissionId -> permissionRepository
                        .findById(permissionId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Permission not found: "
                                                + permissionId
                                )
                        )
                )
                .map(Permission::getPermissionCode)
                .toList();

        String token = jwtService.generateToken(
                user,
                role.getRoleCode().name(),
                permissions
        );

        return new LoginResponse(
                token,
                "Bearer",
                jwtService.getExpiration(),
                user.getUserId(),
                user.getUsername(),
                user.getBankId(),
                role.getRoleCode().name(),
                permissions
        );
    }

    private void validateCustomerCif(
            RoleType role,
            String customerCif
    ) {

        if (role == RoleType.CUSTOMER &&
                (customerCif == null ||
                        customerCif.isBlank())) {

            throw new IllegalArgumentException(
                    "Customer CIF is required for CUSTOMER role"
            );
        }

        if (role != RoleType.CUSTOMER &&
                customerCif != null &&
                !customerCif.isBlank()) {

            throw new IllegalArgumentException(
                    "Customer CIF is only allowed for CUSTOMER role"
            );
        }
    }

    private String generateUserId() {

        Long sequence = userRepository.getNextUserIdSequence();

        return String.format(
                "USR%08d",
                sequence
        );
    }
}

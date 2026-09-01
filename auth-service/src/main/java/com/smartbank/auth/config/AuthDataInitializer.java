package com.smartbank.auth.config;

import com.smartbank.auth.entity.Bank;
import com.smartbank.auth.entity.Permission;
import com.smartbank.auth.entity.Role;
import com.smartbank.auth.entity.RolePermission;
import com.smartbank.auth.entity.RoleType;
import com.smartbank.auth.entity.UserRole;
import com.smartbank.auth.repository.BankRepository;
import com.smartbank.auth.repository.PermissionRepository;
import com.smartbank.auth.repository.RolePermissionRepository;
import com.smartbank.auth.repository.RoleRepository;
import com.smartbank.auth.repository.UserRoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AuthDataInitializer {

    @Bean
    CommandLineRunner initializeAuthData(
            BankRepository bankRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            UserRoleRepository userRoleRepository,
            RolePermissionRepository rolePermissionRepository
    ) {
        return args -> {

            /*
             * 1. Bank
             */
            Bank bank = bankRepository
                    .findByBankId("SB001")
                    .orElseGet(() ->
                            bankRepository.save(
                                    new Bank(
                                            "SB001",
                                            "SmartBank India"
                                    )
                            )
                    );

            /*
             * 2. Roles
             */
            Map<RoleType, Role> roles = new HashMap<>();

            roles.put(
                    RoleType.CUSTOMER,
                    createRole(
                            roleRepository,
                            RoleType.CUSTOMER,
                            "Customer",
                            "Retail banking customer"
                    )
            );

            roles.put(
                    RoleType.TELLER,
                    createRole(
                            roleRepository,
                            RoleType.TELLER,
                            "Teller",
                            "Bank branch teller"
                    )
            );

            roles.put(
                    RoleType.BRANCH_MANAGER,
                    createRole(
                            roleRepository,
                            RoleType.BRANCH_MANAGER,
                            "Branch Manager",
                            "Manages branch operations"
                    )
            );

            roles.put(
                    RoleType.BANK_ADMIN,
                    createRole(
                            roleRepository,
                            RoleType.BANK_ADMIN,
                            "Bank Administrator",
                            "Bank-level administrator"
                    )
            );

            roles.put(
                    RoleType.AUDITOR,
                    createRole(
                            roleRepository,
                            RoleType.AUDITOR,
                            "Auditor",
                            "Read-only audit user"
                    )
            );

            /*
             * 3. Permissions
             */
            Map<String, Permission> permissions = new HashMap<>();

            createPermission(
                    permissionRepository,
                    permissions,
                    "CUSTOMER_READ",
                    "Customer Read",
                    "View customer information"
            );

            createPermission(
                    permissionRepository,
                    permissions,
                    "CUSTOMER_CREATE",
                    "Customer Create",
                    "Create customer"
            );

            createPermission(
                    permissionRepository,
                    permissions,
                    "ACCOUNT_READ",
                    "Account Read",
                    "View account information"
            );

            createPermission(
                    permissionRepository,
                    permissions,
                    "ACCOUNT_CREATE",
                    "Account Create",
                    "Create bank account"
            );

            createPermission(
                    permissionRepository,
                    permissions,
                    "ACCOUNT_UPDATE",
                    "Account Update",
                    "Update account information"
            );

            createPermission(
                    permissionRepository,
                    permissions,
                    "TRANSACTION_READ",
                    "Transaction Read",
                    "View transaction information"
            );

            createPermission(
                    permissionRepository,
                    permissions,
                    "TRANSACTION_CREATE",
                    "Transaction Create",
                    "Create banking transaction"
            );

            createPermission(
                    permissionRepository,
                    permissions,
                    "CUSTOMER_UPDATE",
                    "Customer Update",
                    "Update customer information"
            );

            createPermission(
                    permissionRepository,
                    permissions,
                    "CUSTOMER_DEACTIVATE",
                    "Customer Deactivate",
                    "Deactivate customer"
            );

            createPermission(
                permissionRepository,
                permissions,
                "OUTBOX_REPLAY",
                "Outbox Replay",
                "Replay failed outbox events"
            );

            /*
             * 4. Role → Permission mappings
             */

            // CUSTOMER
            assignPermission(
                    rolePermissionRepository,
                    roles.get(RoleType.CUSTOMER),
                    permissions.get("ACCOUNT_READ")
            );

            assignPermission(
                    rolePermissionRepository,
                    roles.get(RoleType.CUSTOMER),
                    permissions.get("TRANSACTION_READ")
            );

            // TELLER
            assignPermission(
                    rolePermissionRepository,
                    roles.get(RoleType.TELLER),
                    permissions.get("CUSTOMER_READ")
            );

            assignPermission(
                    rolePermissionRepository,
                    roles.get(RoleType.TELLER),
                    permissions.get("ACCOUNT_READ")
            );

            assignPermission(
                    rolePermissionRepository,
                    roles.get(RoleType.TELLER),
                    permissions.get("ACCOUNT_CREATE")
            );

            // BRANCH MANAGER
            assignPermission(
                    rolePermissionRepository,
                    roles.get(RoleType.BRANCH_MANAGER),
                    permissions.get("CUSTOMER_READ")
            );

            assignPermission(
                    rolePermissionRepository,
                    roles.get(RoleType.BRANCH_MANAGER),
                    permissions.get("ACCOUNT_READ")
            );

            assignPermission(
                    rolePermissionRepository,
                    roles.get(RoleType.BRANCH_MANAGER),
                    permissions.get("ACCOUNT_CREATE")
            );

            assignPermission(
                    rolePermissionRepository,
                    roles.get(RoleType.BRANCH_MANAGER),
                    permissions.get("ACCOUNT_UPDATE")
            );

            assignPermission(
                    rolePermissionRepository,
                    roles.get(RoleType.BRANCH_MANAGER),
                    permissions.get("CUSTOMER_UPDATE")
            );

            assignPermission(
                    rolePermissionRepository,
                    roles.get(RoleType.BRANCH_MANAGER),
                    permissions.get("CUSTOMER_DEACTIVATE")
            );

            // BANK ADMIN
            assignPermission(
                    rolePermissionRepository,
                    roles.get(RoleType.BANK_ADMIN),
                    permissions.get("CUSTOMER_READ")
            );

            assignPermission(
                    rolePermissionRepository,
                    roles.get(RoleType.BANK_ADMIN),
                    permissions.get("CUSTOMER_CREATE")
            );

            assignPermission(
                    rolePermissionRepository,
                    roles.get(RoleType.BANK_ADMIN),
                    permissions.get("ACCOUNT_READ")
            );

            assignPermission(
                    rolePermissionRepository,
                    roles.get(RoleType.BANK_ADMIN),
                    permissions.get("ACCOUNT_CREATE")
            );

            assignPermission(
                    rolePermissionRepository,
                    roles.get(RoleType.BANK_ADMIN),
                    permissions.get("ACCOUNT_UPDATE")
            );

            assignPermission(
                    rolePermissionRepository,
                    roles.get(RoleType.BANK_ADMIN),
                    permissions.get("TRANSACTION_READ")
            );

            assignPermission(
                    rolePermissionRepository,
                    roles.get(RoleType.BANK_ADMIN),
                    permissions.get("TRANSACTION_CREATE")
            );

            assignPermission(
                    rolePermissionRepository,
                    roles.get(RoleType.BRANCH_MANAGER),
                    permissions.get("CUSTOMER_UPDATE")
            );

            assignPermission(
                    rolePermissionRepository,
                    roles.get(RoleType.BRANCH_MANAGER),
                    permissions.get("CUSTOMER_DEACTIVATE")
            );
            assignPermission(
                    rolePermissionRepository,
                    roles.get(RoleType.BANK_ADMIN),
                    permissions.get("OUTBOX_REPLAY")
            );

            // AUDITOR
            assignPermission(
                    rolePermissionRepository,
                    roles.get(RoleType.AUDITOR),
                    permissions.get("CUSTOMER_READ")
            );

            assignPermission(
                    rolePermissionRepository,
                    roles.get(RoleType.AUDITOR),
                    permissions.get("ACCOUNT_READ")
            );

            assignPermission(
                    rolePermissionRepository,
                    roles.get(RoleType.AUDITOR),
                    permissions.get("TRANSACTION_READ")
            );

            System.out.println("==========================================");
            System.out.println(" SmartBank Auth data initialization done");
            System.out.println(" Bank       : " + bank.getBankId());
            System.out.println(" Roles      : " + roles.size());
            System.out.println(" Permissions: " + permissions.size());
            System.out.println("==========================================");
        };
    }

    private Role createRole(
            RoleRepository repository,
            RoleType roleType,
            String roleName,
            String description
    ) {
        return repository
                .findByRoleCode(roleType)
                .orElseGet(() ->
                        repository.save(
                                new Role(
                                        roleType,
                                        roleName,
                                        description
                                )
                        )
                );
    }

    private void createPermission(
            PermissionRepository repository,
            Map<String, Permission> permissions,
            String code,
            String name,
            String description
    ) {
        Permission permission = repository
                .findByPermissionCode(code)
                .orElseGet(() ->
                        repository.save(
                                new Permission(
                                        code,
                                        name,
                                        description
                                )
                        )
                );

        permissions.put(code, permission);
    }

    private void assignPermission(
            RolePermissionRepository repository,
            Role role,
            Permission permission
    ) {
        if (!repository.existsByRoleIdAndPermissionId(
                role.getId(),
                permission.getId()
        )) {
            repository.save(
                    new RolePermission(
                            role.getId(),
                            permission.getId(),
                            "SYSTEM"
                    )
            );
        }
    }
}
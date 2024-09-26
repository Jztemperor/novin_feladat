package com.md.backend.configuration;

import com.md.backend.entity.ApplicationUser;
import com.md.backend.entity.Role;
import com.md.backend.enums.RoleNamesEnum;
import com.md.backend.repository.ApplicationUserRepository;
import com.md.backend.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
public class DataSeeder {

    private final RoleRepository roleRepository;
    private final ApplicationUserRepository applicationUserRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(RoleRepository roleRepository, ApplicationUserRepository applicationUserRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.applicationUserRepository = applicationUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    CommandLineRunner seed() {
        return args -> {
            HashSet<Role> authorities = new HashSet<>();

            // Seed roles
            if(roleRepository.findByAuthority(RoleNamesEnum.FELHASZNALO.getName()).isEmpty()) {
                Role felhasznaloRole = new Role();
                felhasznaloRole.setAuthority(RoleNamesEnum.FELHASZNALO.getName());
                felhasznaloRole.setDescription("Alap role");
                roleRepository.save(felhasznaloRole);
            }

            if(roleRepository.findByAuthority(RoleNamesEnum.KONYVELO.getName()).isEmpty()) {
                Role konyveloRole = new Role();
                konyveloRole.setAuthority(RoleNamesEnum.KONYVELO.getName());
                konyveloRole.setDescription("Konyvelo role");
                roleRepository.save(konyveloRole);
            }

            if(roleRepository.findByAuthority(RoleNamesEnum.ADMINISZTRATOR.getName()).isEmpty()) {
                Role administratorRole = new Role();
                administratorRole.setAuthority(RoleNamesEnum.ADMINISZTRATOR.getName());
                administratorRole.setDescription("Administrator role");
                roleRepository.save(administratorRole);
            }

            // Seed 1 user for each role
            if(!applicationUserRepository.existsByUsername("felhasznaloUser")) {
                Role felhasznaloRole = roleRepository.findByAuthority(RoleNamesEnum.FELHASZNALO.getName())
                        .orElseThrow(() -> new IllegalStateException("Role not found: " + RoleNamesEnum.FELHASZNALO.getName()));

                authorities.add(felhasznaloRole);
                ApplicationUser user = new ApplicationUser();
                user.setUsername("felhasznaloUser");
                user.setName("felhasznaloUser");
                user.setPassword(passwordEncoder.encode("pwd"));
                user.setAuthorities(authorities);

                applicationUserRepository.save(user);
                authorities.clear();
            }

            if(!applicationUserRepository.existsByUsername("konyveloUser")) {
                Role konyveloRole = roleRepository.findByAuthority(RoleNamesEnum.KONYVELO.getName())
                        .orElseThrow(() -> new IllegalStateException("Role not found: " + RoleNamesEnum.KONYVELO.getName()));

                authorities.add(konyveloRole);
                ApplicationUser user = new ApplicationUser();
                user.setUsername("konyveloUser");
                user.setName("konyveloUser");
                user.setPassword(passwordEncoder.encode("pwd"));
                user.setAuthorities(authorities);

                applicationUserRepository.save(user);
                authorities.clear();
            }

            if(!applicationUserRepository.existsByUsername("administratorUser")) {
                Role administratorRole = roleRepository.findByAuthority(RoleNamesEnum.ADMINISZTRATOR.getName())
                        .orElseThrow(() -> new IllegalStateException("Role not found: " + RoleNamesEnum.ADMINISZTRATOR.getName()));

                authorities.add(administratorRole);
                ApplicationUser user = new ApplicationUser();
                user.setUsername("administratorUser");
                user.setName("administratorUser");
                user.setAuthorities(authorities);
                user.setPassword(passwordEncoder.encode("pwd"));

                applicationUserRepository.save(user);
                authorities.clear();
            }
        };
    }
}

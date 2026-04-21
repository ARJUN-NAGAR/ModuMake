package com.modumake.config;

import com.modumake.model.Manufacturer;
import com.modumake.model.User;
import com.modumake.repository.ManufacturerRepository;
import com.modumake.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, ManufacturerRepository manufacturerRepository) {
        return args -> {
            Optional<User> sysUser = userRepository.findByEmail("admin@modumake.com");
            if (sysUser.isEmpty()) {
                User user = User.builder()
                        .name("System Admin")
                        .email("admin@modumake.com")
                        .passwordHash("{noop}password")
                        .role(User.Role.ADMIN)
                        .phoneNumber("+919999999999")
                        .build();
                userRepository.save(user);
            }

            if (manufacturerRepository.count() == 0) {
                Manufacturer economy = Manufacturer.builder()
                        .name("Tirupur Mega Textiles")
                        .email("contact@tirupurmega.com")
                        .gstin("33BBBBBBBBBBBB1Z1")
                        .tier(Manufacturer.Tier.ECONOMY)
                        .whatsappNumber("+919876543210")
                        .upiId("tirupur@upi")
                        .build();

                Manufacturer premium = Manufacturer.builder()
                        .name("Luxe Artisans Studio")
                        .email("hello@luxeartisans.in")
                        .gstin("27AAAAAAAAAAAA1Z1")
                        .tier(Manufacturer.Tier.PREMIUM)
                        .whatsappNumber("+918765432109")
                        .upiId("luxe@upi")
                        .build();

                manufacturerRepository.save(economy);
                manufacturerRepository.save(premium);
            }
        };
    }
}

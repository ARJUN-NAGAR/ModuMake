package com.modumake.controller;

import com.modumake.dto.TechPackDto;
import com.modumake.model.Design;
import com.modumake.model.Manufacturer;
import com.modumake.model.User;
import com.modumake.repository.DesignRepository;
import com.modumake.repository.ManufacturerRepository;
import com.modumake.repository.UserRepository;
import com.modumake.service.AiOrchestrationService;
import com.modumake.service.ImageToSpecsService;
import com.modumake.service.LiaisonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/designs")
@RequiredArgsConstructor
public class DesignController {

    private final AiOrchestrationService aiOrchestrationService;
    private final ImageToSpecsService imageToSpecsService;
    private final LiaisonService liaisonService;
    private final DesignRepository designRepository;
    private final UserRepository userRepository;
    private final ManufacturerRepository manufacturerRepository;

    @PostMapping("/generate")
    @Transactional
    public ResponseEntity<Design> generateDesign(@RequestBody Map<String, String> request) {
        try {
            String prompt = request.get("prompt");
            String category = request.get("category");
            String tierStr = request.get("tier");

            if (prompt == null || prompt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // 1. AI Orchestration
            String enhancedPrompt = aiOrchestrationService.enhancePromptForStableDiffusion(prompt);
            String imageUrl = aiOrchestrationService.generateImage(enhancedPrompt);

            // 2. Specs Generation
            TechPackDto techPack = imageToSpecsService.convertImageToSpecs(imageUrl, category);

            // 3. User Resolution
            User user = getAuthenticatedUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // 4. Persistence
            Design design = Design.builder()
                    .user(user)
                    .title("Generated: " + prompt)
                    .originalPrompt(prompt)
                    .imageUrl(imageUrl)
                    .techPack(techPack)
                    .status(Design.Status.PENDING_MANUFACTURER_MATCH)
                    .build();

            design = designRepository.save(design);

            // 5. Liaison Mapping
            if (tierStr != null && !tierStr.isEmpty()) {
                Manufacturer.Tier tier = Manufacturer.Tier.valueOf(tierStr.toUpperCase());
                List<Manufacturer> manufacturers = manufacturerRepository.findByTier(tier);
                if (!manufacturers.isEmpty()) {
                    Manufacturer match = manufacturers.get(0); // Take first matching for now
                    liaisonService.dispatchTechPack(techPack, match, imageUrl);
                }
            }

            return ResponseEntity.ok(design);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<String> getDesignsStatus() {
        return ResponseEntity.ok("Design API is running.");
    }

    private User getAuthenticatedUser() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof String email) {
            return userRepository.findByEmail(email).orElse(null);
        }
        // Fallback until Next.js strictly requires JWT
        return userRepository.findByEmail("admin@modumake.com").orElse(null);
    }
}

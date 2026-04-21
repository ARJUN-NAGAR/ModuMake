package com.modumake.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechPackDto {
    private String hsnCode;
    private String category; // e.g., "FASHION", "FURNITURE"
    private String fabricOrMaterialType;
    private Integer gsm; // Grams per square meter, if applicable
    private String stitchingType;
    
    // Using a map to easily support different dimensions (Length, Width, Height) and variations.
    // E.g., {"lengthInches": 40.0, "chestCm": 105.0}
    private Map<String, Double> dimensions;
    
    private String additionalInstructions;
}

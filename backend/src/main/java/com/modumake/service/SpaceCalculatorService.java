package com.modumake.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SpaceCalculatorService {

    /**
     * Calculates realistic Indian dimensions for standard furniture items.
     * Dimensions are returned in centimeters (L x W x H).
     */
    public Map<String, Double> calculateDimensions(String itemDescription) {
        String lowerDesc = itemDescription.toLowerCase();
        Map<String, Double> dimensions = new HashMap<>();

        if (lowerDesc.contains("3-seater sofa") || lowerDesc.contains("3 seater sofa")) {
            dimensions.put("lengthCm", 210.0);
            dimensions.put("widthCm", 90.0);
            dimensions.put("heightCm", 90.0);
        } else if (lowerDesc.contains("2-seater sofa") || lowerDesc.contains("2 seater sofa")) {
            dimensions.put("lengthCm", 150.0);
            dimensions.put("widthCm", 90.0);
            dimensions.put("heightCm", 90.0);
        } else if (lowerDesc.contains("coffee table")) {
            dimensions.put("lengthCm", 100.0);
            dimensions.put("widthCm", 60.0);
            dimensions.put("heightCm", 45.0);
        } else if (lowerDesc.contains("dining table")) {
            dimensions.put("lengthCm", 180.0);
            dimensions.put("widthCm", 90.0);
            dimensions.put("heightCm", 76.0);
        } else {
            // Default generic box scaling
            dimensions.put("lengthCm", 100.0);
            dimensions.put("widthCm", 100.0);
            dimensions.put("heightCm", 100.0);
        }

        return dimensions;
    }
}

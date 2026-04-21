package com.modumake.util;

import java.util.Map;

public class MaterialDefinitions {

    /**
     * Maps specific wood types to their respective HSN codes for Indian tax compliance.
     * 9403 corresponds to "Other furniture and parts thereof".
     */
    public static final Map<String, String> WOOD_TO_HSN = Map.of(
            "Teak", "9403",
            "Sheesham", "9403",
            "Engineered Wood", "9403"
    );

    /**
     * Additional material mapping for fabrics
     */
    public static final Map<String, String> FABRIC_TO_HSN = Map.of(
            "Cotton", "61",
            "Silk", "61",
            "Velvet", "61",
            "Jute", "62"
    );
}

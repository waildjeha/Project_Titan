package com.ken10.Phase2.GUI;

import java.util.Map;

/**
 * Class that defines zoom configuration settings for different viewing contexts.
 * Contains scale factors and relative body sizes for specific zoom scenarios.
*/
public class ZoomConfig {
    final double scaleX, scaleY, scaleZ;
    final Map<String, Double> bodySizes;
    
    /**
     * Constructor for ZoomConfig that sets up zoom parameters.
     * 
     * @param scale The uniform scale factor to apply to the view
     * @param sizes Map of body names to their relative sizes in this zoom context
    */
    ZoomConfig(double scale, Map<String, Double> sizes) {
        this.scaleX = this.scaleY = this.scaleZ = scale;
        this.bodySizes = sizes;
    }
}

package com.griddynamics.jagger.webclient.server;

import java.util.*;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/31/12
 */
public class ColorCodeGenerator {
    private static final List<String> hexCodes = new ArrayList<String>();
    private static final Random random = new Random(47L);

    static {
        hexCodes.addAll(Arrays.asList(
                "#000000",
                "#FF0000",
                "#800000",
                "#FFFF00",
                "#808000",
                "#00FF00",
                "#008000",
                "#00FFFF",
                "#008080",
                "#0000FF",
                "#000080",
                "#FF00FF",
                "#800080",
                "#D2691E"
        ));
    }

    protected ColorCodeGenerator() {
    }

    public static String getHexColorCode() {
        return hexCodes.get(random.nextInt(hexCodes.size()));
    }
}

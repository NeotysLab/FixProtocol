package com.neotys.FIX;

import javax.swing.*;
import java.net.URL;

public class FixUtils {
    private static final ImageIcon WHITEBLOCK_ICON;
    static {

        final URL iconURL = FixUtils.class.getResource("fix.png");
        if (iconURL != null) {
            WHITEBLOCK_ICON = new ImageIcon(iconURL);
        } else {
            WHITEBLOCK_ICON = null;
        }
    }

    public static ImageIcon getFixIcon() {
        return WHITEBLOCK_ICON;
    }

}

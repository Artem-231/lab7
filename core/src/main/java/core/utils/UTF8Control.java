// src/main/java/core/utils/UTF8Control.java
package core.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class UTF8Control extends ResourceBundle.Control {
    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format,
                                    ClassLoader loader, boolean reload)
            throws IOException {
        String bundleName = toBundleName(baseName, locale);
        String resourceName = toResourceName(bundleName, "properties");
        try (InputStream stream = loader.getResourceAsStream(resourceName)) {
            if (stream == null) return null;
            try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                return new PropertyResourceBundle(reader);
            }
        }
    }
}

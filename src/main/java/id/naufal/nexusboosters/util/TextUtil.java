package id.naufal.nexusboosters.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class TextUtil {
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    public static Component color(String text) {
        if (text == null) return Component.empty();
        Component component = LEGACY_SERIALIZER.deserialize(text);
        if (!component.hasDecoration(TextDecoration.ITALIC)) {
            component = component.decoration(TextDecoration.ITALIC, false);
        }
        return component;
    }

    public static String formatTime(long seconds) {
        long d = seconds / 86400;
        long h = (seconds % 86400) / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        
        StringBuilder sb = new StringBuilder();
        if (d > 0) sb.append(d).append("d ");
        if (h > 0) sb.append(h).append("h ");
        if (m > 0) sb.append(m).append("m ");
        sb.append(s).append("s");
        return sb.toString().trim();
    }

    public static int parseDuration(String durationStr) {
        if (durationStr == null || durationStr.isEmpty()) return -1;
        int totalSeconds = 0;
        int currentValue = 0;
        for (char c : durationStr.toCharArray()) {
            if (Character.isDigit(c)) {
                currentValue = currentValue * 10 + (c - '0');
            } else {
                switch (c) {
                    case 's': case 'S': totalSeconds += currentValue; break;
                    case 'm': case 'M': totalSeconds += currentValue * 60; break;
                    case 'h': case 'H': totalSeconds += currentValue * 3600; break;
                    case 'd': case 'D': totalSeconds += currentValue * 86400; break;
                    default: return -1;
                }
                currentValue = 0;
            }
        }
        if (currentValue > 0) return -1;
        return totalSeconds;
    }

    /**
     * Translates '&' color codes to '§' for legacy String-based APIs (e.g. Bukkit BossBar titles).
     * Does not use deprecated org.bukkit.ChatColor.
     */
    public static String colorLegacy(String text) {
        if (text == null || text.isEmpty()) return "";
        return text.replace('&', '\u00A7');
    }
}

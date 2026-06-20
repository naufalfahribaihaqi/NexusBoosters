package id.naufal.nexusboosters.booster;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class BoosterRegistry {
    private final Map<String, Booster> boosters = new HashMap<>();
    private final Logger logger;

    public BoosterRegistry(Logger logger) {
        this.logger = logger;
    }

    public void loadFromConfig(FileConfiguration config) {
        boosters.clear();
        ConfigurationSection section = config.getConfigurationSection("boosters");
        if (section == null) {
            logger.warning("No boosters found in boosters.yml");
            return;
        }

        for (String key : section.getKeys(false)) {
            try {
                String displayName = section.getString(key + ".display-name", key);
                BoosterType type = BoosterType.valueOf(section.getString(key + ".type", "CUSTOM").toUpperCase());
                BoosterScope scope = BoosterScope.valueOf(section.getString(key + ".scope", "PERSONAL").toUpperCase());
                double multiplier = section.getDouble(key + ".multiplier", 1.0);
                int durationSeconds = section.getInt(key + ".duration-seconds", 3600);
                String material = section.getString(key + ".material", "DIAMOND");
                String permission = section.getString(key + ".permission", null);
                java.util.List<String> requiresHooks = section.getStringList(key + ".requires-hooks");

                Booster booster = new Booster(key, displayName, type, scope, multiplier, durationSeconds, material, permission, requiresHooks);
                boosters.put(key, booster);
            } catch (Exception e) {
                logger.warning("Failed to load booster '" + key + "': " + e.getMessage());
            }
        }
        logger.info("Loaded " + boosters.size() + " boosters.");
    }

    public Booster getBooster(String id) {
        return boosters.get(id);
    }

    public Collection<Booster> getAllBoosters() {
        return Collections.unmodifiableCollection(boosters.values());
    }
}

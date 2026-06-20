package id.naufal.nexusboosters.booster;

import java.util.HashMap;
import java.util.Map;

public class LegacyIdMapper {

    private static final Map<String, LegacyMapping> LEGACY_MAP = new HashMap<>();

    static {
        LEGACY_MAP.put("personal_xp_2x", new LegacyMapping("xp", BoosterScope.PERSONAL, 2.0));
        LEGACY_MAP.put("global_xp_3x", new LegacyMapping("xp", BoosterScope.GLOBAL, 3.0));
        LEGACY_MAP.put("personal_points_reward_2x", new LegacyMapping("points_reward", BoosterScope.PERSONAL, 2.0));
        LEGACY_MAP.put("global_points_reward_2x", new LegacyMapping("points_reward", BoosterScope.GLOBAL, 2.0));
        LEGACY_MAP.put("personal_shop_sell_2x", new LegacyMapping("shop_sell", BoosterScope.PERSONAL, 2.0));
        LEGACY_MAP.put("global_shop_sell_2x", new LegacyMapping("shop_sell", BoosterScope.GLOBAL, 2.0));
        LEGACY_MAP.put("personal_mob_drops_2x", new LegacyMapping("mob_drops", BoosterScope.PERSONAL, 2.0));
        LEGACY_MAP.put("global_mob_drops_2x", new LegacyMapping("mob_drops", BoosterScope.GLOBAL, 2.0));
        LEGACY_MAP.put("personal_block_drops_2x", new LegacyMapping("block_drops", BoosterScope.PERSONAL, 2.0));
        LEGACY_MAP.put("global_block_drops_2x", new LegacyMapping("block_drops", BoosterScope.GLOBAL, 2.0));
    }

    public static boolean isLegacyId(String id) {
        return LEGACY_MAP.containsKey(id);
    }

    public static LegacyMapping getMapping(String id) {
        return LEGACY_MAP.get(id);
    }

    public static String resolveToNewId(String id) {
        LegacyMapping mapping = LEGACY_MAP.get(id);
        return mapping != null ? mapping.getNewId() : id;
    }

    public static class LegacyMapping {
        private final String newId;
        private final BoosterScope originalScope;
        private final double originalMultiplier;

        public LegacyMapping(String newId, BoosterScope originalScope, double originalMultiplier) {
            this.newId = newId;
            this.originalScope = originalScope;
            this.originalMultiplier = originalMultiplier;
        }

        public String getNewId() {
            return newId;
        }

        public BoosterScope getOriginalScope() {
            return originalScope;
        }

        public double getOriginalMultiplier() {
            return originalMultiplier;
        }
    }
}

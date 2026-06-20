package id.naufal.nexusboosters.booster;

public class Booster {
    private final String id;
    private final String displayName;
    private final BoosterType type;
    private final BoosterScope scope;
    private final double multiplier;
    private final int durationSeconds;
    private final String material;
    private final String permission;
    private final java.util.List<String> requiresHooks;

    public Booster(String id, String displayName, BoosterType type, BoosterScope scope, double multiplier, int durationSeconds, String material, String permission, java.util.List<String> requiresHooks) {
        this.id = id;
        this.displayName = displayName;
        this.type = type;
        this.scope = scope;
        this.multiplier = multiplier;
        this.durationSeconds = durationSeconds;
        this.material = material;
        this.permission = permission;
        this.requiresHooks = requiresHooks;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BoosterType getType() {
        return type;
    }

    public BoosterScope getScope() {
        return scope;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public String getMaterial() {
        return material;
    }

    public String getPermission() {
        return permission;
    }

    public java.util.List<String> getRequiresHooks() {
        return requiresHooks != null ? requiresHooks : new java.util.ArrayList<>();
    }
}

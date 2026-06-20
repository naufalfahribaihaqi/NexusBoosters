package id.naufal.nexusboosters.booster;

import java.util.UUID;

public class ActiveBooster {
    private final String boosterId;
    private final UUID ownerUuid; // Nullable for global/server scope
    private final BoosterScope scope;
    private final double multiplierOverride;
    private final long startedAt;
    private long expiresAt;
    private boolean paused;
    private int remainingSeconds;

    public ActiveBooster(String boosterId, UUID ownerUuid, BoosterScope scope, double multiplierOverride, long startedAt, long expiresAt) {
        this.boosterId = boosterId;
        this.ownerUuid = ownerUuid;
        this.scope = scope;
        this.multiplierOverride = multiplierOverride;
        this.startedAt = startedAt;
        this.expiresAt = expiresAt;
        this.paused = false;
        this.remainingSeconds = -1;
    }

    public String getBoosterId() {
        return boosterId;
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    public BoosterScope getScope() {
        return scope;
    }

    public double getMultiplierOverride() {
        return multiplierOverride;
    }

    public long getStartedAt() {
        return startedAt;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    public void setRemainingSeconds(int remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }

    public boolean isExpired() {
        if (paused) return false;
        return System.currentTimeMillis() >= expiresAt;
    }
}

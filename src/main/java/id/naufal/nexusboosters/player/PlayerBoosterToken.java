package id.naufal.nexusboosters.player;

import id.naufal.nexusboosters.booster.BoosterScope;

public class PlayerBoosterToken {
    private final String boosterId;
    private final BoosterScope scope;
    private final int durationOverrideSeconds;

    public PlayerBoosterToken(String boosterId, BoosterScope scope, int durationOverrideSeconds) {
        this.boosterId = boosterId;
        this.scope = scope;
        this.durationOverrideSeconds = durationOverrideSeconds;
    }

    public String getBoosterId() {
        return boosterId;
    }

    public BoosterScope getScope() {
        return scope;
    }

    public int getDurationOverrideSeconds() {
        return durationOverrideSeconds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerBoosterToken that = (PlayerBoosterToken) o;
        return durationOverrideSeconds == that.durationOverrideSeconds &&
                boosterId.equals(that.boosterId) &&
                scope == that.scope;
    }

    @Override
    public int hashCode() {
        int result = boosterId.hashCode();
        result = 31 * result + (scope != null ? scope.hashCode() : 0);
        result = 31 * result + durationOverrideSeconds;
        return result;
    }
}

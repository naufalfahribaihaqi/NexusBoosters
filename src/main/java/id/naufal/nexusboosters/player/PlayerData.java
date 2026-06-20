package id.naufal.nexusboosters.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {
    private final UUID uuid;
    private final Map<PlayerBoosterToken, Integer> boosterInventory = new HashMap<>();

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getBoosterAmount(PlayerBoosterToken token) {
        return boosterInventory.getOrDefault(token, 0);
    }

    public int getTotalBoosterAmount(String boosterId) {
        int total = 0;
        for (Map.Entry<PlayerBoosterToken, Integer> entry : boosterInventory.entrySet()) {
            if (entry.getKey().getBoosterId().equalsIgnoreCase(boosterId)) {
                total += entry.getValue();
            }
        }
        return total;
    }

    public void setBoosterAmount(PlayerBoosterToken token, int amount) {
        if (amount <= 0) {
            boosterInventory.remove(token);
        } else {
            boosterInventory.put(token, amount);
        }
    }

    public void addBooster(PlayerBoosterToken token, int amount) {
        setBoosterAmount(token, getBoosterAmount(token) + amount);
    }

    public boolean takeBooster(PlayerBoosterToken token, int amount) {
        int current = getBoosterAmount(token);
        if (current >= amount) {
            setBoosterAmount(token, current - amount);
            return true;
        }
        return false;
    }

    public PlayerBoosterToken findTokenById(String boosterId) {
        for (PlayerBoosterToken token : boosterInventory.keySet()) {
            if (token.getBoosterId().equalsIgnoreCase(boosterId) && boosterInventory.get(token) > 0) {
                return token;
            }
        }
        return null;
    }

    public Map<PlayerBoosterToken, Integer> getBoosterInventory() {
        return boosterInventory;
    }
}

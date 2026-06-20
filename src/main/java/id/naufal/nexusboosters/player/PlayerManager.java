package id.naufal.nexusboosters.player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {
    private final Map<UUID, PlayerData> players = new ConcurrentHashMap<>();

    public PlayerData getPlayerData(UUID uuid) {
        return players.computeIfAbsent(uuid, PlayerData::new);
    }

    public void unloadPlayerData(UUID uuid) {
        players.remove(uuid);
    }
    
    public void loadPlayerData(PlayerData data) {
        players.put(data.getUuid(), data);
    }
}

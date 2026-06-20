package id.naufal.nexusboosters.database;

import id.naufal.nexusboosters.booster.ActiveBooster;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface StorageService {
    CompletableFuture<Void> init();
    CompletableFuture<List<ActiveBooster>> loadActiveBoosters();
    CompletableFuture<Void> saveActiveBooster(ActiveBooster booster);
    CompletableFuture<Void> removeActiveBooster(String boosterId, UUID ownerUuid);
    
    CompletableFuture<id.naufal.nexusboosters.player.PlayerData> loadPlayerData(UUID uuid);
    CompletableFuture<Void> savePlayerData(id.naufal.nexusboosters.player.PlayerData data);
    
    CompletableFuture<Void> shutdown();
}

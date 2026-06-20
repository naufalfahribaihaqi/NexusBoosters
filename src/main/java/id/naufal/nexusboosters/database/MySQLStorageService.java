package id.naufal.nexusboosters.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import id.naufal.nexusboosters.NexusBoostersPlugin;
import id.naufal.nexusboosters.booster.ActiveBooster;
import id.naufal.nexusboosters.booster.BoosterScope;
import id.naufal.nexusboosters.player.PlayerBoosterToken;
import id.naufal.nexusboosters.player.PlayerData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class MySQLStorageService implements StorageService {

    private final NexusBoostersPlugin plugin;
    private HikariDataSource dataSource;

    public MySQLStorageService(NexusBoostersPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<Void> init() {
        return CompletableFuture.runAsync(() -> {
            HikariConfig config = new HikariConfig();
            String host = plugin.getConfig().getString("storage.mysql.host", "localhost");
            int port = plugin.getConfig().getInt("storage.mysql.port", 3306);
            String database = plugin.getConfig().getString("storage.mysql.database", "nexusboosters");
            boolean useSsl = plugin.getConfig().getBoolean("storage.mysql.use-ssl", false);

            config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + useSsl);
            config.setUsername(plugin.getConfig().getString("storage.mysql.username", "root"));
            config.setPassword(plugin.getConfig().getString("storage.mysql.password", ""));
            config.setMaximumPoolSize(plugin.getConfig().getInt("storage.mysql.pool-size", 10));
            config.setConnectionTimeout(10000);

            dataSource = new HikariDataSource(config);

            String createActiveBoostersTable = "CREATE TABLE IF NOT EXISTS nexus_active_boosters (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "booster_id VARCHAR(255) NOT NULL," +
                    "owner_uuid VARCHAR(36)," +
                    "scope VARCHAR(50) NOT NULL," +
                    "started_at BIGINT NOT NULL," +
                    "expires_at BIGINT NOT NULL," +
                    "active BOOLEAN NOT NULL DEFAULT TRUE," +
                    "server_id VARCHAR(255) NOT NULL," +
                    "updated_at BIGINT NOT NULL," +
                    "revision INT NOT NULL DEFAULT 1" +
                    ");";

            String createPlayerBoostersTable = "CREATE TABLE IF NOT EXISTS nexus_player_boosters (" +
                    "player_uuid VARCHAR(36) NOT NULL," +
                    "booster_id VARCHAR(255) NOT NULL," +
                    "scope VARCHAR(50) NOT NULL," +
                    "amount INT NOT NULL," +
                    "duration_override_seconds INT," +
                    "updated_at BIGINT NOT NULL," +
                    "revision INT NOT NULL DEFAULT 1," +
                    "PRIMARY KEY(player_uuid, booster_id, scope, duration_override_seconds)" +
                    ");";

            try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
                stmt.execute(createActiveBoostersTable);
                stmt.execute(createPlayerBoostersTable);
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to initialize MySQL database", e);
            }
        });
    }

    @Override
    public CompletableFuture<List<ActiveBooster>> loadActiveBoosters() {
        return CompletableFuture.supplyAsync(() -> {
            List<ActiveBooster> boosters = new ArrayList<>();
            String query = "SELECT * FROM nexus_active_boosters WHERE active = TRUE";

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    String boosterId = rs.getString("booster_id");
                    String ownerUuidStr = rs.getString("owner_uuid");
                    UUID ownerUuid = (ownerUuidStr != null && !ownerUuidStr.isEmpty()) ? UUID.fromString(ownerUuidStr) : null;
                    BoosterScope scope = BoosterScope.valueOf(rs.getString("scope"));
                    long startedAt = rs.getLong("started_at");
                    long expiresAt = rs.getLong("expires_at");

                    ActiveBooster booster = new ActiveBooster(boosterId, ownerUuid, scope, startedAt, expiresAt);
                    booster.setPaused(!rs.getBoolean("active"));
                    long rem = (expiresAt - System.currentTimeMillis()) / 1000;
                    booster.setRemainingSeconds(rem > 0 ? (int) rem : 0);

                    boosters.add(booster);
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to load active boosters from MySQL", e);
            }

            return boosters;
        });
    }

    @Override
    public CompletableFuture<Void> saveActiveBooster(ActiveBooster booster) {
        return CompletableFuture.runAsync(() -> {
            String insertQuery = "INSERT INTO nexus_active_boosters (booster_id, owner_uuid, scope, started_at, expires_at, active, server_id, updated_at, revision) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            String disableQuery = "UPDATE nexus_active_boosters SET active = FALSE, updated_at = ? WHERE booster_id = ? AND scope = ? AND (owner_uuid = ? OR (owner_uuid IS NULL AND ? IS NULL)) AND active = TRUE";

            try (Connection conn = dataSource.getConnection()) {
                conn.setAutoCommit(false);
                try (PreparedStatement disableStmt = conn.prepareStatement(disableQuery)) {
                    disableStmt.setLong(1, System.currentTimeMillis());
                    disableStmt.setString(2, booster.getBoosterId());
                    disableStmt.setString(3, booster.getScope().name());
                    if (booster.getOwnerUuid() != null) {
                        disableStmt.setString(4, booster.getOwnerUuid().toString());
                        disableStmt.setString(5, booster.getOwnerUuid().toString());
                    } else {
                        disableStmt.setNull(4, java.sql.Types.VARCHAR);
                        disableStmt.setNull(5, java.sql.Types.VARCHAR);
                    }
                    disableStmt.executeUpdate();
                }

                if (!booster.isPaused() && booster.getRemainingSeconds() > 0) {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        insertStmt.setString(1, booster.getBoosterId());
                        if (booster.getOwnerUuid() != null) {
                            insertStmt.setString(2, booster.getOwnerUuid().toString());
                        } else {
                            insertStmt.setNull(2, java.sql.Types.VARCHAR);
                        }
                        insertStmt.setString(3, booster.getScope().name());
                        insertStmt.setLong(4, booster.getStartedAt());
                        insertStmt.setLong(5, booster.getExpiresAt());
                        insertStmt.setBoolean(6, true);
                        insertStmt.setString(7, plugin.getConfig().getString("cross-server.server-id", "local"));
                        insertStmt.setLong(8, System.currentTimeMillis());
                        insertStmt.setInt(9, 1);
                        insertStmt.executeUpdate();
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to save active booster", e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> removeActiveBooster(String boosterId, UUID ownerUuid) {
        return CompletableFuture.runAsync(() -> {
            String updateQuery = "UPDATE nexus_active_boosters SET active = FALSE, updated_at = ? WHERE booster_id = ? AND (owner_uuid = ? OR (owner_uuid IS NULL AND ? IS NULL))";

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

                pstmt.setLong(1, System.currentTimeMillis());
                pstmt.setString(2, boosterId);
                if (ownerUuid != null) {
                    pstmt.setString(3, ownerUuid.toString());
                    pstmt.setString(4, ownerUuid.toString());
                } else {
                    pstmt.setNull(3, java.sql.Types.VARCHAR);
                    pstmt.setNull(4, java.sql.Types.VARCHAR);
                }

                pstmt.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to remove active booster", e);
            }
        });
    }

    @Override
    public CompletableFuture<PlayerData> loadPlayerData(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            PlayerData data = new PlayerData(uuid);
            String query = "SELECT booster_id, scope, amount, duration_override_seconds FROM nexus_player_boosters WHERE player_uuid = ?";

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, uuid.toString());
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String boosterId = rs.getString("booster_id");
                        BoosterScope scope = BoosterScope.valueOf(rs.getString("scope"));
                        int durationOverride = rs.getInt("duration_override_seconds");
                        int amount = rs.getInt("amount");

                        PlayerBoosterToken token = new PlayerBoosterToken(boosterId, scope, durationOverride);
                        data.setBoosterAmount(token, amount);
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to load player data", e);
            }
            return data;
        });
    }

    @Override
    public CompletableFuture<Void> savePlayerData(PlayerData data) {
        return CompletableFuture.runAsync(() -> {
            String upsertQuery = "INSERT INTO nexus_player_boosters (player_uuid, booster_id, scope, amount, duration_override_seconds, updated_at, revision) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE amount = VALUES(amount), updated_at = VALUES(updated_at), revision = revision + 1";

            try (Connection conn = dataSource.getConnection()) {
                conn.setAutoCommit(false);
                try (PreparedStatement delStmt = conn.prepareStatement("DELETE FROM nexus_player_boosters WHERE player_uuid = ?")) {
                    delStmt.setString(1, data.getUuid().toString());
                    delStmt.executeUpdate();
                }

                try (PreparedStatement pstmt = conn.prepareStatement(upsertQuery)) {
                    for (Map.Entry<PlayerBoosterToken, Integer> entry : data.getBoosterInventory().entrySet()) {
                        pstmt.setString(1, data.getUuid().toString());
                        pstmt.setString(2, entry.getKey().getBoosterId());
                        pstmt.setString(3, entry.getKey().getScope().name());
                        pstmt.setInt(4, entry.getValue());
                        pstmt.setInt(5, entry.getKey().getDurationOverrideSeconds());
                        pstmt.setLong(6, System.currentTimeMillis());
                        pstmt.setInt(7, 1);
                        pstmt.addBatch();
                    }
                    pstmt.executeBatch();
                }
                conn.commit();
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to save player data", e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> shutdown() {
        return CompletableFuture.runAsync(() -> {
            if (dataSource != null && !dataSource.isClosed()) {
                dataSource.close();
            }
        });
    }
}

package id.naufal.nexusboosters.database;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import id.naufal.nexusboosters.booster.ActiveBooster;
import id.naufal.nexusboosters.booster.BoosterScope;
import id.naufal.nexusboosters.player.PlayerBoosterToken;
import id.naufal.nexusboosters.player.PlayerData;
import id.naufal.nexusboosters.booster.LegacyIdMapper;
import id.naufal.nexusboosters.booster.LegacyIdMapper.LegacyMapping;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class SQLiteStorageService implements StorageService {

    private final NexusBoostersPlugin plugin;
    private final String url;

    public SQLiteStorageService(NexusBoostersPlugin plugin) {
        this.plugin = plugin;
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        File databaseFile = new File(dataFolder, "database.db");
        this.url = "jdbc:sqlite:" + databaseFile.getAbsolutePath();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    @Override
    public CompletableFuture<Void> init() {
        return CompletableFuture.runAsync(() -> {
            String createActiveBoostersTable = "CREATE TABLE IF NOT EXISTS nexus_active_boosters (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "booster_id VARCHAR(255) NOT NULL," +
                    "owner_uuid VARCHAR(36)," +
                    "scope VARCHAR(50) NOT NULL," +
                    "started_at BIGINT NOT NULL," +
                    "expires_at BIGINT NOT NULL," +
                    "active BOOLEAN NOT NULL DEFAULT 1," +
                    "server_id VARCHAR(255) NOT NULL," +
                    "multiplier_override DOUBLE DEFAULT -1.0," +
                    "updated_at BIGINT NOT NULL," +
                    "revision INTEGER NOT NULL DEFAULT 1" +
                    ");";

            String createPlayerBoostersTable = "CREATE TABLE IF NOT EXISTS nexus_player_boosters (" +
                    "player_uuid VARCHAR(36) NOT NULL," +
                    "booster_id VARCHAR(255) NOT NULL," +
                    "scope VARCHAR(50) NOT NULL," +
                    "amount INTEGER NOT NULL," +
                    "duration_override_seconds INTEGER," +
                    "multiplier_override DOUBLE DEFAULT -1.0," +
                    "updated_at BIGINT NOT NULL," +
                    "revision INTEGER NOT NULL DEFAULT 1," +
                    "PRIMARY KEY(player_uuid, booster_id, scope, duration_override_seconds, multiplier_override)" +
                    ");";

            try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
                stmt.execute(createActiveBoostersTable);
                stmt.execute(createPlayerBoostersTable);
                migrateOldData(conn);
                migrateV2Data(conn);
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to initialize SQLite database", e);
            }
        });
    }

    private void migrateOldData(Connection conn) {
        try {
            boolean oldPlayerBoostersExists = false;
            try (ResultSet rs = conn.getMetaData().getTables(null, null, "player_boosters", null)) {
                if (rs.next()) oldPlayerBoostersExists = true;
            }
            if (oldPlayerBoostersExists) {
                plugin.getLogger().info("Found old player_boosters table. Migrating data...");
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT uuid, booster_id, amount FROM player_boosters")) {
                    String insert = "INSERT OR IGNORE INTO nexus_player_boosters (player_uuid, booster_id, scope, amount, duration_override_seconds, updated_at, revision) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(insert)) {
                        while (rs.next()) {
                            pstmt.setString(1, rs.getString("uuid"));
                            pstmt.setString(2, rs.getString("booster_id"));
                            pstmt.setString(3, "PERSONAL"); // Defaulting old un-scoped boosters to PERSONAL
                            pstmt.setInt(4, rs.getInt("amount"));
                            pstmt.setInt(5, -1); // -1 signifies no override (use default)
                            pstmt.setLong(6, System.currentTimeMillis());
                            pstmt.setInt(7, 1);
                            pstmt.addBatch();
                        }
                        pstmt.executeBatch();
                    }
                }
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("DROP TABLE player_boosters");
                    plugin.getLogger().info("Old player_boosters table dropped after migration.");
                }
            }

            boolean oldActiveBoostersExists = false;
            try (ResultSet rs = conn.getMetaData().getTables(null, null, "active_boosters", null)) {
                if (rs.next()) oldActiveBoostersExists = true;
            }
            if (oldActiveBoostersExists) {
                plugin.getLogger().info("Found old active_boosters table. Migrating data...");
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT * FROM active_boosters")) {
                    String insert = "INSERT INTO nexus_active_boosters (booster_id, owner_uuid, scope, started_at, expires_at, active, server_id, updated_at, revision) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(insert)) {
                        while (rs.next()) {
                            pstmt.setString(1, rs.getString("booster_id"));
                            pstmt.setString(2, rs.getString("owner_uuid"));
                            pstmt.setString(3, rs.getString("scope"));
                            pstmt.setLong(4, rs.getLong("started_at"));
                            pstmt.setLong(5, rs.getLong("expires_at"));
                            pstmt.setBoolean(6, !rs.getBoolean("paused"));
                            pstmt.setString(7, "local");
                            pstmt.setLong(8, System.currentTimeMillis());
                            pstmt.setInt(9, 1);
                            pstmt.addBatch();
                        }
                        pstmt.executeBatch();
                    }
                }
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("DROP TABLE active_boosters");
                    plugin.getLogger().info("Old active_boosters table dropped after migration.");
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error during SQLite migration", e);
        }
    }

    private void migrateV2Data(Connection conn) {
        try {
            boolean hasMultiplier = false;
            try (ResultSet rs = conn.getMetaData().getColumns(null, null, "nexus_player_boosters", "multiplier_override")) {
                if (rs.next()) hasMultiplier = true;
            }
            if (!hasMultiplier) {
                plugin.getLogger().info("Migrating SQLite to V2 (adding multiplier_override and mapping legacy IDs)...");

                File dbFile = new File(plugin.getDataFolder(), "database.db");
                java.nio.file.Files.copy(dbFile.toPath(), new File(plugin.getDataFolder(), "database.db.backup").toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("ALTER TABLE nexus_active_boosters ADD COLUMN multiplier_override DOUBLE DEFAULT -1.0");
                    
                    try (ResultSet rs = stmt.executeQuery("SELECT id, booster_id FROM nexus_active_boosters")) {
                        String update = "UPDATE nexus_active_boosters SET booster_id = ?, multiplier_override = ?, scope = ? WHERE id = ?";
                        try (PreparedStatement pstmt = conn.prepareStatement(update)) {
                            while (rs.next()) {
                                int rowId = rs.getInt("id");
                                String boosterId = rs.getString("booster_id");
                                if (LegacyIdMapper.isLegacyId(boosterId)) {
                                    LegacyMapping mapping = LegacyIdMapper.getMapping(boosterId);
                                    pstmt.setString(1, mapping.getNewId());
                                    pstmt.setDouble(2, mapping.getOriginalMultiplier());
                                    pstmt.setString(3, mapping.getOriginalScope().name());
                                    pstmt.setInt(4, rowId);
                                    pstmt.addBatch();
                                    plugin.getLogger().info("Migrating active booster " + boosterId + " -> " + mapping.getNewId());
                                }
                            }
                            pstmt.executeBatch();
                        }
                    }

                    stmt.execute("ALTER TABLE nexus_player_boosters RENAME TO nexus_player_boosters_old");
                    
                    String createPlayerBoostersTable = "CREATE TABLE IF NOT EXISTS nexus_player_boosters (" +
                            "player_uuid VARCHAR(36) NOT NULL," +
                            "booster_id VARCHAR(255) NOT NULL," +
                            "scope VARCHAR(50) NOT NULL," +
                            "amount INTEGER NOT NULL," +
                            "duration_override_seconds INTEGER," +
                            "multiplier_override DOUBLE DEFAULT -1.0," +
                            "updated_at BIGINT NOT NULL," +
                            "revision INTEGER NOT NULL DEFAULT 1," +
                            "PRIMARY KEY(player_uuid, booster_id, scope, duration_override_seconds, multiplier_override)" +
                            ");";
                    stmt.execute(createPlayerBoostersTable);
                    
                    try (ResultSet rs = stmt.executeQuery("SELECT * FROM nexus_player_boosters_old")) {
                        String insert = "INSERT OR IGNORE INTO nexus_player_boosters (player_uuid, booster_id, scope, amount, duration_override_seconds, multiplier_override, updated_at, revision) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                        try (PreparedStatement pstmt = conn.prepareStatement(insert)) {
                            while (rs.next()) {
                                String boosterId = rs.getString("booster_id");
                                String scopeStr = rs.getString("scope");
                                double multiplierOverride = -1.0;
                                
                                if (LegacyIdMapper.isLegacyId(boosterId)) {
                                    LegacyMapping mapping = LegacyIdMapper.getMapping(boosterId);
                                    boosterId = mapping.getNewId();
                                    scopeStr = mapping.getOriginalScope().name();
                                    multiplierOverride = mapping.getOriginalMultiplier();
                                    plugin.getLogger().info("Migrating player booster token " + rs.getString("booster_id") + " -> " + boosterId);
                                }
                                
                                pstmt.setString(1, rs.getString("player_uuid"));
                                pstmt.setString(2, boosterId);
                                pstmt.setString(3, scopeStr);
                                pstmt.setInt(4, rs.getInt("amount"));
                                pstmt.setInt(5, rs.getInt("duration_override_seconds"));
                                pstmt.setDouble(6, multiplierOverride);
                                pstmt.setLong(7, rs.getLong("updated_at"));
                                pstmt.setInt(8, rs.getInt("revision"));
                                pstmt.addBatch();
                            }
                            pstmt.executeBatch();
                        }
                    }
                    stmt.execute("DROP TABLE nexus_player_boosters_old");
                    plugin.getLogger().info("SQLite V2 migration completed successfully.");
                }
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error during SQLite V2 migration", e);
        }
    }

    @Override
    public CompletableFuture<List<ActiveBooster>> loadActiveBoosters() {
        return CompletableFuture.supplyAsync(() -> {
            List<ActiveBooster> boosters = new ArrayList<>();
            String query = "SELECT * FROM nexus_active_boosters WHERE active = 1";

            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    String boosterId = rs.getString("booster_id");
                    String ownerUuidStr = rs.getString("owner_uuid");
                    UUID ownerUuid = (ownerUuidStr != null && !ownerUuidStr.isEmpty()) ? UUID.fromString(ownerUuidStr) : null;
                    BoosterScope scope = BoosterScope.valueOf(rs.getString("scope"));
                    long startedAt = rs.getLong("started_at");
                    long expiresAt = rs.getLong("expires_at");
                    double multiplierOverride = rs.getDouble("multiplier_override");

                    ActiveBooster booster = new ActiveBooster(boosterId, ownerUuid, scope, multiplierOverride, startedAt, expiresAt);
                    booster.setPaused(!rs.getBoolean("active"));
                    long rem = (expiresAt - System.currentTimeMillis()) / 1000;
                    booster.setRemainingSeconds(rem > 0 ? (int) rem : 0);

                    boosters.add(booster);
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to load active boosters from SQLite", e);
            }

            return boosters;
        });
    }

    @Override
    public CompletableFuture<Void> saveActiveBooster(ActiveBooster booster) {
        return CompletableFuture.runAsync(() -> {
            String insertQuery = "INSERT INTO nexus_active_boosters (booster_id, owner_uuid, scope, started_at, expires_at, active, server_id, multiplier_override, updated_at, revision) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            String disableQuery = "UPDATE nexus_active_boosters SET active = 0, updated_at = ? WHERE booster_id = ? AND scope = ? AND (owner_uuid = ? OR (owner_uuid IS NULL AND ? IS NULL)) AND active = 1";

            try (Connection conn = getConnection()) {
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
                        insertStmt.setDouble(8, booster.getMultiplierOverride());
                        insertStmt.setLong(9, System.currentTimeMillis());
                        insertStmt.setInt(10, 1);
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
            String updateQuery = "UPDATE nexus_active_boosters SET active = 0, updated_at = ? WHERE booster_id = ? AND (owner_uuid = ? OR (owner_uuid IS NULL AND ? IS NULL))";

            try (Connection conn = getConnection();
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
            String query = "SELECT booster_id, scope, amount, duration_override_seconds, multiplier_override FROM nexus_player_boosters WHERE player_uuid = ?";
            
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                
                pstmt.setString(1, uuid.toString());
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String boosterId = rs.getString("booster_id");
                        BoosterScope scope = BoosterScope.valueOf(rs.getString("scope"));
                        int durationOverride = rs.getInt("duration_override_seconds");
                        double multiplierOverride = rs.getDouble("multiplier_override");
                        int amount = rs.getInt("amount");
                        
                        PlayerBoosterToken token = new PlayerBoosterToken(boosterId, scope, durationOverride, multiplierOverride);
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
            String replaceQuery = "REPLACE INTO nexus_player_boosters (player_uuid, booster_id, scope, amount, duration_override_seconds, multiplier_override, updated_at, revision) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (Connection conn = getConnection()) {
                conn.setAutoCommit(false);
                // First delete empty amounts
                try (PreparedStatement delStmt = conn.prepareStatement("DELETE FROM nexus_player_boosters WHERE player_uuid = ?")) {
                    delStmt.setString(1, data.getUuid().toString());
                    delStmt.executeUpdate();
                }
                
                try (PreparedStatement pstmt = conn.prepareStatement(replaceQuery)) {
                    for (Map.Entry<PlayerBoosterToken, Integer> entry : data.getBoosterInventory().entrySet()) {
                        pstmt.setString(1, data.getUuid().toString());
                        pstmt.setString(2, entry.getKey().getBoosterId());
                        pstmt.setString(3, entry.getKey().getScope().name());
                        pstmt.setInt(4, entry.getValue());
                        pstmt.setInt(5, entry.getKey().getDurationOverrideSeconds());
                        pstmt.setDouble(6, entry.getKey().getMultiplierOverride());
                        pstmt.setLong(7, System.currentTimeMillis());
                        pstmt.setInt(8, 1);
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
        return CompletableFuture.completedFuture(null);
    }
}

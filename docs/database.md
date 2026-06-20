# Database

NexusBoosters supports two database storage engines: **SQLite** (local) and **MySQL** (network).

## SQLite
By default, the plugin uses SQLite. This is perfect for single servers.
Data is stored locally in `plugins/NexusBoosters/database.db`.

```yaml
storage:
  type: sqlite
```

## MySQL
If you run a network of servers (e.g., BungeeCord/Velocity) and want players' booster tokens or active global boosters to synchronize across servers, you must use MySQL.

### Connection Configuration
Edit your `config.yml` to connect to your MySQL server:

```yaml
storage:
  type: mysql
  mysql:
    host: localhost
    port: 3306
    database: nexusboosters
    username: root
    password: "your_secure_password"
```

> [!WARNING]
> Never share your `config.yml` if it contains your MySQL credentials. Keep your passwords secure.

## Data Stored
The database handles:
1. **Player Data**: The amount of booster tokens a player owns.
2. **Active Boosters**: The currently running boosters, their scope, owner, and expiration times.

## Backups
We highly recommend setting up automated backups for your database. If you use SQLite, simply back up the `database.db` file. If using MySQL, use standard `mysqldump` procedures.

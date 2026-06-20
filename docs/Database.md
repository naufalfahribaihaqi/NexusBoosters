# Database

Choose the best storage engine for your environment.

## Overview

NexusBoosters requires a database. It defaults to SQLite, which requires zero setup.

## SQLite

Ideal for single servers (e.g., standalone Survival). It stores data locally in `plugins/NexusBoosters/database.db`.

## MySQL

Required for networks or Cross-Server synchronization. Allows multiple instances to query the same tables.

## Configuration Example

```yaml
storage:
  type: mysql
  mysql:
    host: localhost
    port: 3306
    database: nexusboosters
    username: root
    password: "secure_password"
```

## Backup

* **SQLite**: Copy the `database.db` file securely.
* **MySQL**: Use standard `mysqldump` automated tools.

---

[← Previous](Messages) | [Home](Home) | [Next →](Cross-Server-Setup)

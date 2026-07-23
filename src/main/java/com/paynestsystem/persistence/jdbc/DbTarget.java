package com.paynestsystem.persistence.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classroom demo targets for the product CRUD Swing UI.
 * H2 matches {@code PayNestApplication}; Postgres matches {@code docker-compose.yml}.
 */
public enum DbTarget {

    H2(
            "jdbc:h2:file:./data/paynest;AUTO_SERVER=TRUE;TRACE_LEVEL_FILE=3",
            null,
            null,
            """
                    CREATE TABLE IF NOT EXISTS products (
                      id    INT PRIMARY KEY,
                      name  VARCHAR(100) NOT NULL,
                      price DOUBLE NOT NULL
                    )
                    """
    ),
    POSTGRES(
            "jdbc:postgresql://localhost:5432/paynest",
            "paynest",
            "paynest",
            """
                    CREATE TABLE IF NOT EXISTS products (
                      id    INT PRIMARY KEY,
                      name  VARCHAR(100) NOT NULL,
                      price DOUBLE PRECISION NOT NULL
                    )
                    """
    );

    private final String jdbcUrl;
    private final String user;
    private final String password;
    private final String createProductsSql;

    DbTarget(String jdbcUrl, String user, String password, String createProductsSql) {
        this.jdbcUrl = jdbcUrl;
        this.user = user;
        this.password = password;
        this.createProductsSql = createProductsSql;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getCreateProductsSql() {
        return createProductsSql;
    }

    /**
     * Opens a JDBC connection for this target.
     * H2 uses the URL only; Postgres uses user/password from Docker Compose.
     */
    public Connection openConnection() throws SQLException {
        if (user == null) {
            return DriverManager.getConnection(jdbcUrl);
        }
        return DriverManager.getConnection(jdbcUrl, user, password);
    }

    @Override
    public String toString() {
        return name();
    }
}

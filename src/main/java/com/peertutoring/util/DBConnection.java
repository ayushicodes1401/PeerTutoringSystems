
package com.peertutoring.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DBConnection {

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        Properties properties = new Properties();
        Path configPath = Path.of("config", "db.properties");

        try (InputStream input = Files.newInputStream(configPath)) {
            properties.load(input);
        } catch (IOException exception) {
            throw new SQLException(
                    "Unable to read config/db.properties. "
                    + "Check that the file exists in the project root.",
                    exception
            );
        }

        String url = properties.getProperty("db.url");
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");

        if (url == null || username == null || password == null
                || url.isBlank() || username.isBlank()) {
            throw new SQLException(
                    "Database URL, username, or password is missing."
            );
        }

        return DriverManager.getConnection(url, username, password);
    }
}

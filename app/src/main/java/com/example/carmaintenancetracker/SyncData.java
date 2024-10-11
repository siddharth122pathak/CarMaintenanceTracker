package com.example.carmaintenancetracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.sql.*;

public class SyncData {

    private static final String MYSQL_URL = "jdbc:mysql://sql5.freemysqlhosting.net:3306/sql5733810";
    private static final String MYSQL_USER = "sql5733810";
    private static final String MYSQL_PASSWORD = "yGxUyFjYUx";

    public static void syncData(Context context) {
        SQLiteDatabase sqliteDb = null;
        Connection mysqlConn = null;

        try {
            // Open SQLite database
            String sqliteDbPath = context.getDatabasePath("vehicle.db").getAbsolutePath();
            Log.d("DatabasePath", "SQLite Path: " + sqliteDbPath);

            if (!context.getDatabasePath("vehicle.db").exists()) {
                Log.e("DatabaseError", "SQLite database file not found.");
                return;
            }

            sqliteDb = SQLiteDatabase.openDatabase(sqliteDbPath, null, SQLiteDatabase.OPEN_READWRITE);

            // Connect to MySQL
            mysqlConn = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);

            // Perform synchronization
            syncRecords(sqliteDb, mysqlConn);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (sqliteDb != null) sqliteDb.close();
            try {
                if (mysqlConn != null) mysqlConn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @SuppressLint("Range")
    private static void syncRecords(SQLiteDatabase sqliteDb, Connection mysqlConn) throws SQLException {
        // Select unsynced records from SQLite
        Cursor cursor = sqliteDb.rawQuery("SELECT id, make, model, year, nickname FROM vehicles WHERE is_synced = 0", null);

        // Prepare statement to insert into MySQL
        PreparedStatement ps = mysqlConn.prepareStatement(
                "INSERT INTO sql5733810.car_info (id, car_id, make, model, year, nickname) VALUES (?, ?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE make = VALUES(make), model = VALUES(model), year = VALUES(year), " +
                        "nickname = VALUES(nickname)");

        while (cursor.moveToNext()) {
            ps.setInt(1, cursor.getInt(cursor.getColumnIndex("id")));
            ps.setInt(2, cursor.getInt(cursor.getColumnIndex("id")));
            ps.setString(3, cursor.getString(cursor.getColumnIndex("make")));
            ps.setString(4, cursor.getString(cursor.getColumnIndex("model")));
            ps.setString(5, cursor.getString(cursor.getColumnIndex("year")));
            ps.setString(6, cursor.getString(cursor.getColumnIndex("nickname")));
            ps.executeUpdate();
        }

        cursor.close();
        ps.close();
    }
}

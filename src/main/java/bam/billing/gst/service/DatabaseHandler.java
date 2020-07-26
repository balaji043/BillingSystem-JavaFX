package bam.billing.gst.service;

import bam.billing.gst.alert.AlertMaker;

import java.sql.Connection;
import java.sql.DriverManager;


class DatabaseHandler {

    private static DatabaseHandler handler = null;
    private static Connection conn = null;

    static {
        createConnection();
    }

    private DatabaseHandler() {
    }

    static DatabaseHandler getInstance() {
        if (handler == null) {
            handler = new DatabaseHandler();
        }
        return handler;
    }

    private static void createConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            String s = "tiger";
            conn = DriverManager.getConnection("jdbc:sqlite:GSTApp.sqlite"
                    , "scott"
                    , s);
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
    }

    Connection getConnection() {
        return conn;
    }
}

package sample.Database;

import sample.Alert.AlertMaker;

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

    private static void createConnection()  {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:billing.sqlite"
                    ,"scott"
                    ,"tiger");
        }catch (Exception e){
            AlertMaker.showErrorMessage(e);
        }
    }

    Connection getConnection() {
        return conn;
    }
}

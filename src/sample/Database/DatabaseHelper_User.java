package sample.Database;

import com.sun.istack.internal.NotNull;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Alert.AlertMaker;
import sample.Model.User;
import sample.Utils.Preferences;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseHelper_User extends DatabaseHelper {
    static boolean createUserTable() {
        String create = "CREATE TABLE IF NOT EXISTS Employee (Name TEXT NOT NULL" +
                ",id TEXT NOT NULL UNIQUE,password TEXT NOT NULL" +
                ",access TEXT NOT NULL,UserName TEXT NOT NULL)";
        return DatabaseHelper.createTable(create);
    }

    public static boolean insertNewUser(@NotNull User user) {
        PreparedStatement preparedStatement = null;
        boolean okay = false;
        try {
            String query = "insert or ignore into employee ( name " +
                    ", id , password , access , username ) " +
                    " values ( ? , ? , ? , ? , ? ) ";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getId());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setString(4, user.getAccess());
            preparedStatement.setString(5, user.getUserName());
            okay = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return okay;
    }

    public static boolean updateUser(@NotNull User user) {
        PreparedStatement preparedStatement = null;
        boolean okay = false;

        try {
            String query = "update employee set name = ?,password = ?,access =" +
                    " ?,username = ? where id = ?";

            preparedStatement = DatabaseHandler.getInstance()
                    .getConnection().prepareStatement(query);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getAccess());
            preparedStatement.setString(4, user.getUserName());
            preparedStatement.setString(5, user.getId());
            okay = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return okay;
    }

    public static User getUserInfo(@NotNull String userName) {
        User u = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            String query = "select * from employee where username = ?";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, userName);
            resultSet = preparedStatement.executeQuery();
            u = new User(resultSet.getString("name")
                    , resultSet.getString("username")
                    , resultSet.getString("id")
                    , resultSet.getString("password")
                    , resultSet.getString("access"));
        } catch (SQLException e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            assert preparedStatement != null;
            assert resultSet != null;
            DatabaseHelper.closePAndRMethod(preparedStatement, resultSet);
        }
        return u;
    }

    public static boolean deleteUser(@NotNull User user) {
        PreparedStatement preparedStatement = null;

        boolean okay = false;
        try {
            String insert = "DELETE FROM EMPLOYEE where name = ? and username = ? and id = ? ";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(insert);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getUserName());
            preparedStatement.setString(3, user.getId());
            okay = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return okay;
    }

    public static boolean valid(@NotNull String user, @NotNull String password) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        boolean okay = false;
        Preferences preferences = Preferences.getPreferences();
        try {
            String query = "select *from Employee where username = ? and password = ?";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, user);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                okay = true;
                Preferences.setPreference(preferences);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert preparedStatement != null;
            assert resultSet != null;
            closePAndRMethod(preparedStatement, resultSet);
        }
        return okay;
    }

    public static ObservableList<User> getUserList() {
        ObservableList<User> users = FXCollections.observableArrayList();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            String query = "SELECT * FROM EMPLOYEE WHERE TRUE";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                users.add(new User(resultSet.getString("name")
                        , resultSet.getString("username")
                        , resultSet.getString("id")
                        , resultSet.getString("password")
                        , resultSet.getString("access")));
            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            assert preparedStatement != null;
            assert resultSet != null;
            closePAndRMethod(preparedStatement, resultSet);
        }
        return users;
    }

}

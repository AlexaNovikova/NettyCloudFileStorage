import java.sql.*;
import java.util.logging.Level;

public class BaseAuthService {

    private Connection connection;
    private  PreparedStatement regPrepStm;
    private PreparedStatement authPrepStm;
    private PreparedStatement changeNickStm;


    public BaseAuthService(){
        try {
            connect();
        } catch (SQLException|ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:CloudUsers.db");
    }

    public void disconnect(){
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

//public Integer registration (String login, String password) {
//    try {
//        connect();
//       // statement = connection.createStatement();
//        regStatement();
//        regPrepStm.setString(1, nick);
//        regPrepStm.setString(2,login);
//        regPrepStm.setString(3,password);
//        int rez = regPrepStm.executeUpdate();
//        regPrepStm.close();;
//        return rez;
//    } catch (SQLException|ClassNotFoundException e) {
//        e.printStackTrace();
//    }
//    finally {
//        disconnect();
//    }
//    return -1;
//
//}


public void regStatement () throws SQLException {
       regPrepStm = connection.prepareStatement("INSERT INTO users (nick,login,password) VALUES (?,?,?);");
}
public void authStatement () throws SQLException {
        authPrepStm = connection.prepareStatement("SELECT * FROM users WHERE login= ? AND password= ?;");
}

    public String checkAuth(String login, String password){
        try {
            authStatement();
            authPrepStm.setString(1,login);
            authPrepStm.setString(2,password);
            ResultSet rez = authPrepStm.executeQuery();
            if (rez.next()) {
                NettyServer.logger.log(Level.FINE,login+ " прошел авторизацию.");

                return login;
            }
            else {

                return null;
            }

        } catch (SQLException e) {

            NettyServer.logger.log(Level.SEVERE,"DataBase error");
        e.printStackTrace();
        }
        finally {

            try {
                authPrepStm.close();
            } catch (SQLException throwables) {
                NettyServer.logger.log(Level.SEVERE,"DataBase error");
                throwables.printStackTrace();
            }
        }
        return null;
     }
}

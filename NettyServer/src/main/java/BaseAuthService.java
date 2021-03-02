import java.sql.*;

public class BaseAuthService {

    private Connection connection;
    private  PreparedStatement regPrepStm;
    private PreparedStatement authPrepStm;
    private PreparedStatement changeNickStm;

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
            connect();
        } catch (SQLException|ClassNotFoundException e) {
           e.printStackTrace();
        }
        try {
            authStatement();
            authPrepStm.setString(1,login);
            authPrepStm.setString(2,password);
            ResultSet rez = authPrepStm.executeQuery();
            if (rez.next()) {
                System.out.println(login+ " прошел авторизацию.");
                authPrepStm.close();
                return login;
            }
            else {
                authPrepStm.close();
                return null;
            }

        } catch (SQLException throwables) {

           // throwables.printStackTrace();
        }
        finally {
            disconnect();
        }
        return null;
     }
}

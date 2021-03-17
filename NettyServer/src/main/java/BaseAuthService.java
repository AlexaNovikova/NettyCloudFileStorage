import java.sql.*;
import java.util.logging.Level;

public class BaseAuthService {

    private Connection connection;
    private  PreparedStatement regPrepStm;
    private PreparedStatement authPrepStm;


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

public Integer registration (String login, String password) {
    try {
        regStatement();
        regPrepStm.setString(1,login);
        regPrepStm.setString(2,password);
        try{
            int rez = regPrepStm.executeUpdate();
            regPrepStm.close();
            return rez;
        }
        catch (SQLException e){
         NettyServer.logger.log(Level.INFO, "Не уникальные лошин/пароль!");
         return -1;
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return -1;
}


public void regStatement () throws SQLException {
       regPrepStm = connection.prepareStatement("INSERT INTO users (login,password) VALUES (?,?);");
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

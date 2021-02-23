package commands;


import java.io.Serializable;

public class AuthCommandData implements Serializable {
    String login;
    String password;

    public AuthCommandData(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "AuthCommandData{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

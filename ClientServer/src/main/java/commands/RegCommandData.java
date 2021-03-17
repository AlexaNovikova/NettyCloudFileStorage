package commands;

import java.io.Serializable;

public class RegCommandData implements Serializable {
    String login;
    String password;

    public RegCommandData(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}

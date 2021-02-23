package commands;

import java.io.Serializable;

public class ErrorCommandData implements Serializable {
    String error;

    public ErrorCommandData(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}

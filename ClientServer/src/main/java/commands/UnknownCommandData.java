package commands;

import java.io.Serializable;

public class UnknownCommandData implements Serializable {
    String error;
    public UnknownCommandData(String error) {
        this.error=error;
    }

    public String getError() {
        return error;
    }
}

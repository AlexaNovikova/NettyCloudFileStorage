package commands;

import java.io.Serializable;

public class CreateDidCommandData implements Serializable {
    private String dirName;

    public CreateDidCommandData(String dirName) {
        this.dirName = dirName;
    }

    public String getDirName() {
        return dirName;
    }
}

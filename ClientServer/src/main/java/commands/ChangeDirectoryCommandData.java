package commands;

import java.io.Serializable;

public class ChangeDirectoryCommandData implements Serializable {
    String path;
    public ChangeDirectoryCommandData(String path){
        this.path=path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "ChangeDirectoryCommandData{" +
                "path='" + path + '\'' +
                '}';
    }
}

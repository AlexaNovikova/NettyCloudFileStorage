package commands;

import java.io.Serializable;

public class MoveFileCommandData implements Serializable {
    String oldFile;
    String newPLaceFile;

    public MoveFileCommandData(String oldFile, String newPLaceFile) {
        this.oldFile = oldFile;
        this.newPLaceFile = newPLaceFile;
    }

    public String getNewPLaceFile() {
        return newPLaceFile;
    }

    public String getOldFile() {
        return oldFile;
    }
}

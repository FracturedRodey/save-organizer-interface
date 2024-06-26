package de.fracturedassemblies.saveorganizer.data;

import java.util.List;

public class SaveDirectory {
    private String directory;
    private List<String> saveFiles;

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public List<String> getSaveFiles() {
        return saveFiles;
    }

    public void setSaveFiles(List<String> saveFiles) {
        this.saveFiles = saveFiles;
    }

    @Override
    public String toString() {
        return "SaveDirectory [directory=" + directory + ", saveFiles=" + saveFiles + "]";
    }
}

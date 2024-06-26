package de.fracturedassemblies.saveorganizer.data;

public class SaveFileRequest {
    String directory;
    String file;

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String toString() {
        return "SaveFileRequest [directory=" + directory + ", file=" + file + "]";
    }
}

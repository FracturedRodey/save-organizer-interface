package de.fracturedassemblies.saveorganizer.services;

import de.fracturedassemblies.saveorganizer.data.SaveDirectory;
import de.fracturedassemblies.saveorganizer.data.SaveFileResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

@EnableScheduling
@Service
public class FileOrganizerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileOrganizerService.class);

    private String saveFilePath;
    private String delimiter;

    @Value(value = "${save.file.path.windows}")
    private String windowsFilePath;

    @Value(value = "${save.file.path.mac}")
    private String macFilePath;

    @Value(value = "${save.file.path.linux}")
    private String linuxFilePath;

    private SaveFileResult saveFiles;

    @Scheduled(fixedDelay = Long.MAX_VALUE, timeUnit = TimeUnit.SECONDS)
    public void initialSaveFilePathLoad() {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            saveFilePath = this.windowsFilePath;
            delimiter = "/";
        } else if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            saveFilePath = this.macFilePath;
            delimiter = "/";
        } else {
            saveFilePath = this.linuxFilePath;
            delimiter = "/";
        }

        this.loadSaveFiles();
    }

    @Scheduled(fixedDelayString = "10", initialDelayString = "10", timeUnit = TimeUnit.SECONDS)
    public void scheduledLoadLatestSaveFiles() {
        this.loadSaveFiles();
    }

    public ResponseEntity<Void> setSaveFilePath(String saveFilePath) {
        try {
            String path = Path.of(saveFilePath).toString();
            LOGGER.info("Trying to save new save file path: {}", path);
            this.saveFilePath = saveFilePath;
            this.loadSaveFiles();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (InvalidPathException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public void loadSaveFiles() {
        File[] directories = new File(this.saveFilePath).listFiles(File::isDirectory);
        List<SaveDirectory> saveList = new ArrayList<>();
        if (directories != null) {
            for (File directory : directories) {
                SaveDirectory saveDirectory = this.getSaveDirectory(directory);
                saveList.add(saveDirectory);
            }
            saveFiles = new SaveFileResult(saveList);
            LOGGER.info("Following files were found: {}", this.saveFiles);
        } else {
            LOGGER.error("No directories found in {}", this.saveFilePath);
        }
    }

    private SaveDirectory getSaveDirectory(File directory) {
        SaveDirectory saveDirectory = new SaveDirectory();
        List<String> files = new ArrayList<>();
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.getName().toLowerCase().endsWith(".sl2")) {
                files.add(file.getName().substring(0, file.getName().length() - 4));
            }
        }
        saveDirectory.setDirectory(directory.getName());
        saveDirectory.setSaveFiles(files);
        return saveDirectory;
    }

    public ResponseEntity<Void> saveFile(String directory, String fileName) {
        this.loadSaveFiles();
        try {
            if (directory.isEmpty()) {
                throw new InvalidPathException(directory, "Directory cannot be empty");
            }
            Files.createDirectories(Paths.get(this.saveFilePath + delimiter + directory));
            Path copied = Paths.get(this.saveFilePath + "/ER0000.sl2");
            Path pasteDirectory = Path.of(this.saveFilePath + delimiter + directory + delimiter + fileName + ".sl2");
            Files.copy(copied, pasteDirectory, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOGGER.error("Saving file {} failed. {}", fileName, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (InvalidPathException e) {
            return this.invalidPathResponse(e);
        }
        this.loadSaveFiles();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<Void> loadFile(String directory, String fileName) {
        this.loadSaveFiles();
        Path copied = Paths.get(this.saveFilePath + delimiter + directory + delimiter + fileName + ".sl2");
        Path mainPath = Path.of(this.saveFilePath + delimiter + "ER0000.sl2");
        try {
            Files.copy(copied, mainPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOGGER.error("Loading file {} was not successful. {}", fileName, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (InvalidPathException e) {
            return this.invalidPathResponse(e);
        }
        LOGGER.info("Loading of file {} was successful.", fileName);
        this.loadSaveFiles();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<Void> deleteFile(String directory, String fileName) {
        this.loadSaveFiles();
        try {
            this.cleanUp(Path.of(this.saveFilePath + delimiter + directory + delimiter + fileName + ".sl2"));
            LOGGER.info("Deleting file {} from directory: {}", fileName, directory);
            this.loadSaveFiles();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IOException e) {
            LOGGER.error("No such file: {}. {}", fileName, e.getStackTrace());
            this.loadSaveFiles();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (InvalidPathException e) {
            return this.invalidPathResponse(e);
        }
    }

    public ResponseEntity<Void> deleteDirectory(String directory) {
        this.loadSaveFiles();
        try {
            File directoryFile = new File(this.saveFilePath + delimiter + directory);
            for (File file : Objects.requireNonNull(directoryFile.listFiles())) {
                deleteFile(directory, file.getName().substring(0, file.getName().length() - 4));
            }
            this.cleanUp(Path.of(this.saveFilePath + delimiter + directory));
        } catch (IOException e) {
            LOGGER.error("Error deleting directory: {}. {}", directory, e.getStackTrace());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (InvalidPathException e) {
            return this.invalidPathResponse(e);
        }
        this.loadSaveFiles();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private ResponseEntity<Void> invalidPathResponse(InvalidPathException e) {
        LOGGER.error("Invalid file or path. {}", e.getMessage());
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    private void cleanUp(Path path) throws IOException {
        Files.delete(path);
        LOGGER.info("Deleted file/directory at: {}", path);
    }

    public SaveFileResult getSaveFiles() {
        this.loadSaveFiles();
        return saveFiles;
    }

    public ResponseEntity<String> getSaveFilePath() {
        if (this.saveFilePath != null) {
            return new ResponseEntity<>(this.saveFilePath, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

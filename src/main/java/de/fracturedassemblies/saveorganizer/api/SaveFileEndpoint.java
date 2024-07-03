package de.fracturedassemblies.saveorganizer.api;

import de.fracturedassemblies.saveorganizer.data.SaveFileRequest;
import de.fracturedassemblies.saveorganizer.data.SaveFileResult;
import de.fracturedassemblies.saveorganizer.services.FileOrganizerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "save-organizer-interface", description = "Save organizer for Elden Ring")
@RestController
public class SaveFileEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(SaveFileEndpoint.class);
    private final FileOrganizerService fileOrganizerService;

    public SaveFileEndpoint(FileOrganizerService fileOrganizerService) {
        this.fileOrganizerService = fileOrganizerService;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @Operation(summary = "Query existing Elden Ring save files from local storage.",
            description = "Returns a map with the names of the profiles with their corresponding save files.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns a map with the names of the profiles with their corresponding save files.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SaveFileResult.class))) })
    @GetMapping(value = "/int/api/1.0/savefiles", produces = MediaType.APPLICATION_JSON_VALUE)
    public SaveFileResult savefiles() {
        LOGGER.info("Loading save files from local storage...");
        return this.fileOrganizerService.getSaveFiles();
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @Operation(summary = "Load selected game file into memory.",
            description = "Loads selected file from chosen directory into the current ER0000.sl2 file.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Loads selected file into memory.") })
    @PutMapping(value = "/int/api/1.0/loadfile")
    public ResponseEntity<Void> loadfile(@RequestBody SaveFileRequest request) {
        LOGGER.info("Loading save file from local storage.");
        return this.fileOrganizerService.loadFile(request.getDirectory(), request.getFile());
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @Operation(summary = "Saves selected game file into memory.",
            description = "Saves selected file into chosen directory from the current ER0000.sl2 file.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Saves selected file into memory.") })
    @PutMapping(value = "/int/api/1.0/savefile")
    public ResponseEntity<Void> savefile(@RequestBody SaveFileRequest request) {
        LOGGER.info("Saving save file {} into local storage {}.", request.getFile(), request.getDirectory());
        return this.fileOrganizerService.saveFile(request.getDirectory(), request.getFile());
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @Operation(summary = "Deletes selected file from memory.", description = "Selected file from chosen directory gets permanently deleted.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Deletes selected file from memory.") })
    @DeleteMapping(value = "/int/api/1.0/deletefile")
    public ResponseEntity<Void> deletefile(String directory, String fileName) {
        LOGGER.info("Deleting file {} in directory {}.", fileName, directory);
        return this.fileOrganizerService.deleteFile(directory, fileName);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @Operation(summary = "Deletes selected directory from memory.", description = "Selected directory gets permanently deleted.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Deletes selected directory from memory.") })
    @DeleteMapping(value = "/int/api/1.0/deletedirectory")
    public ResponseEntity<Void> deletedirectory(String directory) {
        LOGGER.info("Deleting directory...");
        return this.fileOrganizerService.deleteDirectory(directory);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @Operation(summary = "Sets individual save directory path.",
            description = "Instead of the given paths of the save-organizer-interface this endpoint gives the possibility to select own path.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Sets individual save directory path.") })
    @PutMapping(value = "/int/api/1.0/savefilespath")
    public ResponseEntity<Void> savefilespath(@RequestBody String path) {
        LOGGER.info("Replacing save file path with new path {}", path);
        return this.fileOrganizerService.setSaveFilePath(path);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @Operation(summary = "Returns current save directory path.",
            description = "To give the user some direction on which folder or path is currently used to manipulate the save files this endpoint returns the current value used.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Returns current save directory path.",
            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE)) })
    @GetMapping(value = "/int/api/1.0/savefilespath", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> savefilespath() {
        LOGGER.info("Returning save file path.");
        return this.fileOrganizerService.getSaveFilePath();
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @Operation(summary = "Renames selected save.",
            description = "Since the user doesn't always know what the save file should be called in advance, this endpoint allows the user to rename the save file.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Renames selected save.",
            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE)) })
    @PutMapping(value = "/int/api/1.0/renamefile", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Void> renamefile(String directory, String oldName, String newName) {
        LOGGER.info("Renaming save file.");
        return this.fileOrganizerService.renameFile(directory, oldName, newName);
    }
}

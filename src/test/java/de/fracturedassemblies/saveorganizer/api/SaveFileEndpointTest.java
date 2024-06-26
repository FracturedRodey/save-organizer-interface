package de.fracturedassemblies.saveorganizer.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.fracturedassemblies.saveorganizer.data.SaveDirectory;
import de.fracturedassemblies.saveorganizer.data.SaveFileRequest;
import de.fracturedassemblies.saveorganizer.services.FileOrganizerService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SaveFileEndpointTest {
    private static final String SAVE_FILE_ENDPOINT = "/int/api/1.0/savefile";
    private static final ObjectMapper jsonMapper = new ObjectMapper().configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    private static final ObjectWriter jsonWriter = jsonMapper.writer().withDefaultPrettyPrinter();
    @Autowired
    private MockMvc mvc;

    @Autowired
    private FileOrganizerService fileOrganizerService;

    @BeforeEach
    void beforeEach() {
        this.fileOrganizerService.saveFile("Dagger", "Limgrave");
        this.fileOrganizerService.saveFile("Dagger", "Mohg");
        this.fileOrganizerService.saveFile("Sword", "Test");
    }

    @AfterEach
    void afterEach() {
        List<SaveDirectory> saveDirectories = this.fileOrganizerService.getSaveFiles().saveFileResult();
        for (SaveDirectory saveDirectory : saveDirectories) {
            this.fileOrganizerService.deleteDirectory(saveDirectory.getDirectory());
        }
    }

    @Test
    void saveFileTest() throws Exception {
        String directory = "Sword";
        String fileName = "Miquella";

        SaveFileRequest request = new SaveFileRequest();
        request.setDirectory(directory);
        request.setFile(fileName);

        this.mvc.perform(put(SAVE_FILE_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(jsonWriter.writeValueAsString(request)))
                .andExpect(status().isOk());

        SaveDirectory saveDirectory = this.getSaveDirectory(directory);

        assert (saveDirectory != null);
        assert (saveDirectory.getSaveFiles().size() == 2);
        assert (saveDirectory.getSaveFiles().contains(fileName));
    }

    @Test
    void invalidSaveFileNameTest() throws Exception {
        String directory = "Sword";
        String fileName = "*";

        SaveFileRequest request = new SaveFileRequest();
        request.setDirectory(directory);
        request.setFile(fileName);

        this.mvc.perform(put(SAVE_FILE_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(jsonWriter.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        SaveDirectory saveDirectory = this.getSaveDirectory(directory);

        assert (saveDirectory != null);
        assert (saveDirectory.getSaveFiles().size() == 1);
    }

    @Test
    void emptySaveFileNameTest() throws Exception {
        String directory = "Sword";
        String fileName = "";

        SaveFileRequest request = new SaveFileRequest();
        request.setDirectory(directory);
        request.setFile(fileName);

        this.mvc.perform(put(SAVE_FILE_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(jsonWriter.writeValueAsString(request)))
                .andExpect(status().isOk());

        SaveDirectory saveDirectory = this.getSaveDirectory(directory);

        assert (saveDirectory != null);
        assert (saveDirectory.getSaveFiles().size() == 2);
    }

    @Test
    void newDirectoryTest() throws Exception {
        String directory = "NewDirectory";
        String fileName = "Miquella";

        SaveFileRequest request = new SaveFileRequest();
        request.setDirectory(directory);
        request.setFile(fileName);

        this.mvc.perform(put(SAVE_FILE_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(jsonWriter.writeValueAsString(request)))
                .andExpect(status().isOk());

        SaveDirectory saveDirectory = this.getSaveDirectory(directory);
        assert (saveDirectory != null);
        assert (saveDirectory.getSaveFiles().contains(fileName));
    }

    @Test
    void invalidDirectoryNameTest() throws Exception {
        String directory = "*";
        String fileName = "Miquella";

        SaveFileRequest request = new SaveFileRequest();
        request.setDirectory(directory);
        request.setFile(fileName);

        this.mvc.perform(put(SAVE_FILE_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(jsonWriter.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        SaveDirectory saveDirectory = this.getSaveDirectory(directory);
        assert (saveDirectory == null);
    }

    @Test
    void emptyDirectoryNameTest() throws Exception {
        String directory = "";
        String fileName = "Miquella";

        SaveFileRequest request = new SaveFileRequest();
        request.setDirectory(directory);
        request.setFile(fileName);

        this.mvc.perform(put(SAVE_FILE_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(jsonWriter.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        SaveDirectory saveDirectory = this.getSaveDirectory(directory);
        assert (saveDirectory == null);
    }

    @Test
    void invalidSaveFileRequestTest() throws Exception {
        String directory = "";

        this.mvc.perform(put(SAVE_FILE_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(jsonWriter.writeValueAsString(directory)))
                .andExpect(status().isBadRequest());
    }

    SaveDirectory getSaveDirectory(String directory) {
        for (SaveDirectory dir : this.fileOrganizerService.getSaveFiles().saveFileResult()) {
            if (dir.getDirectory().equals(directory)) {
                return dir;
            }
        }
        return null;
    }

    //TODO: Write loadfile test and save file test to check copy/pasted data. Currently only testing save functionality.

}

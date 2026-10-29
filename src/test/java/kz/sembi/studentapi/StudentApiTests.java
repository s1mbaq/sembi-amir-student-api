package kz.sembi.studentapi;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest @AutoConfigureMockMvc
class StudentApiTests {
    @Autowired MockMvc mvc;
    @Autowired StudentRepository repo;
    final String data="{\"fullName\":\"Sembi Amir\",\"groupName\":\"IT2-2307\",\"studentNumber\":\"37693\",\"email\":\"amir@example.test\"}";
    @BeforeEach void reset() { repo.deleteAll(); }
    @Test void createReadUpdateDelete() throws Exception {
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON).content(data)).andExpect(status().isCreated()).andExpect(jsonPath("$.fullName").value("Sembi Amir"));
        long id=repo.findAll().getFirst().getId();
        mvc.perform(get("/api/students")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/students/"+id)).andExpect(status().isOk()).andExpect(jsonPath("$.studentNumber").value("37693"));
        mvc.perform(put("/api/students/"+id).contentType(MediaType.APPLICATION_JSON).content(data.replace("IT2-2307","IT2-2307-updated"))).andExpect(status().isOk()).andExpect(jsonPath("$.groupName").value("IT2-2307-updated"));
        mvc.perform(delete("/api/students/"+id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/students/"+id)).andExpect(status().isNotFound());
    }
    @Test void missingStudent() throws Exception { mvc.perform(get("/api/students/999999")).andExpect(status().isNotFound()); }
    @Test void invalidRequest() throws Exception { mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest()); }
    @Test void duplicateNumber() throws Exception {
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON).content(data)).andExpect(status().isCreated());
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON).content(data)).andExpect(status().isConflict());
    }
    @Test void identity() throws Exception { mvc.perform(get("/")).andExpect(status().isOk()).andExpect(jsonPath("$.studentId").value("37693")); }
}

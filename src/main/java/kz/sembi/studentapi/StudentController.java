package kz.sembi.studentapi;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.*;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentRepository repository;
    public StudentController(StudentRepository repository) { this.repository=repository; }
    @GetMapping public List<Student> all() { return repository.findAll(Sort.by("id")); }
    @GetMapping("/{id}") public Student one(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Student not found: "+id));
    }
    @PostMapping public ResponseEntity<Student> create(@Valid @RequestBody StudentRequest request) {
        Student saved=repository.saveAndFlush(new Student(request));
        return ResponseEntity.created(URI.create("/api/students/"+saved.getId())).body(saved);
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) {
        repository.delete(one(id)); return ResponseEntity.noContent().build();
    }
}

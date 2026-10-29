package kz.sembi.studentapi;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
@RestControllerAdvice
public class ApiErrors {
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> duplicate(DataIntegrityViolationException e) {
        return ResponseEntity.status(409).body(Map.of("error","Student number already exists or data violates a database constraint"));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validation(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(Map.of("error","Invalid student data", "fields",e.getBindingResult().getFieldErrors().stream().map(x -> x.getField()+": "+x.getDefaultMessage()).toList()));
    }
}

package kz.sembi.studentapi;
import java.util.Map;
import org.springframework.web.bind.annotation.*;
@RestController
public class InfoController {
    @GetMapping("/") public Map<String,String> info() {
        return Map.of("application","sembi-amir-student-api","student","Sembi Amir","group","IT2-2307","studentId","37693","api","/api/students");
    }
}

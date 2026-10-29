package kz.sembi.studentapi;
import jakarta.persistence.*;
@Entity
@Table(name="students", uniqueConstraints=@UniqueConstraint(name="uk_student_number", columnNames="student_number"))
public class Student {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String fullName;
    @Column(nullable=false) private String groupName;
    @Column(name="student_number", nullable=false) private String studentNumber;
    @Column(nullable=false) private String email;
    protected Student() {}
    public Student(StudentRequest r) { update(r); }
    public void update(StudentRequest r) {
        fullName=r.fullName().trim(); groupName=r.groupName().trim();
        studentNumber=r.studentNumber().trim(); email=r.email().trim();
    }
    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getGroupName() { return groupName; }
    public String getStudentNumber() { return studentNumber; }
    public String getEmail() { return email; }
}

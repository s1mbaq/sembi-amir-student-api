package kz.sembi.studentapi;
import jakarta.validation.constraints.*;
public record StudentRequest(
    @NotBlank @Size(max=120) String fullName,
    @NotBlank @Size(max=40) String groupName,
    @NotBlank @Pattern(regexp="[0-9]{1,20}") String studentNumber,
    @NotBlank @Email @Size(max=200) String email
) {}

package com.resolum.intiva.platform.iam.interfaces.rest.resources.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "Multipart request body for user registration.")
public class SignUpResource {

    @Schema(description = "User email address.", example = "test@gmail.com")
    private String email;

    @Schema(description = "User password.", example = "P@ssw0rd!")
    private String password;

    @Schema(description = "Display name of the user.", example = "John Doe")
    private String name;

    @Schema(description = "Age of the user.", example = "25")
    private Integer age;

    @Schema(description = "Contact phone number.", example = "+51987654321")
    private String phoneNumber;

    @Schema(description = "Short biography.")
    private String bio;

    @Schema(description = "Avatar image file (JPEG, PNG).", type = "string", format = "binary")
    private MultipartFile avatarFile;

    public SignUpResource() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public MultipartFile getAvatarFile() { return avatarFile; }
    public void setAvatarFile(MultipartFile avatarFile) { this.avatarFile = avatarFile; }
}

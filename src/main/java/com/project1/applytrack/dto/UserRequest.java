package com.project1.applytrack.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRequest {
    @NotBlank(message = "User Name Required")
    private String fullname;
    @NotBlank(message = "Password Required")
    @Size(min = 8,message = "Password must be more than 8 characters")
    private String password;
    @NotBlank(message = "Email Required")
    @Email(message = "Enter in email format")
    private String email;
    public String getFullname() {
        return fullname;
    }
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

}

package ru.job4j.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class PersonDTO {

    @NotBlank(message = "Login must be not empty")
    private String login;

    @NotBlank(message = "Login must be not empty")
    @Min(value = 6, message = "Password should not be less than 6 characters")
    private String password;
}

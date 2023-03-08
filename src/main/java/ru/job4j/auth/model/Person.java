package ru.job4j.auth.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "persons")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull(message = "Id must be not null")
    private int id;

    @NotBlank(message = "Login must be not empty")
    private String login;

    @NotBlank(message = "Login must be not empty")
    @Min(value = 5, message = "Password should not be less than 5 characters")
    private String password;
}

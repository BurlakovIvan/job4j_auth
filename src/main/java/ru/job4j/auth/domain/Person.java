package ru.job4j.auth.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.job4j.auth.filter.Operation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @NotNull(message = "Id must be non null", groups = {
            Operation.OnUpdate.class
    })
    private int id;
    @NotBlank(message = "Login must be not empty")
    private String login;
    @Size(min = 8, max = 20, message
            = "Password must be between 8 and 20 characters")
    private String password;
}

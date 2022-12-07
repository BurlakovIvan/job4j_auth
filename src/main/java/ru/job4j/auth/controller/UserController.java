package ru.job4j.auth.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.repository.UserStore;

import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private UserStore users;
    private BCryptPasswordEncoder encoder;

    @PostMapping("/sign-up")
    public void signUp(@RequestBody Person person) {
        validate(person);
        person.setPassword(encoder.encode(person.getPassword()));
        users.save(person);
    }

    private void validate(Person person) {
        if (person == null || person.getPassword().isEmpty() || person.getLogin().isEmpty()) {
            throw new NullPointerException("Пользователь (логин или пароль) не может быть пустым");
        }
    }

    @GetMapping("/all")
    public List<Person> findAll() {
        return users.findAll();
    }
}
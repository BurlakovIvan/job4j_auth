package ru.job4j.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.service.PersonService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/person")
@AllArgsConstructor
public class PersonController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class.getSimpleName());
    private final PersonService persons;

    private final ObjectMapper objectMapper;

    @GetMapping("/")
    public ResponseEntity<List<Person>> findAll() {
        return new ResponseEntity<>(persons.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        validateId(id);
        return new ResponseEntity<>(persons.findById(id)
                .orElseThrow(this::notFoundPerson), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<Person> create(@RequestBody Person person) {
        validate(person);
        return new ResponseEntity<>(persons.save(person), HttpStatus.CREATED);
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Person person) {
        validate(person);
        Person newPerson = persons.save(person);
        Optional<Person> findPerson = persons.findById(newPerson.getId());
        if (findPerson.isPresent()) {
            return ResponseEntity.ok().build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Не удалось обновить данные пользователя"
            );
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        validateId(id);
        Optional<Person> findPerson = persons.findById(id);
        ResponseEntity<Void> result = ResponseEntity.ok().build();
        if (findPerson.isPresent()) {
            Person person = new Person();
            person.setId(id);
            persons.delete(person);
        } else {
           throw notFoundPerson();
        }
        return result;
    }

    private void validate(Person person) {
        if (person == null || person.getPassword().isEmpty() || person.getLogin().isEmpty()) {
            throw new NullPointerException("Пользователь (логин или пароль) не может быть пустым");
        }
        if (person.getPassword().length() < 8) {
            throw new IllegalArgumentException("Ошибка. Короткий пароль");
        }
    }

    private void validateId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Некорректный идентификатор");
        }
    }

    private ResponseStatusException notFoundPerson() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Пользователь с таким идентификатором не найден"
        );
    }

    @ExceptionHandler(value = { IllegalArgumentException.class })
    public void exceptionHandler(Exception e, HttpServletRequest request,
                                 HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() { {
            put("message", e.getMessage());
            put("type", e.getClass());
        }}));
        LOGGER.error(e.getLocalizedMessage());
    }
}

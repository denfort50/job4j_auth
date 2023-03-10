package ru.job4j.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.job4j.auth.model.PersonDTO;
import ru.job4j.auth.exception.PersonDoesNotExistException;
import ru.job4j.auth.model.Person;
import ru.job4j.auth.service.PersonService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/person")
public class PersonController {

    private final PersonService personService;

    private BCryptPasswordEncoder encoder;

    private final ObjectMapper objectMapper;

    @GetMapping("/all")
    public ResponseEntity<List<Person>> findAll() {
        List<Person> personList = personService.findAll();
        ResponseEntity<List<Person>> response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        if (!personList.isEmpty()) {
            response = new ResponseEntity<>(personList, HttpStatus.OK);
        }
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@Min(0) @PathVariable int id) {
        var person = this.personService.findById(id);
        return new ResponseEntity<>(
                person.orElse(new Person()),
                person.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    @PatchMapping("/update")
    public ResponseEntity<String> updatePartOfFields(@Valid @RequestBody PersonDTO personDTO) {
        Person person = personService.findByLogin(personDTO.getLogin())
                .orElseThrow(() -> new PersonDoesNotExistException("Such person does not exist in database"));
        person.setPassword(personDTO.getPassword());
        personService.save(person);
        return ResponseEntity.ok("Password was updated");
    }

    @PostMapping("/")
    public ResponseEntity<Person> create(@Valid @RequestBody Person person) {
        return new ResponseEntity<>(
                personService.save(person),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@Valid @RequestBody Person person) {
        personService.findById(person.getId())
                .orElseThrow(() -> new PersonDoesNotExistException("Such person does not exist in database"));
        personService.save(person);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Min(0) @PathVariable int id) {
        Person personInDb = personService.findById(id)
                .orElseThrow(() -> new PersonDoesNotExistException("Such person does not exist in database"));
        personService.delete(personInDb);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sign-up")
    public void signUp(@Valid @RequestBody Person person) {
        String login = person.getLogin();
        String password = person.getPassword();
        if (login == null || password == null) {
            throw new NullPointerException("Login and password mustn't be empty");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Invalid password. Password length must be more than 5 characters");
        }
        person.setPassword(encoder.encode(person.getPassword()));
        personService.save(person);
    }

    @ExceptionHandler(value = { IllegalArgumentException.class })
    public void exceptionHandler(Exception exception, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() { {
            put("message", exception.getMessage());
            put("type", exception.getClass());
        }}));
    }
}

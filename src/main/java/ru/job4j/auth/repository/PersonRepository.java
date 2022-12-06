package ru.job4j.auth.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.auth.domain.Person;

import java.util.List;

@Repository
public interface PersonRepository extends CrudRepository<Person, Integer> {
    @Query("SELECT p FROM Person p")
    List<Person> findAll();
}

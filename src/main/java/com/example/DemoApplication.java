package com.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.Assert;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.util.List;

@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

//    @Bean
//    public DataSource dataSource() {
//        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
//        dataSource.setUrl("jdbc:h2:mem:mydb;MODE=Oracle;DB_CLOSE_ON_EXIT=FALSE");
//        dataSource.setDriverClassName("org.h2.Driver");
//        dataSource.setCommitOnReturn(true);
//        return dataSource;
//    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public CommandLineRunner clr(PersonService personService) {
        return strings -> {
            Person p1 = new Person();
            p1.setName("p1");

            personService.save(p1);

            //check if data was committed
            Assert.isTrue(personService.findPeople().size() == 1, "Data was not committed !!!");
        };
    }
}

interface PersonService {
    List<Person> findPeople();

    Person save(Person p);
}

@Service
class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;

    PersonServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public List<Person> findPeople() {
        return personRepository.findAll();
    }

    @Transactional
    @Override
    public Person save(Person person) {
        return personRepository.save(person);
    }
}

interface PersonRepository extends JpaRepository<Person, Long> {
}

@Entity
class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    public Person() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}


package com.omeganessy.payroll.config;

import com.omeganessy.payroll.entity.Employee;
import com.omeganessy.payroll.entity.Order;
import com.omeganessy.payroll.entity.Status;
import com.omeganessy.payroll.repository.EmployeeRepository;
import com.omeganessy.payroll.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabase {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(EmployeeRepository repository, OrderRepository orderRepository){
        return args -> {
            log.info("Preloading {}", repository.save(new Employee("Bilbo","Baggins", "burglar")));
            log.info("Preloading {}", repository.save(new Employee("Frodo","Baggins", "thief")));
            log.info("Preloading {}", orderRepository.save(new Order("MacBook Pro", Status.COMPLETED)));
            log.info("Preloading {}", orderRepository.save(new Order("iPhone", Status.IN_PROGRESS)));
        };
    }
}

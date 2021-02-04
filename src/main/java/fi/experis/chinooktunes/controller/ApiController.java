package fi.experis.chinooktunes.controller;

import fi.experis.chinooktunes.model.Customer;
import fi.experis.chinooktunes.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class ApiController {

    private final CustomerRepository customerRepository;

    @Autowired
    public ApiController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping("/api/customers")
    public ArrayList<Customer> getAllCustomers() {
        return customerRepository.getAllCustomers();
    }
}

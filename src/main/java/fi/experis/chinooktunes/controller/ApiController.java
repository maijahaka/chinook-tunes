package fi.experis.chinooktunes.controller;

import fi.experis.chinooktunes.model.Customer;
import fi.experis.chinooktunes.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("/api/customers")
    public ResponseEntity addNewCustomer(@RequestBody Customer customer) {
        // returns null if customer was not added successfully
        Customer addedCustomer = customerRepository.addNewCustomer(customer);

        if (addedCustomer == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(addedCustomer);
    }
}

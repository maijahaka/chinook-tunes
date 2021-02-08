package fi.experis.chinooktunes.controller;

import fi.experis.chinooktunes.model.Customer;
import fi.experis.chinooktunes.model.CustomerWithSpendingInformation;
import fi.experis.chinooktunes.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;

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

    @GetMapping("/api/customers/countries")
    public LinkedHashMap<String, Integer> getNumberOfCustomersInEachCountry() {
        return customerRepository.getNumberOfCustomersPerCountry();
    }

    // get all customers ordered by their total spending (descending)
    @GetMapping("/api/customers/top/spenders")
    public ArrayList<CustomerWithSpendingInformation> getHighestSpenders() {
        return customerRepository.getHighestSpendingCustomers();
    }

    // get the most popular genre or a customer (or genres if several are equally popular)
    @GetMapping("/api/customers/{customerId}/popular/genre")
    public ArrayList<String> getMostPopularGenresOfACustomer(@PathVariable long customerId) {
        return customerRepository.getMostPopularGenresOfACustomer(customerId);
    }

    @PostMapping("/api/customers")
    public ResponseEntity addNewCustomer(@RequestBody Customer customer) {
        // returns null if customer was not added successfully
        Customer addedCustomer = customerRepository.addCustomer(customer);

        if (addedCustomer == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    "Customer was not added successfully.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(addedCustomer);
    }

    @PutMapping("/api/customers/{id}")
    public ResponseEntity updateExistingCustomer(@PathVariable long id,
                                                 @RequestBody Customer customer) {
        // check if customer ids in the path and request body match
        if (id != customer.getCustomerId()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    "Customer was not updated successfully.");
        }

        // returns null if customer was not updated successfully
        Customer updatedCustomer = customerRepository.updateCustomer(customer);

        if (updatedCustomer == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    "Customer was not updated successfully.");
        }

        return ResponseEntity.ok(updatedCustomer);
    }
}

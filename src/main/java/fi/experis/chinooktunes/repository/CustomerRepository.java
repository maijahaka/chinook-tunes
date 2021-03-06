package fi.experis.chinooktunes.repository;

import fi.experis.chinooktunes.logger.CustomLogger;
import fi.experis.chinooktunes.model.Customer;
import fi.experis.chinooktunes.model.CustomerWithSpendingInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

@Repository
public class CustomerRepository {

    private final CustomLogger logger;

    private final String URL = ConnectionHelper.CONNECTION_URL;
    private Connection conn = null;

    @Autowired
    public CustomerRepository(@Qualifier(value="logToConsole") CustomLogger logger) {
        this.logger = logger;
    }

    public ArrayList<Customer> getAllCustomers() {
        ArrayList<Customer> customers = new ArrayList<>();

        try {
            conn = DriverManager.getConnection(URL);
            logger.log("Connection to SQLite has been established.");

            PreparedStatement preparedStatement = conn.prepareStatement(
                    "SELECT CustomerId, FirstName, LastName, Country, PostalCode, Phone, Email " +
                            "FROM customers");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                customers.add(
                        new Customer(
                            resultSet.getInt("CustomerId"),
                            resultSet.getString("FirstName"),
                            resultSet.getString("LastName"),
                            resultSet.getString("Country"),
                            resultSet.getString("PostalCode"),
                            resultSet.getString("Phone"),
                            resultSet.getString("Email")
                ));
            }

            logger.log("All customers were retrieved successfully.");
        } catch (SQLException e) {
            logger.log(e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.log(e.getMessage());
            }
        }
        return customers;
    }

    private Customer getCustomerWithId(long customerId) {
        // initialize customer to be returned to null
        Customer customer = null;
        try {
            conn = DriverManager.getConnection(URL);
            logger.log("Connection to SQLite has been established.");

            PreparedStatement preparedStatement = conn.prepareStatement(
                    "SELECT FirstName, LastName, Country, PostalCode, Phone, Email " +
                            "FROM customers WHERE CustomerId = ?");
            preparedStatement.setLong(1, customerId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // set the customer retrieved from the database as the value to be returned
                customer = new Customer(
                        customerId,
                        resultSet.getString("FirstName"),
                        resultSet.getString("LastName"),
                        resultSet.getString("Country"),
                        resultSet.getString("PostalCode"),
                        resultSet.getString("Phone"),
                        resultSet.getString("Email")
                );
            } else {
                throw new SQLException("Customer with id " + customerId + " was retrieved successfully.");
            }
            logger.log("Customer with id " + customerId + " was retrieved successfully.");
        } catch (SQLException e) {
            logger.log(e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.log(e.getMessage());
            }
        }
        // returns null if no customer was found with the given id
        return customer;
    }

    public LinkedHashMap<String, Integer> getNumberOfCustomersPerCountry() {
        LinkedHashMap<String, Integer> customersPerCountry = new LinkedHashMap<>();

        try {
            conn = DriverManager.getConnection(URL);
            logger.log("Connection to SQLite has been established.");

            // get number of customers in each country in descending order
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "SELECT Country, COUNT(*) AS NoOfCustomers FROM customers " +
                            "GROUP BY Country ORDER BY NoOfCustomers DESC, Country");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                customersPerCountry.put(resultSet.getString("Country"),
                        resultSet.getInt("NoOfCustomers"));
            }

            logger.log("The requested information was retrieved successfully.");
        } catch (SQLException e) {
            logger.log(e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.log(e.getMessage());
            }
        }
        return customersPerCountry;
    }

    // returns all customers in the database ordered by their total spending (descending)
    public ArrayList<CustomerWithSpendingInformation> getHighestSpendingCustomers() {
        ArrayList<CustomerWithSpendingInformation> highestSpendingCustomers = new ArrayList<>();

        try {
            conn = DriverManager.getConnection(URL);
            logger.log("Connection to SQLite has been established.");

            PreparedStatement preparedStatement = conn.prepareStatement(
                    "SELECT customers.CustomerId, FirstName, LastName, Country, PostalCode, " +
                            "Phone, Email, SUM(Total) AS TotalSpending " +
                            "FROM customers LEFT JOIN invoices " +
                            "ON customers.CustomerId = invoices.CustomerId " +
                            "GROUP BY customers.CustomerId " +
                            "ORDER BY TotalSpending DESC, customers.CustomerId");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                highestSpendingCustomers.add(
                        new CustomerWithSpendingInformation(
                                resultSet.getLong("CustomerId"),
                                resultSet.getString("FirstName"),
                                resultSet.getString("LastName"),
                                resultSet.getString("Country"),
                                resultSet.getString("PostalCode"),
                                resultSet.getString("Phone"),
                                resultSet.getString("Email"),
                                resultSet.getDouble("TotalSpending")
                        ));
            }

            logger.log("All customers were retrieved successfully.");
        } catch (SQLException e) {
            logger.log(e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.log(e.getMessage());
            }
        }

        return highestSpendingCustomers;
    }

    // returns the most popular genre or a customer (or genres if several are equally popular)
    public ArrayList<String> getMostPopularGenresOfACustomer(long customerId) {
        ArrayList<String> mostPopularGenres = new ArrayList<>();

        try {
            conn = DriverManager.getConnection(URL);
            logger.log("Connection to SQLite has been established.");

            PreparedStatement preparedStatement = conn.prepareStatement(
                    "WITH CountTracksPerGenreQuery AS (" +
                            "SELECT SUM(ii.Quantity) AS TotalTracksPerGenre, g.Name AS GenreName " +
                            "FROM customers c " +
                            "    LEFT JOIN invoices i ON c.CustomerId = i.CustomerId " +
                            "    LEFT JOIN invoice_items ii on i.InvoiceId = ii.InvoiceId " +
                            "    LEFT JOIN tracks t on ii.TrackId = t.TrackId " +
                            "    LEFT JOIN genres g on t.GenreId = g.GenreId " +
                            "WHERE c.CustomerId = ? GROUP BY g.GenreId) " +
                            "SELECT GenreName FROM  CountTracksPerGenreQuery " +
                            "WHERE TotalTracksPerGenre = " +
                            "   (SELECT MAX(TotalTracksPerGenre) FROM CountTracksPerGenreQuery) " +
                            "ORDER BY GenreName");
            preparedStatement.setLong(1, customerId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                mostPopularGenres.add(resultSet.getString("GenreName"));
            }

            logger.log("The requested information was retrieved successfully.");
        } catch (SQLException e) {
            logger.log(e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.log(e.getMessage());
            }
        }

        return mostPopularGenres;
    }

    public Customer addCustomer(Customer customer) {
        // initialize the id of the customer added to the database as -1
        long addedCustomerId = -1;

        // initialize the customer added to the database as null
        Customer addedCustomer = null;

        try {
            conn = DriverManager.getConnection(URL);
            logger.log("Connection to SQLite has been established.");

            PreparedStatement preparedStatement = conn.prepareStatement(
                    "INSERT INTO customers(FirstName, LastName, Country, PostalCode, Phone, Email)" +
                            "VALUES(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, customer.getFirstName());
            preparedStatement.setString(2, customer.getLastName());
            preparedStatement.setString(3, customer.getCountry());
            preparedStatement.setString(4, customer.getPostalCode());
            preparedStatement.setString(5, customer.getPhone());
            preparedStatement.setString(6, customer.getEmail());

            int addedRows = preparedStatement.executeUpdate();

            // check if adding the customer to the database was successful
            if (addedRows == 0) {
                throw new SQLException("ERROR: Creating customer failed.");
            }

            // get the id that was automatically created for the added customer
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                addedCustomerId = generatedKeys.getLong(1);
            } else {
                throw new SQLException("ERROR : Creating customer failed, no ID obtained.");
            }

            logger.log("Customer " + customer.getFirstName() + " " + customer.getFirstName() +
                    " was added successfully.");
        } catch (SQLException e) {
            logger.log(e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.log(e.getMessage());
            }
        }

        // retrieves the newly added customer from the database, if customer creation was successful
        if (addedCustomerId >= 0) {
            addedCustomer = getCustomerWithId(addedCustomerId);
        }

        // returns null if customer creation was unsuccessful
        return addedCustomer;
    }

    public Customer updateCustomer(Customer customer) {
        // variable for indicating the success status of the update operation
        boolean success = false;

        // initialize the successfully updated customer to null
        Customer updatedCustomer = null;

        try {
            conn = DriverManager.getConnection(URL);
            logger.log("Connection to SQLite has been established.");

            PreparedStatement preparedStatement = conn.prepareStatement(
                    "UPDATE customers SET FirstName = ?, LastName = ?, Country = ?," +
                            "PostalCode = ?, Phone = ?, Email = ? WHERE CustomerId = ?");
            preparedStatement.setString(1, customer.getFirstName());
            preparedStatement.setString(2, customer.getLastName());
            preparedStatement.setString(3, customer.getCountry());
            preparedStatement.setString(4, customer.getPostalCode());
            preparedStatement.setString(5, customer.getPhone());
            preparedStatement.setString(6, customer.getEmail());
            preparedStatement.setLong(7, customer.getCustomerId());

            int modifiedRows = preparedStatement.executeUpdate();

            // check if the update was executed successfully in the database
            success = (modifiedRows != 0);

            if (!success) {
                throw new SQLException("ERROR: Updating customer failed.");
            }

            logger.log("Customer " + customer.getFirstName() + " " + customer.getLastName() +
                    " was updated successfully.");
        } catch (SQLException e) {
            logger.log(e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.log(e.getMessage());
            }
        }

        // returns the updated customer, if the update was successful
        if (success) {
            updatedCustomer = getCustomerWithId(customer.getCustomerId());
        }

        // returns null if customer update was unsuccessful
        return updatedCustomer;
    }

    public boolean customerExists(long customerId) {
        boolean customerFound = false;

        try {
            conn = DriverManager.getConnection(URL);
            logger.log("Connection to SQLite has been established.");

            PreparedStatement preparedStatement = conn.prepareStatement(
                    "SELECT CustomerId FROM customers WHERE CustomerId = ? LIMIT 1");

            preparedStatement.setLong(1, customerId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                customerFound = true;
            }
        } catch (SQLException e) {
            logger.log(e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.log(e.getMessage());
            }
        }

        return customerFound;
    }
}

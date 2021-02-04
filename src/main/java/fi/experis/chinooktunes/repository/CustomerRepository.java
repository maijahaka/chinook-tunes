package fi.experis.chinooktunes.repository;

import fi.experis.chinooktunes.logger.CustomLogger;
import fi.experis.chinooktunes.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;

@Repository
public class CustomerRepository {

    private final CustomLogger logger;

    private String URL = ConnectionHelper.CONNECTION_URL;
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
                            "FROM Customers");
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
}

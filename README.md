# Chinook Tunes

App on Heroku: https://chinook-tunes.herokuapp.com/

This application offers several API endpoints and two views created with Thymeleaf.

The application uses the Chinook sample database with SQLite:
https://github.com/lerocha/chinook-database


## Pages

### Home page

The home page contains a search bar for searching songs by name. There are also lists of randomly picked artists, songs and genres from the database.

### Search results page

The search results show the name, album, artist and genre of the songs whose names contain the given search criterion. The search is case insensitive.

If the search criterion is empty, no search will be performed and an error message will be shown.

## API endpoints

### Get all customers

https://chinook-tunes.herokuapp.com/api/customers

Returns information about all customers in the database as JSON.

### Get the number of customers in each country

https://chinook-tunes.herokuapp.com/api/customers/countries

Returns the number of customers in each country in descending order.

### Get the highest spending customers

https://chinook-tunes.herokuapp.com/api/customers/top/spenders

Returns all customers in the database with information about their total spending, in descending order by total spending.

### Get the most popular genre of a customer

https<nolink>://chinook-tunes.herokuapp.com/api/customers/{customerId}/popular/genre

Returns the most popular genre of the customer with id {customerId}. If multiple genres are equally popular, all of them are returned.

For example: https://chinook-tunes.herokuapp.com/api/customers/1/popular/genre
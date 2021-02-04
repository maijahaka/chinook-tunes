package fi.experis.chinooktunes.logger;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component(value="logToConsole")
public class LogToConsole implements CustomLogger {

    @Override
    public void log(String message) {
        System.out.println(getCurrentTimeAsString() + " - " + message);
    }

    private String getCurrentTimeAsString() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.now();
        return dateTime.format(dateTimeFormatter);
    }
}

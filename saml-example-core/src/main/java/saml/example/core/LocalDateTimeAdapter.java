package saml.example.core;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

final class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public String marshal(LocalDateTime dateTime) {
        return nonNull(dateTime) ? dateTime.format(dateFormat) : "";
    }

    /**
     * @throws DateTimeParseException if dateTime string cannot be parsed
     */
    @Override
    public LocalDateTime unmarshal(String dateTime) {
        return LocalDateTime.parse(dateTime, dateFormat);
    }

}
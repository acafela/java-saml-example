package saml.example.core;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnumValue;

final class Status {

    @XmlElement(name = "StatusCode")
    private final StatusCode statusCode;

    private Status(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public static Status success() {
        StatusCode statusCode = new StatusCode(StatusCode.Value.SUCCESS);
        return new Status(statusCode);
    }

    public static Status fail(StatusCode.Value failValue) {
        StatusCode statusCode = new StatusCode(failValue);
        return new Status(statusCode);
    }

}

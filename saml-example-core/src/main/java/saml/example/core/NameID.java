package saml.example.core;

import javax.xml.bind.annotation.XmlValue;

public final class NameID {

    private NameIDFormat nameIDFormat = NameIDFormat.ENTITY;

    @XmlValue
    private String value;

    public static NameID of(String value) {
        return new NameID(value);
    }

    public NameID(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public void value(String newValue) {
        this.value = newValue;
    }

    public NameIDFormat format() {
        return nameIDFormat;
    }

    public NameID format(NameIDFormat newFormat) {
        nameIDFormat = newFormat;
        return this;
    }

    @Override
    public String toString() {
        return "NameID{" +
                "nameIDFormat=" + nameIDFormat +
                ", value='" + value + '\'' +
                '}';
    }
}

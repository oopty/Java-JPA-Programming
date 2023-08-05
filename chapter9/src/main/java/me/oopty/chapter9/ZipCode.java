package me.oopty.chapter9;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@Access(AccessType.FIELD)
public class ZipCode {
    String zip;
    String plusFour;

    public ZipCode() {
    }

    public ZipCode(String zip, String plusFour) {
        this.zip = zip;
        this.plusFour = plusFour;
    }

    public String getZip() {
        return zip;
    }

    public String getPlusFour() {
        return plusFour;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ZipCode zipCode = (ZipCode) o;
        return Objects.equals(zip, zipCode.zip) && Objects.equals(plusFour, zipCode.plusFour);
    }

    @Override
    public int hashCode() {
        return Objects.hash(zip, plusFour);
    }
}

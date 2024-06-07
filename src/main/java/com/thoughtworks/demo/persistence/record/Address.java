package com.thoughtworks.demo.persistence.record;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Address {

    private UUID id;

    private String countryCode;

    private String zoneCode;

    private String city;

    private String postalCode;

    private String streetAddress1;

    private String streetAddress2;
}

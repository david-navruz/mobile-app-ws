package com.appsdeveloperblog.app.ws.ui.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressesRest {

    private String addressId;
    private String city;
    private String country;
    private String streetName;
    private String postalCode;
    private String type;


}

package com.appsdeveloperblog.app.ws.service;

import com.appsdeveloperblog.app.ws.shared.dto.AddressDto;

import java.util.List;

public interface AddressService {

    List<AddressDto> getAddressesById(String id);

}

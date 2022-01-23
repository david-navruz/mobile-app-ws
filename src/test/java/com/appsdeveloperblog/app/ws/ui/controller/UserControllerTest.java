package com.appsdeveloperblog.app.ws.ui.controller;

import com.appsdeveloperblog.app.ws.service.impl.UserServiceImpl;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDto;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.response.UserRest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @InjectMocks
    UserController userController;

    @Mock
    UserServiceImpl userService;

    UserDto userDto;
    String userId = "abcde";
    String encryptedPasword = "123456";

    @BeforeEach
    void setUp() throws Exception {
        // initmocks initializes the Mocks and injects them at runtime
        MockitoAnnotations.initMocks(this);
        userDto = new UserDto();
        userDto.setUserId(userId);
        userDto.setFirstName("Tom");
        userDto.setLastName("James");
        userDto.setEmail("test@test.com");
        userDto.setEncryptedPassword(encryptedPasword);
        userDto.setEmailVerificationStatus(Boolean.FALSE);
        userDto.setAddresses(getAddressesDto());
    }

    @Test
    final void getUser() {
        when(userService.getUserByUserId(anyString())).thenReturn(userDto);
        UserRest userRest = userController.getUser(userId);

        assertNotNull(userRest);
        assertEquals(userId, userRest.getUserId());
        assertEquals(userDto.getFirstName(), userRest.getFirstName());
        assertEquals(userDto.getLastName(), userRest.getLastName());
  //      assertTrue(userDto.getAddresses().size() == userRest.getAddresses().size());
    }


    // Mock addresses
    private List<AddressDto> getAddressesDto() {
        AddressDto addressDto = new AddressDto();
        addressDto.setType("shipping");
        addressDto.setCity("city1");
        addressDto.setCountry("Country1");
        addressDto.setPostalCode("ABC123");
        addressDto.setStreetName("123 Street name");

        AddressDto billingAddressDto = new AddressDto();
        billingAddressDto.setType("billling");
        billingAddressDto.setCity("city2");
        billingAddressDto.setCountry("Country1");
        billingAddressDto.setPostalCode("DEF123");
        billingAddressDto.setStreetName("123 OTHER Street name");

        List<AddressDto> addresses = new ArrayList<>();
        addresses.add(addressDto);
        addresses.add(billingAddressDto);
        return addresses;
    }

}
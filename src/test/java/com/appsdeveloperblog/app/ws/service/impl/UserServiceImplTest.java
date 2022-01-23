package com.appsdeveloperblog.app.ws.service.impl;

import com.appsdeveloperblog.app.ws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.io.entity.AddressEntity;
import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.io.repository.UserRepository;
import com.appsdeveloperblog.app.ws.shared.Utils;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDto;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    UserRepository userRepository;
    @InjectMocks    // Because UserServiceImpl is the class that we test, we put InjectMocks
    UserServiceImpl userService;
    @Mock
    Utils utils;
    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    String userId = "abcde";
    String encryptedPasword = "123456";
    UserEntity userEntity = new UserEntity();

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        // Creating a mock object
        userEntity.setId(1L);
        userEntity.setFirstName("Tom");
        userEntity.setUserId(userId);
        userEntity.setEncryptedPassword(encryptedPasword);
        userEntity.setAddresses(getAddressesEntity());
    }

    @Test
    final void testCreateUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(utils.generateAddressId(anyInt())).thenReturn("123456");
        when(utils.generateUserId(anyInt())).thenReturn(userId);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPasword);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // Creating an UserDto
        UserDto userDto = new UserDto();
        userDto.setAddresses(getAddressesDto());
        userDto.setFirstName("Tom");
        userDto.setLastName("James");
        userDto.setPassword(encryptedPasword);
        userDto.setEmail("test@test.com");

        UserDto storedUserDetails = userService.createUser(userDto);

        assertNotNull(storedUserDetails);
        assertNotNull(storedUserDetails.getUserId());
        assertEquals(userEntity.getFirstName(), storedUserDetails.getFirstName());
        assertEquals(userEntity.getLastName(), storedUserDetails.getLastName());
        assertEquals(storedUserDetails.getAddresses().size(), userEntity.getAddresses().size());
        verify(utils, times(storedUserDetails.getAddresses().size())).generateAddressId(30);
        verify(bCryptPasswordEncoder, times(1)).encode(encryptedPasword);
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void testGetUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
        UserDto userDto = userService.getUser("test@test.com");
        assertNotNull(userDto);
        assertEquals(1L, userDto.getId());
        assertEquals("Tom", userDto.getFirstName());
        assertEquals(userId, userDto.getUserId());
        assertEquals(encryptedPasword, userDto.getEncryptedPassword());
    }

    @Test
    final void testGetUser_UsernameNotFoundException() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        assertThrows(UsernameNotFoundException.class,
                () -> {
                    userService.getUser("test@test.com");
                }
        );
    }

    @Test
    final void testCreateUser_CreateUserServiceException() {
        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
        // Creating an UserDto
        UserDto userDto = new UserDto();
        userDto.setAddresses(getAddressesDto());
        userDto.setFirstName("Tom");
        userDto.setLastName("James");
        userDto.setPassword(encryptedPasword);
        userDto.setEmail("test@test.com");

        assertThrows(UserServiceException.class,
                () -> {
                    userService.createUser(userDto);
                }
        );
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

    private List<AddressEntity> getAddressesEntity() {
        List<AddressDto> addresses = getAddressesDto();
        //map AddressDto list into AddressEntity list
        Type listType = new TypeToken<List<AddressEntity>>() {}.getType();
        return new ModelMapper().map(addresses, listType);
    }




}
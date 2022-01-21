package com.appsdeveloperblog.app.ws.ui.controller;

import com.appsdeveloperblog.app.ws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.service.AddressService;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDto;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.request.RequestOperationName;
import com.appsdeveloperblog.app.ws.ui.model.request.UserDetailsRequestModel;
import com.appsdeveloperblog.app.ws.ui.model.response.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.internal.bytebuddy.description.method.MethodDescription;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("users")    // http://localhost:8080/mobile-app-ws/users
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private AddressService addressService;

    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest getUser(@PathVariable String id) {
        UserRest returnValue = new UserRest();
        UserDto userDto = userService.getUserByUserId(id);
        BeanUtils.copyProperties(userDto, returnValue);
        return returnValue;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws UserServiceException {
        UserRest returnValue = new UserRest();
        if (userDetails.getFirstName().isEmpty()) {
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
        }
        // UserDto userDto = new UserDto();
        // BeanUtils.copyProperties(userDetails, userDto);
        // Mapping Dto with Modelmapper
        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);
        UserDto createdUser = userService.createUser(userDto);
        BeanUtils.copyProperties(createdUser, returnValue);
        return returnValue;
    }

    @PutMapping(path = "/{id}", consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
        UserRest returnValue = new UserRest();
        UserDto userDto = new UserDto();
        // Copying the values from userDetails into an UserDto
        BeanUtils.copyProperties(userDetails, userDto);
        // Updating the user entity
        UserDto updatedUser = userService.updateUser(id, userDto);
        // Copying values from updatedUser into an UserRest object
        BeanUtils.copyProperties(updatedUser, returnValue);
        return returnValue;
    }

    @DeleteMapping(path = "/{id}",produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel deleteUser(@PathVariable String id) {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());
        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return returnValue;
    }

    @GetMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "limit", defaultValue = "5") int limit) throws UserServiceException {
        List<UserRest> returnValue = new ArrayList<>();
        List<UserDto> users = userService.getUsers(page, limit);
        if(users == null){
            throw new UserServiceException("User Exception");
        }
        for(UserDto dto : users){
            UserRest userRest = new UserRest();
            BeanUtils.copyProperties(dto, userRest);
            returnValue.add(userRest);
        }
        return returnValue;
    }

    // http://localhost:8080/mobile-app-ws/users/xxxxxxxxxxx/addresses
    @GetMapping(path = "/{id}/addresses", consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public List<AddressesRest> getUserAddresses(@PathVariable String id) throws UserServiceException {
        List<AddressesRest> returnValue = new ArrayList<>();
        // Getting the list of addresses by UserId
        List<AddressDto> addressDtos = addressService.getAddressesById(id);
        ModelMapper modelMapper = new ModelMapper();
        if (addressDtos != null && !addressDtos.isEmpty()) {
            Type listType = new TypeToken<List<AddressesRest>>() {
            }.getType();
            returnValue = modelMapper.map(addressDtos, listType);
        }
        return returnValue;
    }






}

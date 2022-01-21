package com.appsdeveloperblog.app.ws.service.impl;

import com.appsdeveloperblog.app.ws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.io.repository.UserRepository;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.Utils;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDto;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    Utils utils;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDto createUser(UserDto userDto) {
        // Cheking if the user already exists
        if(userRepository.findByEmail(userDto.getEmail()) != null){
            throw new RuntimeException("Record already exists;");
        }
        // Looping on the list of addresses of the given UserDto
        for (int i=0;i<userDto.getAddresses().size(); i++){
            AddressDto address = userDto.getAddresses().get(i);
            address.setUserDetails(userDto);
            address.setAddressId(utils.generateAddressId(30));
            userDto.getAddresses().set(i, address);
        }
        // Creating the User entity
        UserEntity userEntity = new UserEntity();
        // Copying the values from UserDto to UserEntity
        ModelMapper modelMapper = new ModelMapper();
        userEntity = modelMapper.map(userDto, UserEntity.class);

        // Generating userId and password
        String publicUserId = utils.generateUserId(36);
        userEntity.setUserId(publicUserId);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        // Saving the object to the DB
        UserEntity savedUserDetails = userRepository.save(userEntity);
        // Returning back the saved object as an UserDto
        UserDto returnValue = modelMapper.map(savedUserDetails, UserDto.class);
        return returnValue;
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) {
            throw new UsernameNotFoundException(email);
        }
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userEntity, userDto);
        return userDto;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) {
            throw new UsernameNotFoundException(email);
        }
        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
    }


    @Override
    public UserDto getUserByUserId(String userId) {
        UserDto userDto = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null) {
            throw new UsernameNotFoundException("User with the ID : " + userId + " not found.");
        }
        BeanUtils.copyProperties(userEntity, userDto);
        return userDto;
    }

    @Override
    public UserDto updateUser(String id, UserDto user) {
        UserDto returnValue = new UserDto();
        // Finding the entity to update
        UserEntity userEntity = userRepository.findByUserId(id);
        if (userEntity == null) {
            throw new UserServiceException("No record found");
        }
        // updating the User entity with the info from UserDto
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setEmail(user.getEmail());
        // Saving to the DB
        UserEntity updatedUserDetails = userRepository.save(userEntity);
        // Copying the updated entity into a UserDto and returning it
        BeanUtils.copyProperties(updatedUserDetails, returnValue);
        return returnValue;
    }

    @Override
    public void deleteUser(String userId) {
        // Finding the entity to delete
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null) {
            throw new UserServiceException("No record found");
        }
        userRepository.delete(userEntity);
    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {
        List<UserDto> returnValue = new ArrayList<>();
        //Avoiding pagination starts with 0:
        if (page > 0) {
            page -= 1;
        }
        Pageable pageableRequest = PageRequest.of(page, limit);
        Page<UserEntity> userEntityPage = userRepository.findAll(pageableRequest);
        List<UserEntity> users = userEntityPage.getContent();
        for (UserEntity userEntity : users) {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userEntity, userDto);
            returnValue.add(userDto);
        }
        return returnValue;
    }
}

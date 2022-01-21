package com.appsdeveloperblog.app.ws.service.impl;

import com.appsdeveloperblog.app.ws.io.entity.AddressEntity;
import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.io.repository.UserRepository;
import com.appsdeveloperblog.app.ws.service.AddressService;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    UserRepository userRepository;


    @Override
    public List<AddressDto> getAddressesById(String id) {
        if(userRepository.findByUserId(id) == null){
            throw new UsernameNotFoundException("User not found");
        }
        List<AddressDto> addressDtoList = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        UserEntity user = userRepository.findByUserId(id);
        if(!user.getAddresses().isEmpty() && user.getAddresses() != null){
            for (int i=0; i < user.getAddresses().size() ; i++){
                AddressDto addressDto = new AddressDto();
                addressDto = modelMapper.map(user.getAddresses().get(i), AddressDto.class);
                addressDtoList.add(addressDto);
            }
        }
        return addressDtoList;
    }
}

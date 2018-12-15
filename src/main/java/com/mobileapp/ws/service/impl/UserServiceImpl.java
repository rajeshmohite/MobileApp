package com.mobileapp.ws.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.mobileapp.ws.dto.UserDto;
import com.mobileapp.ws.io.entity.UserEntity;
import com.mobileapp.ws.repository.UserRepository;
import com.mobileapp.ws.service.UserService;
import com.mobileapp.ws.shared.Utils;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	Utils utils;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public UserDto createUser(UserDto dto) {

		if (userRepository.findByEmail(dto.getEmail()) != null)
			throw new RuntimeException("Record with this Email id exist!!!");

		UserEntity userEntity = new UserEntity();
		BeanUtils.copyProperties(dto, userEntity);

		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
		userEntity.setUserId(utils.generateUserId(30));
		UserEntity userEntity2 = userRepository.save(userEntity);

		UserDto returnUser = new UserDto();
		BeanUtils.copyProperties(userEntity2, returnUser);

		return returnUser;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

}

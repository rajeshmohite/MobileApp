package com.mobileapp.ws.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.mobileapp.ws.dto.AddressDTO;
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
	public UserDto createUser(UserDto user) {
		ModelMapper modelMapper = new ModelMapper();

		if (userRepository.findByEmail(user.getEmail()) != null)
			throw new RuntimeException("Record with this Email id exist!!!");

		for (int i = 0; i < user.getAddresses().size(); i++) {
			AddressDTO addressDTO = user.getAddresses().get(i);
			addressDTO.setUserDetails(user);
			addressDTO.setAddressId(utils.generateAddressId(30));
			user.getAddresses().set(i, addressDTO);
		}

		UserEntity userEntity = modelMapper.map(user, UserEntity.class);

		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userEntity.setUserId(utils.generateUserId(30));

		UserEntity userEntity2 = userRepository.save(userEntity);

		UserDto returnUser = modelMapper.map(userEntity2, UserDto.class);

		return returnUser;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity userEntity = userRepository.findByEmail(email);

		if (userEntity == null)
			throw new UsernameNotFoundException(email);

		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
	}

	@Override
	public UserDto getUser(String email) {
		UserEntity userEntity = userRepository.findByEmail(email);

		if (userEntity == null)
			throw new UsernameNotFoundException(email);

		UserDto returnUser = new UserDto();
		BeanUtils.copyProperties(userEntity, returnUser);

		return returnUser;
	}

	@Override
	public UserDto getUserByUserId(String userId) {
		UserDto returnUser = new UserDto();

		UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null)
			throw new UsernameNotFoundException("User with user ID : " + userId + " does not exists!");

		BeanUtils.copyProperties(userEntity, returnUser);

		return returnUser;
	}

	@Override
	public UserDto updateUser(String userId, UserDto userDto) {
		UserDto returnUser = new UserDto();

		UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null)
			throw new UsernameNotFoundException(userId);

		userEntity.setFirstName(userDto.getFirstName());
		userEntity.setLastName(userDto.getLastName());
		UserEntity updatedUserEntity = userRepository.save(userEntity);

		BeanUtils.copyProperties(updatedUserEntity, returnUser);

		return returnUser;
	}

	@Override
	public void deleteUser(String userId) {
		UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null)
			throw new UsernameNotFoundException(userId);

		userRepository.delete(userEntity);

	}

	@Override
	public List<UserDto> getUsers(int page, int limit) {
		List<UserDto> returnValue = new ArrayList<>();

		Pageable pageable = PageRequest.of(page, limit);
		Page<UserEntity> userPages = userRepository.findAll(pageable);
		List<UserEntity> users = userPages.getContent();

		for (UserEntity userEntity : users) {
			UserDto userDto = new UserDto();
			BeanUtils.copyProperties(userEntity, userDto);
			returnValue.add(userDto);
		}
		return returnValue;
	}

}

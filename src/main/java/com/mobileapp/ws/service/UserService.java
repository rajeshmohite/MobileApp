package com.mobileapp.ws.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.mobileapp.ws.dto.UserDto;

public interface UserService extends UserDetailsService {
	UserDto createUser(UserDto dto);

	UserDto getUser(String email);

	UserDto getUserByUserId(String userId);

	UserDto updateUser(String userId, UserDto userDto);

	void deleteUser(String userId);

	List<UserDto> getUsers(int page, int limit);
}

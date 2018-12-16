package com.mobileapp.ws.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mobileapp.ws.dto.UserDto;
import com.mobileapp.ws.exceptions.UserServiceException;
import com.mobileapp.ws.service.UserService;
import com.mobileapp.ws.ui.model.request.UserDetailsRequestModel;
import com.mobileapp.ws.ui.model.response.ErrorMessages;
import com.mobileapp.ws.ui.model.response.UserDetailsResponseModel;

@RestController
@RequestMapping("users")
public class UserController {

	@Autowired
	UserService userService;

	@GetMapping(path = "/{userId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public UserDetailsResponseModel getUser(@PathVariable String userId) {
		UserDetailsResponseModel returnUser = new UserDetailsResponseModel();

		UserDto userDto = userService.getUserByUserId(userId);
		BeanUtils.copyProperties(userDto, returnUser);

		return returnUser;
	}

	@PostMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, consumes = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public UserDetailsResponseModel createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
		UserDetailsResponseModel returnUser = new UserDetailsResponseModel();

		if (userDetails.getFirstName().isEmpty())
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

		// Below case is just to check how other exceptions can handle
		if (userDetails.getPassword().isEmpty())
			throw new NullPointerException("The Object is Null");

		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto);

		UserDto userDto2 = userService.createUser(userDto);
		BeanUtils.copyProperties(userDto2, returnUser);

		return returnUser;
	}

	@PutMapping
	public String updateUser() {
		return "update user";
	}

	@DeleteMapping
	public String deleteUser() {
		return "delete user";
	}

}

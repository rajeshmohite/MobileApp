package com.mobileapp.ws.controller;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mobileapp.ws.dto.UserDto;
import com.mobileapp.ws.exceptions.UserServiceException;
import com.mobileapp.ws.service.UserService;
import com.mobileapp.ws.ui.model.request.UserDetailsRequestModel;
import com.mobileapp.ws.ui.model.response.ErrorMessages;
import com.mobileapp.ws.ui.model.response.OperationStatusModel;
import com.mobileapp.ws.ui.model.response.RequestOperationName;
import com.mobileapp.ws.ui.model.response.RequestOperationStatus;
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
		ModelMapper modelMapper = new ModelMapper();

		if (userDetails.getFirstName().isEmpty())
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

		// Below case is just to check how other exceptions can handle
		if (userDetails.getPassword().isEmpty())
			throw new NullPointerException("The Object is Null");

		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		UserDto userDto2 = userService.createUser(userDto);
		UserDetailsResponseModel returnUser = modelMapper.map(userDto2, UserDetailsResponseModel.class);
		return returnUser;
	}

	@PutMapping(path = "/{userId}", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE })
	public UserDetailsResponseModel updateUser(@PathVariable String userId,
			@RequestBody UserDetailsRequestModel userDetails) {

		UserDetailsResponseModel returnUser = new UserDetailsResponseModel();

		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto);

		UserDto userDto2 = userService.updateUser(userId, userDto);
		BeanUtils.copyProperties(userDto2, returnUser);

		return returnUser;
	}

	@DeleteMapping(path = "/{userId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public OperationStatusModel deleteUser(@PathVariable String userId) {
		OperationStatusModel returnObj = new OperationStatusModel();
		returnObj.setOperationName(RequestOperationName.DELETE.name());

		userService.deleteUser(userId);

		returnObj.setOperationResult(RequestOperationStatus.SUCCESS.name());
		return returnObj;
	}

	@GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public List<UserDetailsResponseModel> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "limit", defaultValue = "2") int limit) {

		List<UserDetailsResponseModel> returnUsers = new ArrayList<>();

		List<UserDto> users = userService.getUsers(page, limit);

		for (UserDto userDto : users) {
			UserDetailsResponseModel userModel = new UserDetailsResponseModel();
			BeanUtils.copyProperties(userDto, userModel);
			returnUsers.add(userModel);
		}

		return returnUsers;
	}
}

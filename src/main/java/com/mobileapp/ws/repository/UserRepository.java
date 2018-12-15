package com.mobileapp.ws.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mobileapp.ws.io.entity.UserEntity;

@Repository
public interface UserRepository extends CrudRepository<UserEntity,Long>{
	UserEntity findByEmail(String email);
}

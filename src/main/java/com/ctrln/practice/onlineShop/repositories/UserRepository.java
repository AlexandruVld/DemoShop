package com.ctrln.practice.onlineShop.repositories;

import com.ctrln.practice.onlineShop.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}

package com.heji.server.data.repository;

import com.heji.server.data.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<User, Integer> {

}

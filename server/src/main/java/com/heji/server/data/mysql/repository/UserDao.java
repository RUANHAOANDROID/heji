package com.heji.server.data.mysql.repository;

import com.heji.server.data.mysql.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<User, Integer> {

}

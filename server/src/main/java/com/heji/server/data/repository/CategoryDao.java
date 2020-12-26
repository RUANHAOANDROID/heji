package com.heji.server.data.repository;

import com.heji.server.data.Category;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CategoryDao extends JpaRepository<Category, Integer> {

    @Query
    List<Category> findAllByTypeAndLevel(int type, int level);

    /**
     * 该类型名是否存在
     *
     * @param name
     * @return
     */
    @Query
    boolean existsDistinctByName(String name);

    @Transactional
    @Modifying
    @Query("update Category c set c.type=:type,c.level =:level where c.name = :name")
    int updateCategory(@Param("type") int type, @Param("level") int level, @Param("name") String name);

    @Transactional
    @Query
    void deleteCategoryByName(String name);
}

package com.example.clubmanagement.mapper;

import com.example.clubmanagement.entity.Club;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ClubMapper {
    //@Select("SELECT * FROM club")
    List<Club> findAll();

    //@Select("SELECT * FROM club WHERE id = #{id}")
    Club findById(Integer id);

    //@Insert("INSERT INTO club(name, logo, description) VALUES(#{name}, #{logo}, #{description})")
    //@Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Club club);

    //@Update("UPDATE club SET name = #{name}, logo = #{logo}, description = #{description} WHERE id = #{id}")
    void update(Club club);

    //@Delete("DELETE FROM club WHERE id = #{id}")
    void delete(Integer id);
}
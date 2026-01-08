package com.example.clubmanagement.mapper;

import com.example.clubmanagement.entity.Member;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MemberMapper {
    //@Select("SELECT * FROM member")
    List<Member> findAll();

    //@Select("SELECT * FROM member WHERE id = #{id}")
    Member findById(Integer id);

    //@Insert("INSERT INTO member(name, clubId) VALUES(#{name}, #{clubId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Member member);

    //@Update("UPDATE member SET name = #{name}, clubId = #{clubId} WHERE id = #{id}")
    void update(Member member);

    //@Delete("DELETE FROM member WHERE id = #{id}")
    void delete(Integer id);
}
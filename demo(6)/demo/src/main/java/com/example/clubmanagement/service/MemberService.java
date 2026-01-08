package com.example.clubmanagement.service;

import com.example.clubmanagement.mapper.MemberMapper;
import com.example.clubmanagement.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    @Autowired
    private MemberMapper memberMapper;

    public List<Member> findAll() {
        return memberMapper.findAll();
    }

    public Member findById(Integer id) {
        return memberMapper.findById(id);
    }

    public void save(Member member) {
        if (member.getId() == null) {
            memberMapper.insert(member);
        } else {
            memberMapper.update(member);
        }
    }

    public void delete(Integer id) {
        memberMapper.delete(id);
    }
}
package com.example.clubmanagement.controller;

import com.example.clubmanagement.entity.Member;
import com.example.clubmanagement.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @GetMapping
    public List<Member> findAll() {
        return memberService.findAll();
    }

    @GetMapping("/{id}")
    public Member findById(@PathVariable Integer id) {
        return memberService.findById(id);
    }

    @PostMapping
    public void save(@RequestBody Member member) {
        memberService.save(member);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        memberService.delete(id);
    }
}
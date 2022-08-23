package spring.corelecture;

import spring.corelecture.member.Grade;
import spring.corelecture.member.Member;
import spring.corelecture.member.MemberService;
import spring.corelecture.member.MemberServiceImpl;

public class MemberApp {
    public static void main(String[] args) {
        MemberService memberService = new MemberServiceImpl();
        Member member = new Member(1L,"memberA", Grade.VIP);
        memberService.join(member);
        Member findMember = memberService.findMember(1L);
        System.out.println("findMember = " + member.getName());
        System.out.println("find Member = " + findMember.getName());

    }
}

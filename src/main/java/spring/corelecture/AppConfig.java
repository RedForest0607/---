package spring.corelecture;

import spring.corelecture.discount.DiscountPolicy;
import spring.corelecture.discount.FixDiscountPolicy;
import spring.corelecture.member.MemberService;
import spring.corelecture.member.MemberServiceImpl;
import spring.corelecture.member.MemoryMemberRepository;
import spring.corelecture.order.OrderService;
import spring.corelecture.order.OrderServiceImpl;

public class AppConfig {
    public MemberService memberService() {
        return new MemberServiceImpl(MemberRepository());
    }

    private MemoryMemberRepository MemberRepository() {
        return new MemoryMemberRepository();
    }

    public OrderService orderService() {
        return new OrderServiceImpl(MemberRepository(),discountPolicy());
    }

    public DiscountPolicy discountPolicy() {
        return new FixDiscountPolicy();
    }
}
//구현 객체를 전부 생성하고, 각 인스턴스의 참조를 생성자를 통해 주입해준다.

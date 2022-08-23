package spring.corelecture.discount;

import spring.corelecture.member.Member;

public interface DiscountPolicy {
    int discount(Member member, int price);
}

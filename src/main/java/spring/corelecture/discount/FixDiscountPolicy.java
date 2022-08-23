package spring.corelecture.discount;

import spring.corelecture.member.Grade;
import spring.corelecture.member.Member;

public class FixDiscountPolicy implements DiscountPolicy{

    private int discountFIxAmount = 1000;
    @Override
    public int discount(Member member, int price) {
        if (member.getGrade() == Grade.VIP) {
            return discountFIxAmount;
        }
        return 0;
    }
}

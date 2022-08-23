package spring.corelecture.order;

import spring.corelecture.discount.DiscountPolicy;
import spring.corelecture.discount.FixDiscountPolicy;
import spring.corelecture.discount.RateDiscountPolicy;
import spring.corelecture.member.Member;
import spring.corelecture.member.MemberRepository;
import spring.corelecture.member.MemoryMemberRepository;

public class OrderServiceImpl implements OrderService{


    private final MemberRepository memberRepository = new MemoryMemberRepository();
    /*
    private final DiscountPolicy discountPolicy = new RateDiscountPolicy(); //추상이 아닌 구현에도 의존하는 모습 -> DIP 위반
    */                                                                  //DiscountPolicy뿐만 아니라, 현재 OrderService코드도 고쳐야함 -> OCP 위반
    private DiscountPolicy discountPolicy;

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member, itemPrice);

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }
}

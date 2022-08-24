package spring.corelecture.order;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spring.corelecture.AppConfig;
import spring.corelecture.member.Grade;
import spring.corelecture.member.Member;
import spring.corelecture.member.MemberService;
import spring.corelecture.member.MemberServiceImpl;

public class OrderServiceTest {
    @BeforeEach
    public void beforeEach() {
        AppConfig appConfig = new AppConfig();
        memberService = appConfig.memberService();
        orderService = appConfig.orderService();
    }
    MemberService memberService;
    OrderService orderService;

    @Test
    void createOrder(){
        Long memberId = 1L;
        Member member = new Member(memberId, "memberA", Grade.VIP);
        memberService.join(member);

        Order order = orderService.createOrder(memberId, "itemA", 10000);
        Assertions.assertThat(order.getDiscountPrice()).isEqualTo(1000);
    }
}

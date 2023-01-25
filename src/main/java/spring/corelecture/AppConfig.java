package spring.corelecture;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import spring.corelecture.discount.DiscountPolicy;
import spring.corelecture.discount.RateDiscountPolicy;
import spring.corelecture.member.MemberService;
import spring.corelecture.member.MemberServiceImpl;
import spring.corelecture.member.MemoryMemberRepository;
import spring.corelecture.order.OrderService;
import spring.corelecture.order.OrderServiceImpl;

@Configuration
public class AppConfig {

    @Bean
    public MemberService memberService() {
        return new MemberServiceImpl(MemberRepository());
    }

    @Bean
    public MemoryMemberRepository MemberRepository() {
        return new MemoryMemberRepository();
    }

    @Bean
    public OrderService orderService() {
        return new OrderServiceImpl(MemberRepository(), discountPolicy());
    }

    @Bean
    public DiscountPolicy discountPolicy() {
        // return new FixDiscountPolicy(); 구성 영역에서 갈아끼우듯이 바꾸면 주입이 바뀜
        return new RateDiscountPolicy();
    }
}
//구현 객체를 전부 생성하고, 각 인스턴스의 참조를 생성자를 통해 주입해준다.

package spring.corelecture.singleton;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import spring.corelecture.AppConfig;
import spring.corelecture.member.MemberRepository;
import spring.corelecture.member.MemberServiceImpl;
import spring.corelecture.order.OrderServiceImpl;

public class ConfigurationSingletonTest {

    @Test
    @DisplayName("@Configuration에서의 싱글톤")
    void configurationSingleton() {

        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

        MemberServiceImpl memberService = ac.getBean("memberService", MemberServiceImpl.class);
        OrderServiceImpl orderService = ac.getBean("orderService", OrderServiceImpl.class);

        MemberRepository memberRepository1 = memberService.getMemberRepository();
        MemberRepository memberRepository2 = orderService.getMemberRepository();

        Assertions.assertThat(memberRepository1).isSameAs(memberRepository2);
    }

    @Test
    void configurationTest() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
        AppConfig bean = ac.getBean(AppConfig.class);

        System.out.println("bean = " + bean.getClass()); //bean = class spring.corelecture.AppConfig$$EnhancerBySpringCGLIB$$dac20487 -> 부모타입인 AppConfig를 조회했기 때문에, 상속받은 자식인 @CGLIB도 튀어나옴
    }
}

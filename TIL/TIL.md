# 예제 만들기

### 비즈니스 요구사항

- 회원
    - 회원 가입과 조회
    - VIP와 일반 등급
    - 자체 DB, 외부 시스템을 활용할 수 있음
- 주문과 할인 정책
    - 상품 주문
    - 등급에 따른 할인 정책
    - 모든 VIP는 1000원을 할인해주는 고정 금액 할인
    - 할인 정책은 변경 가능성이 높아, 할인을 적용하지 않을 가능성도 존재함.
    

> 아래의 부분은 스프링이 없는 순수 자바 수준으로 코딩된 부분이다.
> 

![Untitled](https://user-images.githubusercontent.com/74250270/186934376-18a92204-dc25-4532-b418-64c6d208d220.png)


DB나 외부저장소가 정해지지 않아서 메모리로 먼저 테스트하고, 구현제를 갈아끼우는 방식으로 개발할 예정 이다.

![Untitled 1](https://user-images.githubusercontent.com/74250270/186934422-c80bea91-2f90-4db5-b5ae-5bf1b16d1962.png)

### 회원 도메인의 설계상 문제

memberService가 MemberRepository의 추상구조화 MemoryMemberRepository의 구현 구조 두 부분에 모두 의존하는 방식으로 설계 되어 있다.

![Untitled 2](https://user-images.githubusercontent.com/74250270/186934561-e1f4a9f2-5ea1-455d-a07a-3aa3e29309e8.png)


할인 정책을 역할에다가 끼워넣는 형태의 객체들을 조립하는 방식으로 생성하여 좀 더 편하고, 유연하게 대처할 수 있다.

![Untitled 3](https://user-images.githubusercontent.com/74250270/186934480-bbd857c8-8122-4ea9-9b67-fc52f0d3f8a5.png)


협력관계에 대해서 그대로 재사용할 수 있는 형태의 설계가 나오게 된다.

```java
public class OrderServiceImpl implements OrderService{
		private final DiscountPolicy discountPolicy = new RateDiscountPolicy();
}
```

다음과 같은 코드는, OrderService에 다른 할인 정책을 적용하기 위해서, 의존하고 있는 할인정책의 구현부 `DIP위반`를 바꾸면서 동시에 OrderService의 코드도 변경해야 한다. `OCP위반` 이러한 방식은 전혀 객체지향을 고려한 설계가 아니라고 할 수 있다.

### 관심사를 분리하자

각각의 구현체는 스스로의 기능 구현에만 집중해야 한다. 각 구현이 자신의 내부에서 사용되는 추상에 대해서 구현까지 생각하면 안된다. 어떤 구현체를 사용할지에 대해서는 구현 객체를 생성하고 연결하는 `AppConfig`를 필요로 한다

![Untitled 4](https://user-images.githubusercontent.com/74250270/186934636-cdc6d976-06db-41cc-a46e-3bd10021d6cb.png)


다음과 같이 AppConfig를 통해서 인스턴스를 생성하고 생성자를 통해서 주입해준다.

![Untitled 5](https://user-images.githubusercontent.com/74250270/186934682-826250c1-597b-48ff-b628-3bec9ddcb0bc.png)


다음과 같이 `memberServiceImpl` 입장에서 보게 되면 외부에서 의존관계를 `주입`하는 모양새가 되기 때문에 의존관계 주입이라고 한다.

각각의 파일에서 OrderService나 MemberService를 필요로 하다면, appConfig에서 주입 받으면 된다.

DIP를 지키게 된다

### AppConfig 리팩토링

```java
public class AppConfig {
		public MemberService memberService() {
				return new MemberServiceImpl(new MemoryMemberRepository());
    }

		public OrderService orderService() {
				return new OrderServiceImpl(new MemoryMemberRepository(),new FixDiscountPolicy());
    }
}
```

다음과 같은 경우, 중복이 존재하며, 역할과 구현이 보이지 않는다.

```java
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
```

`new MemoryMemberRepository()`의 중복이 제거 되었으며, `AppConfig`의 역할과 구현이 한눈에 보이며, 각 서비스와 repo, 그리고 policy가 어떤걸 사용하는지 알 수 있게된다.

실질적으로 서비스로 사용하는 사용영역과 이러한 사용영역을 구성하기위한 구성영역으로 나뉘어서 관리할 있다.

### 객체 지향 설계의 5가지 원칙 적용

> SRP, DIP, OCP
> 
- SRP
    
    클라이언트는 객체 생성과 연결, 실행을 전부 책임지고 있었다. → `AppConfig`가 생성과 연결을 담당하고 클라이언트 객체는 실행만 담당하도록 조정
    
- DIP
    
    추상화에 의존해야하며, 구체화에 의존하면 안된다. → `FixDiscountPolicy`에 의존하는 모습을 `AppConfig`가 클라이언트 코드에 의존관계를 주입하는 모습으로 바꾸며 구체화 의존 문제를 해결했다.
    
- OCP
    
    확장에는 열려 있으나 변경에는 닫혀 있어야 한다. → 어프리케이션을 사용 영역과 구성 영역으로 나뉘고, `AppConfig`가 클라이언트 코드로 외부에서 주입하므로 변경하지 않아도 된다.
    

### 정적인 클래스 의존 관계

> `OrderServiceImpl` 이 `MemberRepository`와 `DiscountPolicy`에 의존한다는 사실은 알지만 실제로 어떤 객체가 주입될 지 알 수 없다.
> 

![Untitled 6](https://user-images.githubusercontent.com/74250270/186934730-ac1d1eb2-313b-40be-9bb4-72fe5c7323e2.png)

### 동적인 객체 인스턴스 의존 관계

실행 시점에서 객체의 각 인스턴스들이 어떤식으로 의존하는지 알 수 있다.

## 객체 다이어그램

![Untitled 7](https://user-images.githubusercontent.com/74250270/186934785-131f44ca-ad63-446d-81f1-fe5d70848600.png)

의존관계 주입을 사용해서 동적인 관계를 바꾸지 않고, 동적인 객체 인스턴스 의존 관계를 바꿀 수 있다.

→ 각 클라이언트 객체를 손대지 않고도 의존을 바꿀 수 있다.

### IoC 컨테이너 (=DI 컨테이너)

- AppConfig처럼 객체를 생성하고 관리해주는 것을 **IoC 컨테이너(DI 컨테이너)**라고 한다.
- 오브젝트 팩토리 혹은 어샘블리라고도 한다.

### 스프링 빈의 조회
스프링의 대 원칙 *부모타입의 빈을 조회하면 자식 타입도 함께 조회한다*  
최고 부모 타입인 Object를 조회하면 Spring에 등록된 모든 bean이 조회된다.

![img](https://user-images.githubusercontent.com/74250270/215919841-10cc42ed-5dfb-42c7-b29b-951fc9b9b23f.png)

- BeanFactory
  스프링의 최상위 컨테이너로 스프링 빈을 관리하고 조회하는 역할을 담당한다.
- ApplicationContext
  BeanFactory의 거의 모든 기능을 상속 받아서 제공한다.

### 스프링 BeanDefinition
스프링의 설정 (JavaCode, XML, ApplicationContext)의 다양한 형식 지원은 BeanDefinition이라는 추상화를 통해서 이러한 설정을 지원하는 것이다.
![img](https://user-images.githubusercontent.com/74250270/215920423-22afbe18-79c5-4175-a049-cba82d3fcb8d.png)
스프링 컨테이너는 설정파일의 형식과는 관계없이 BeanDefinition만 알면 Bean들을 관리할 수 있게 된다.

![img](https://user-images.githubusercontent.com/74250270/215920385-7dcdaa41-33a7-45f1-9903-cd74d2fc7a18.png)
다음처럼 메타정보를 읽고, 생성하는 각각의 구현체가 존재하기 떄문에, ApplicationContext가 다양한 설정 정보들을 읽어들일 수 있는 것이다.

BeanDefinition에 관해서는 너무 구체적으로 아는 것이 힘들다면, 위와같은 추상화의 흐름을 이해하고, BeanDefinition이 어떤 방식으로 ApplicationContext에 등록되는지를 알고 있으면 된다.

### 싱글톤 패턴
웹어플리케이션에서 싱글톤 패턴의 필요성

```java
public class SingletonObject {
  private static final SingletonObject singletonObject = new SingletonObject();

  //public을 통해서 생성된 객체를 가져올 수 있지만
  public static SingletonObject getInstance() {
    return singletonObject;
  }

  // 생성자는 감춰둔다
  private SingletonObject() {
  }
}
```


## 싱글톤 패턴의 문제점
- 구현에 코드가 많이 들어간다.
- 의존관계상 구체 클래스에 의존한다 -> DIP 위반, OCP 위반
- 테스트가 어렵다
- 내부속성 변경이나 초기화가 어렵다
- 자식클래스 생성이 어렵다  
  => 유연성이 떨어진다
- 안티패턴이라고도 불린다

## 스프링에서의 싱글톤 패턴
스프링 컨테이너는 각각의 빈이 싱글톤 패턴을 적용하지 않아도 싱글톤으로 객체의 인스턴스를 관리한다.  
스프링 컨테이너가 싱글톤 객체를 생성하고 관리하는 `싱글톤 레지스트리` 방식으로 관리를 하는데, DIP 위반, OCP 위반, 테스트, private가 없어도 싱글톤 사용이 가능하다.

## 싱글톤 사용 시 주의사항
> 싱글톤은 항상 stateless 해야한다.  

싱글톤의 특성상 하나의 객체 인스턴스가 공유되기 때문에 내부적으로 상태가 존재하는 필드가 있을 경우, 외부의 호출에 따라서 값이 달라질 위험이 있다.
따라서 싱글톤의 내부는 항상 stateless로 상태가 없도록 코딩해야지 공유자원에 대한 문제가 생기지 않는다.

## @Configuration에서 싱글톤의 특징
설정 내부에서는 여러번 new로 호출하지만 실제로 Bean이 생성되는건 단 한번뿐이다.
```java
@Configuration
public class AppConfig {

    @Bean
    public MemberService memberService() {
        return new MemberServiceImpl(MemberRepository()); //MemberRepositry 호출
    }

    @Bean
    public MemoryMemberRepository MemberRepository() {
        return new MemoryMemberRepository(); //MemberRepositry 호출
    }

    @Bean
    public OrderService orderService() {
        return new OrderServiceImpl(MemberRepository(), discountPolicy()); //MemberRepositry 호출
    }
    
  ...
```

```logcatfilter
실제로는

MemberServiceImpl
MemoryMemberRepository
OrderServiceImpl

한번씩만 호출
```
>@Configuration은 CGLIB을 통해서 싱글톤을 보장한다  

해당하는 어노테이션을 사용하는 Config파일은, 실제로 스프링이 직접 가져다 쓰지 않고, CGLIB을 통해서 프록시 객체를 사용하여서,
내부적으로 Bean이 이미 생성되어 있는지를 확인하고 생성하기 때문에 싱글톤이 보장된다.
![img](https://user-images.githubusercontent.com/74250270/215931514-53bb0921-4a8e-439c-aaae-7322d956440f.png)
따라서 내부적으로는 다른 객체가 호출되고, 이를 통해서 추가적인 다른코드 없이 싱글톤을 보장하게 된다.  
만약 어노테이션을 `@Bean`으로 바꾸게 될 경우, 순수한 객체 인스턴스가 가져와지면서, 위의 코드에서도 세번 호출되게 된다.

## Component Scan

컴포넌트 스캔 지금까지의 스프링 빈을 자동 등록할때에는 자바 코드의 `@Bean`이나 XML파일을 통해서 설정 정보를 직접 저장하는 방식을 택헀다.
-> 일일이 설정정보를 등록하는 문제를 해결하기 위해서 스프링빈의 설정 정보가 없어도 자동으로 설정정보를 등록하는 `ComponentScan`을 제공한다.
의존관계도 자동으로 주입하는 `@Autowired`라는 기능도 제공한다.

#### @Autowired
생성자에 `@Autowired`를 지정하면 스프링 컨테이너가 자동으로 해당 스프링 빈을 찾아서 주입한디  
이때 기본적으로 같은 타입을 찾아서 주입한다.  
`getBean(MemberREpository.class)`와 동일하다고 생각하면 된다.
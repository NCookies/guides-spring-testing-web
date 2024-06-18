**(아래 링크의 문서 내용을 번역한 내용입니다.)** </br>
https://spring.io/guides/gs/testing-web

이 가이드는 Spring 애플리케이션을 생성한 다음 JUnit으로 테스트하는 과정을 안내한다.

## 무엇을 할 것인가

간단한 Spring 애플리케이션을 만들고 이를 Junit으로 테스트 할 것이다. 

애플리케이션의 개별 클래스에 대한 단위 테스트를 어떻게 작성하고 실행할지 이미 알고 있을 것이다.

그래서 이 가이드에서는 Spring Test와 Spring Boot 기능들을 사용하여 

Spring과 코드 간의 상호 작용을 테스트하는 데 집중할 것이다.

애플리케이션 컨텍스트를 성공적으로 불러오고 이를 테스트하는 것부터 시작하고,

이어서 Spring의 `MockMvc`를 사용하여 웹 계층만을 테스트하는 것을 해볼 것이다. 

## 필요한 요소

- 약 15분
- 사용할 에디터 또는 IDE
- Java 1.8 또는 그 이후 버전
- Gradle 7.5+ 또는 Maven 3.5+

## 프로젝트 시작하기

가이드에서 제공하는 예제 코드를 다운로드 해서 사용하거나, https://start.spring.io 에서 프로젝트를 생성할 수 있다.
예제 코드의 링크는 다음과 같다. :

https://github.com/spring-guides/gs-testing-web.git

## 간단한 애플리케이션 만들기

Spring 애플리케이션에 새로운 컨트롤러를 생성한다. 

이 예제에서는 HTTP 요청의 메소드를 따로 구분하지 않고 있다.

`@GetMapping`이나 `@RequestMapping(method=GET)`으로 매핑 범위를 좁힐 수 있다.

```java
package com.example.testingweb;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @RequestMapping("/")
    public @ResponseBody String greeting() {
        return "Hello, World";
    }

}
```

## 애플리케이션 실행

Spring Initializer는 main() 메소드가 있는 애플리케이션 클래스를 만든다. 

해당 가이드에서는 이 클래스를 수정하지 않는다.

```java
package com.example.testingweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestingWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestingWebApplication.class, args);
    }
}
```

`@SpringBootApplication`은 아래와 같은 어노테이션을 모두 포함하는 편의성 어노테이션이다.
- [`@Configuration`](https://docs.spring.io/spring-framework/reference/core/beans/java/configuration-annotation.html) : 애플리케이션 컨텍스트에 대한 Bean 정의의 소스(출처)라고 클래스에 태그를 지정한다. 즉, 해당 어노테이션이 달린 클래스가 Spring 컨테이너에 빈을 제공하는 역할을 한다는 점을 강조한다. Spring 애플리케이션의 설정 파일을 정의하는 데 사용된다.
- `@EnableAutoConfiguation` : 클래스 경로 설정, 다른 Bean들 및 다양한 속성 설정을 기반으로 Bean 추가를 시작하도록 Spring Boot에 지시한다.
- `@EnableMvc` : 애플리케이션을 웹 애플리케이션으로 플래그 지정하고, `DispatcherServlet` 설정과 같은 주요 동작을 활성화한다. Spring Boot는 클래스 경로에서 `spring-webmvc`를 발견하면 자동으로 추가한다.
- `@ComponentScan` : 어노테이션이 달린 `TestingWebApplication` 클래스가 있는 패키지(`com.example.testingweb`)에서 다른 컴포넌트, 설정들, 그리고 서비스들을 찾도록 Spring에 지시한다.

main() 메소드는 애플리케이션을 실행하기 위한 Spring Boot의 `SpringApplication.run()` 메소드를 사용한다. 

XML 파일이나 코드는 전혀 사용하지 않는다.

애플리케이션은 순수 100% Java로 작성되었으며, 개발자는 기본 설정 및 구성, 인프라스트럭처 구성 등을 신경 쓸 필요가 전혀 없다.

Spring Boot가 이 모든 것을 처리한다.

## 애플리케이션 테스트

이제 애플리케이션이 실행되고 있고, 우리는 그걸 테스트할 수 있다. 

홈페이지는 `http://localhost:8080`에서 볼 수 있다.

그러나 프로그램을 수정했을 때 정상적으로 동작하는지 확신을 더 가지려면, 테스트를 자동화할 수 있다. 

참고로 Spring Boot는 당신이 애플리케이션을 테스트할 것이라고 가정하기 때문에 

필요한 관련 의존성을 빌드 파일(`build.gradle` 또는 `pom.xml`)에 추가해둔다.

### @SpringBootTest 어노테이션 사용 예시

가장 먼저 할 수 있는 일은 애플리케이션 컨텍스트를 시작할 수 없는 경우 실패하는 간단한 온전성 검사 테스트를 작성하는 것이다.

아래는 그 예제 코드다.

```java
package com.example.testingweb;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TestingWebApplicationTests {

	@Test
	void contextLoads() {
	}

}
```

`@SpringBootTest` 어노테이션은 Spring Boot가 기본 구성 클래스(Ex. @SpringBootApplication이 포함된 클래스)를 찾고, 이를 사용하여 Spring 애플리케이션 컨텍스트를 시작하도록 지시한다.

컨텍스트가 컨트롤러를 생성했음을 확신하려면 아래 예제(`SmokeTest.java`)와 같이 assertion을 추가할 수 있다.

```java
package com.example.testingweb;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SmokeTest {

	@Autowired
	private HomeController controller;

	@Test
	void contextLoads() throws Exception {
		assertThat(controller).isNotNull();
	}
}
```

스프링은 `@Autowired` 어노테이션을 해석하고, 테스트 메소드가 실행되기 전에 controller를 주입한다.

```
Spring Test 지원의 좋은 기능은 애플리케이션 컨텍스트가 테스트 간에 캐싱된다는 것이다.

이렇게 하면 테스트 케이스에 여러 메소드가 있거나 동일한 구성을 가진 여러 테스트 케이스가 있는 경우, 

애플리케이션을 한 번만 시작하는데 드는 비용이 발생한다.

`@DirtiesContext` 어노테이션을 사용해 캐싱을 제어할 수 있다.
```

### 실제 서비스 연결 및 테스트

애플리케이션이 온전한 상태인지 확인하는 것은 좋지만, 애플리케이션의 동작을 확인하는 몇 가지 테스트도 작성해야 한다.

이를 위해서 애플리케이션을 시작하고, 연결을 수신한 다음 (실제 서비스와 마찬가지로) HTTP request와 확인 response를 받을 수 있다.

```java
package com.example.testingweb;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class HttpRequestTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void greetingShouldReturnDefaultMessage() throws Exception {
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/",
				String.class)).contains("Hello, World");
	}
}
```

`SpringBootTest.WebEnvironment.RANDOM_PORT`는 서버를 랜덤 포트로 시작하고, `@LocalServerPort`로 포트를 주입한다.

`@LocalServerPort`는 테스트 환경에서 포트 충돌을 방지하기 위해 사용한다.

또한 스프링 부트는 자동으로 `TestRestTemplate`를 제공한다. 마찬가지로 `@Autowired`를 사용해 주입된다.

### `MockMvc` 사용해서 서버 시작하지 않고 테스트하기

또 다른 유용한 접근 방식은 서버를 전혀 시작하지 않고 그 아래 계층만 테스트하는 것이다. 

여기서 스프링은 들어오는 HTTP 요청을 처리하고 이를 컨트롤러에 전달한다.

이 방식은 거의 모든 전체 스택이 사용되며, 실제 HTTP 요청을 처리하는 것과 똑같은 방식으로 코드가 호출되지만, **서버를 시작하는 비용은 들지 않는다.**

그렇게 하려면 스프링의 `MockMvc`를 사용하고 테스트 케이스에 `@AutoConfigureMockMvc` 어노테이션을 사용해 주입되도록 요청해야 한다.

```java
package com.example.testingweb;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class TestingWebApplicationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void shouldReturnDefaultMessage() throws Exception {
		this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(containsString("Hello, World")));
	}
}
```

### `@WebMvcTest` 사용해서 필요한 클래스만 인스턴트화 하기

이 테스트의 경우 모든 스프링 애플리케이션 컨텍스트가 서버 없이 실행되었다. `@WebMvcTest`를 사용해 오직 web 레이어만으로 테스트 범위를 좁혔다.

```
@WebMvcTest
include::complete/src/test/java/com/example/testingweb/WebLayerTest.java
```

```java
package site.ncookie.testingweb;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HomeController.class)
public class WebLayerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnDefaultMessage() throws Exception {
        this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello World")));
    }
}
```

테스트 assertion은 이전 케이스와 똑같다. 

하지만 여기서는 스프링 부트가 전체 컨텍스트가 아닌 웹 계층만 인스턴트화한다. 

예를 들어, `@WebMvcTest(HomeController.class)`라고 지정하여 하나만 인스턴트화 되도록 요청할 수 있다.

### `Mockito`로 서비스 주입하여 테스트하기

지금까지는 다른 의존성이 없는 단순한 컨트롤러인 `HomeController`를 사용했다. 

인사말을 저장하기 위한 추가 구성 요소를 도입하여(아마도 새 컨트롤러에) 더욱 현실적으로 만들 수 있다.

```java
package com.example.testingweb;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class GreetingController {

	private final GreetingService service;

	public GreetingController(GreetingService service) {
		this.service = service;
	}

	@RequestMapping("/greeting")
	public @ResponseBody String greeting() {
		return service.greet();
	}

}
```

greeting 서비스도 만들어준다.

```java
package com.example.testingweb;

import org.springframework.stereotype.Service;

@Service
public class GreetingService {
	public String greet() {
		return "Hello, World";
	}
}
```

`GreetingService`를 생성하면 스프링에서 컨트롤러에에 자동으로 서비스 의존성을 주입한다.

```java
package com.example.testingweb;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(GreetingController.class)
class WebMockTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private GreetingService service;

	@Test
	void greetingShouldReturnMessageFromService() throws Exception {
		when(service.greet()).thenReturn("Hello, Mock");
		this.mockMvc.perform(get("/greeting")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(containsString("Hello, Mock")));
	}
}
```

`@MockBean`으로 `GreetingService`를 위한 mock을 생성하고 주입한다.</br> 
(이렇게 해주지 않으면 애플리케이션 컨텍스트가 시작되지 않는다.) 

그리고 `Mockito`를 사용해 기대값을 설정할 수 있다.

## 요약

이렇게 Spring 애플리케이션을 개발하고 이를 JUnit 및 Spring MockMvc로 테스트했으며,

Spring Boot를 사용하여 웹 계층을 격리하고 특정 애플리케이션 컨텍스트를 로드했다.

## 링크

- [Testing the Web Layer](https://spring.io/guides/gs/testing-web)
- [GitHub Repository](https://github.com/spring-guides/gs-testing-web)

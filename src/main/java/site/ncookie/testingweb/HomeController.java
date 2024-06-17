package site.ncookie.testingweb;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    // GET, PUT, POST 등의 메소드를 별도로 지정하지 않음
    // 모든 종류의 HTTP 요청을 받을 수 있다.
    @RequestMapping("/")
    public @ResponseBody String greeting() {
        return "Hello World";
    }
}

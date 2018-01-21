package io.flippedclassroom.server.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestController
public class TestPageController {
	@RequestMapping("/hello")
	public String hello() {
		return "Hello World!";
	}

	@RequestMapping("/")
	public String index() {
		return "<html>\n" +
				"<head>\n" +
				"    <meta charset=\"utf-8\"/>\n" +
				"    <title>XD</title>\n" +
				"</head>\n" +
				"<body>\n" +
				"<p>Hello fc.xd.style!</p>\n" +
				"<br/>\n" +
				"<p>API 说明：<a href=\"https://fc.xd.style/swagger-ui.html\">httpsL//fc.xd.style/swagger-ui.html</a>\n" +
				"</body>\n" +
				"</html>\n";
	}
}

package com.ruoyi.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user/hello")
public class HelloUserController {
	@GetMapping("/l")
	@ResponseBody
	public String hello(Model o) {
		return "hello user";
	}
}

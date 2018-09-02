package com.queue.app.action.test;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.queue.common.exception.ErrorException;

@Controller
@RequestMapping(value="/test")
public class TestController {
	@RequestMapping(method=RequestMethod.GET)
	public String test(Model model) throws ErrorException {
		return "test/index";
	}
}
package com.increff.assure.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AppUiController extends AbstractUiController {
	@RequestMapping(value = "/consumer")
	public ModelAndView consumer() {
		return mav("consumer.html");
	}

	@RequestMapping(value = "/product")
	public ModelAndView product() {
		return mav("product.html");
	}

	@RequestMapping(value = "/bin")
	public ModelAndView bin() {
		return mav("bin.html");
	}

	@RequestMapping(value = "/binSku")
	public ModelAndView binInventory() {
		return mav("binInventory.html");
	}
}

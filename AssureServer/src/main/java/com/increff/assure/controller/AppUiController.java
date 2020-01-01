package com.increff.assure.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AppUiController extends AbstractUiController {
	@RequestMapping(value = "/ui/consumer")
	public ModelAndView consumer() {
		return mav("consumer.html");
	}

	@RequestMapping(value = "/ui/product")
	public ModelAndView product() {
		return mav("product.html");
	}

	@RequestMapping(value = "/ui/bin")
	public ModelAndView bin() {
		return mav("bin.html");
	}

	@RequestMapping(value = "/ui/binSku")
	public ModelAndView binInventory() {
		return mav("binInventory.html");
	}
}

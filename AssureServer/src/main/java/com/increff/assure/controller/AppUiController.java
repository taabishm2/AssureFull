package com.increff.assure.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AppUiController extends AbstractUiController {
	@RequestMapping(value = "/")
	public ModelAndView home() {
		return mav("index.html");
	}

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

	@RequestMapping(value = "/channel")
	public ModelAndView channel() {
		return mav("channel.html");
	}

	@RequestMapping(value = "/channelListing")
	public ModelAndView channelListing() {
		return mav("channelListing.html");
	}

	@RequestMapping(value = "/order")
	public ModelAndView order() {
		return mav("order.html");
	}
}

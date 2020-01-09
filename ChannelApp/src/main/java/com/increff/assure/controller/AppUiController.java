package com.increff.assure.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AppUiController extends AbstractUiController {
    @RequestMapping(value = "/order")
    public ModelAndView order() {
        return mav("order.html");
    }
}

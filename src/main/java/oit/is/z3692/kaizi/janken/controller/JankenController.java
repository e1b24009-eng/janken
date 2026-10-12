package oit.is.z3692.kaizi.janken.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import oit.is.z3692.kaizi.janken.model.Janken;

/**
 * JankenController
 */
@Controller
public class JankenController {

  /**
   * /jankenへのGETリクエスト(URL直接アクセス)ではユーザ名を表示しない
   */
  @GetMapping("/janken")
  public String janken() {
    return "janken.html";
  }

  /**
   * index.htmlのフォームからPOSTされたユーザ名を受け取り，janken.htmlに渡す
   */
  @PostMapping("/janken")
  public String janken(@RequestParam String username, ModelMap model) {
    model.addAttribute("username", username);
    return "janken.html";
  }

  /**
   * /jankengame?hand=Gu のようなクエリパラメータで手を受け取り，CPUと対戦する
   */
  @GetMapping("/jankengame")
  public String jankengame(@RequestParam String hand, ModelMap model) {
    Janken janken = new Janken(hand);
    model.addAttribute("janken", janken);
    return "janken.html";
  }

}

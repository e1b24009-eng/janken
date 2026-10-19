package oit.is.z3692.kaizi.janken.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import oit.is.z3692.kaizi.janken.model.Entry;
import oit.is.z3692.kaizi.janken.model.Janken;

/**
 * JankenController
 */
@Controller
public class JankenController {

  /**
   * DIコンテナが生成したEntryを全ユーザで共有する
   */
  @Autowired
  private Entry entry;

  /**
   * /jankenへのGETリクエスト：ログインユーザをエントリーに追加し，
   * ログインユーザ名とエントリー中の全ユーザを表示する
   */
  @GetMapping("/janken")
  public String janken(Principal prin, ModelMap model) {
    String loginUser = prin.getName();
    entry.addUser(loginUser);
    model.addAttribute("username", loginUser);
    model.addAttribute("entry", entry);
    return "janken.html";
  }

  /**
   * フォームからPOSTされたユーザ名を受け取り，janken.htmlに渡す
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

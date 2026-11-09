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

import java.util.ArrayList;
import oit.is.z3692.kaizi.janken.model.User;
import oit.is.z3692.kaizi.janken.model.UserMapper;
import oit.is.z3692.kaizi.janken.model.MatchMapper;
import oit.is.z3692.kaizi.janken.model.Match;

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

  @Autowired
  UserMapper userMapper;

  @Autowired
  MatchMapper matchMapper;

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
    model.addAttribute("users", userMapper.selectAllUsers());
    model.addAttribute("matches", matchMapper.selectAllMatches());
    return "janken.html";
  }

  /**
   * /match?id=1 のように対戦相手のidを受け取り，match.htmlに対戦相手を表示する
   */
  @GetMapping("/match")
  public String match(@RequestParam Integer id, Principal prin, ModelMap model) {
    String loginUser = prin.getName();
    User opponent = userMapper.selectById(id);
    model.addAttribute("username", loginUser);
    model.addAttribute("opponent", opponent);
    return "match.html";
  }

  /**
   * /fight?id=1&hand=Gu のように対戦相手のidと自分の手を受け取り，
   * CPUと対戦して結果をmatchesテーブルに登録し，match.htmlに表示する
   */
  @GetMapping("/fight")
  public String fight(@RequestParam Integer id, @RequestParam String hand, Principal prin, ModelMap model) {
    String loginUser = prin.getName();
    User me = userMapper.selectByName(loginUser);
    User opponent = userMapper.selectById(id);
    Janken janken = new Janken(hand); // CPUの手は固定（グー）

    Match match = new Match();
    match.setUser1(me.getId());
    match.setUser2(opponent.getId());
    match.setUser1Hand(hand);
    match.setUser2Hand(janken.getCpuHand());
    matchMapper.insertMatch(match);

    model.addAttribute("username", loginUser);
    model.addAttribute("opponent", opponent);
    model.addAttribute("janken", janken);
    return "match.html";
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

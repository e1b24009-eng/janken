package oit.is.z3692.kaizi.janken.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import oit.is.z3692.kaizi.janken.model.Entry;
import oit.is.z3692.kaizi.janken.model.Janken;

import java.util.ArrayList;
import oit.is.z3692.kaizi.janken.model.User;
import oit.is.z3692.kaizi.janken.model.UserMapper;
import oit.is.z3692.kaizi.janken.model.MatchMapper;
import oit.is.z3692.kaizi.janken.model.Match;
import oit.is.z3692.kaizi.janken.model.MatchInfo;
import oit.is.z3692.kaizi.janken.model.MatchInfoMapper;
import oit.is.z3692.kaizi.janken.service.AsyncKekka;

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

  @Autowired
  MatchInfoMapper matchInfoMapper;

  @Autowired
  AsyncKekka asyncKekka;

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
    model.addAttribute("activeMatches", matchInfoMapper.selectActiveMatchInfo());
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
   * /wait?id=3&hand=Gu のように対戦相手のidと自分の手を受け取る
   * - 相手が自分を待っている（相手→自分のmatchinfoがisActive=TRUE）なら試合成立：
   *   matchesに「相手のid・自分のid・相手の手・自分の手・isActive=TRUE」を登録し，そのmatchinfoをisActive=FALSEにする
   * - 待っていなければ：matchinfoに「自分のid・相手のid・自分の手・isActive=TRUE」を登録して待つ
   */
  @GetMapping("/wait")
  @Transactional
  public String waitMatch(@RequestParam Integer id, @RequestParam String hand, Principal prin, ModelMap model) {
    String loginUser = prin.getName();
    User me = userMapper.selectByName(loginUser);

    // 相手(id)が自分(me)を待っているmatchinfoを探す
    MatchInfo waiting = matchInfoMapper.selectActiveByUsers(id, me.getId());

    if (waiting == null) {
      // 待っている相手がいない：自分が待つ側になる
      MatchInfo matchInfo = new MatchInfo();
      matchInfo.setUser1(me.getId());
      matchInfo.setUser2(id);
      matchInfo.setUser1Hand(hand);
      matchInfo.setIsActive(true);
      matchInfoMapper.insertMatchInfo(matchInfo);
    } else {
      // 試合成立：matchesに登録し，待っていたmatchinfoを終了にする
      Match match = new Match();
      match.setUser1(waiting.getUser1());
      match.setUser2(me.getId());
      match.setUser1Hand(waiting.getUser1Hand());
      match.setUser2Hand(hand);
      match.setIsActive(true);
      matchMapper.insertMatch(match);
      matchInfoMapper.updateInactiveById(waiting.getId());
      // 後から手を出した側には，その場で結果を表示する
      model.addAttribute("match", match);
    }

    model.addAttribute("username", loginUser);
    return "wait.html";
  }

  /**
   * wait.html の EventSource から呼ばれる．待っている側に試合結果を SSE で送る
   */
  @GetMapping("/waitresult")
  public SseEmitter waitResult(Principal prin) {
    User me = userMapper.selectByName(prin.getName());
    final SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
    this.asyncKekka.asyncShowKekka(emitter, me.getId());
    return emitter;
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

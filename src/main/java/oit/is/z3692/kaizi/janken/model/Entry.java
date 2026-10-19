package oit.is.z3692.kaizi.janken.model;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

/**
 * エントリー中のユーザを管理するクラス
 * @Componentを付けるとDIコンテナが1つだけ生成し，全リクエストで共有される
 */
@Component
public class Entry {
  ArrayList<String> users = new ArrayList<>();

  /**
   * ユーザをエントリーに追加する（同じユーザは重複して追加しない）
   */
  public void addUser(String name) {
    if (!users.contains(name)) {
      users.add(name);
    }
  }

  public ArrayList<String> getUsers() {
    return users;
  }

  public int getUserCount() {
    return users.size();
  }
}

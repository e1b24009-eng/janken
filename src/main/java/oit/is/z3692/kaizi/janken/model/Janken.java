package oit.is.z3692.kaizi.janken.model;

/**
 * Janken
 *
 * プレイヤーの手とCPUの手から勝敗を判定する
 */
public class Janken {
  private String playerHand;
  private String cpuHand;
  private String result;

  public Janken(String playerHand) {
    this.playerHand = playerHand;
    this.cpuHand = "Gu"; // task2: CPUは常にグー
    this.result = judge(playerHand, cpuHand);
  }

  private String judge(String p, String c) {
    if (p.equals(c)) {
      return "あいこ";
    }
    if ((p.equals("Gu") && c.equals("Choki")) ||
        (p.equals("Choki") && c.equals("Pa")) ||
        (p.equals("Pa") && c.equals("Gu"))) {
      return "勝ち";
    }
    return "負け";
  }

  // thymeleafから ${janken.playerHand} などで参照するためにgetterが必要
  public String getPlayerHand() {
    return playerHand;
  }

  public String getCpuHand() {
    return cpuHand;
  }

  public String getResult() {
    return result;
  }
}

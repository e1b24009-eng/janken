package oit.is.z3692.kaizi.janken.service;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import oit.is.z3692.kaizi.janken.model.Match;
import oit.is.z3692.kaizi.janken.model.MatchMapper;

@Service
public class AsyncKekka {

  private final Logger logger = LoggerFactory.getLogger(AsyncKekka.class);

  @Autowired
  MatchMapper matchMapper;

  /**
   * 待っている側（userId が user1 の試合）の結果が matches に isActive=TRUE で登録されるまで見張り，
   * 見つかったら isActive を FALSE に更新してから，その試合結果をブラウザに send する
   *
   * @param emitter
   * @param userId  待っているユーザのid
   */
  @Async
  public void asyncShowKekka(SseEmitter emitter, int userId) {
    try {
      while (true) {// 無限ループ
        Match match = matchMapper.selectActiveByUser1(userId);
        // まだ試合が成立していなければ0.5s休む
        if (match == null) {
          TimeUnit.MILLISECONDS.sleep(500);
          continue;
        }
        // 同じ結果を何度も表示しないように，isActive を FALSE にしてから send する
        matchMapper.updateInactiveById(match.getId());
        emitter.send(match);
        break;
      }
    } catch (Exception e) {
      // 例外の名前とメッセージだけ表示する
      logger.warn("Exception:" + e.getClass().getName() + ":" + e.getMessage());
    } finally {
      emitter.complete();
    }
  }
}

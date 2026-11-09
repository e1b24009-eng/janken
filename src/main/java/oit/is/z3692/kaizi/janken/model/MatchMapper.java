package oit.is.z3692.kaizi.janken.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MatchMapper {
  @Select("SELECT id, user1, user2, user1Hand, user2Hand, isActive FROM matches")
  ArrayList<Match> selectAllMatches();

  @Insert("INSERT INTO matches (user1, user2, user1Hand, user2Hand, isActive) VALUES (#{user1}, #{user2}, #{user1Hand}, #{user2Hand}, #{isActive})")
  void insertMatch(Match match);

  /**
   * user1（待っていた側）の，isActive=TRUE の試合を1件取得する（なければnull）
   */
  @Select("SELECT id, user1, user2, user1Hand, user2Hand, isActive FROM matches WHERE user1 = #{user1} AND isActive = TRUE ORDER BY id LIMIT 1")
  Match selectActiveByUser1(int user1);

  @Update("UPDATE matches SET isActive = FALSE WHERE id = #{id}")
  boolean updateInactiveById(int id);
}

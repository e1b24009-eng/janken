package oit.is.z3692.kaizi.janken.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MatchInfoMapper {
  @Insert("INSERT INTO matchinfo (user1, user2, user1Hand, isActive) VALUES (#{user1}, #{user2}, #{user1Hand}, #{isActive})")
  void insertMatchInfo(MatchInfo matchInfo);

  @Select("SELECT id, user1, user2, user1Hand, isActive FROM matchinfo WHERE isActive = TRUE")
  ArrayList<MatchInfo> selectActiveMatchInfo();

  /**
   * user1が待っている相手(user2)との，isActive=TRUEのmatchinfoを1件取得する（なければnull）
   */
  @Select("SELECT id, user1, user2, user1Hand, isActive FROM matchinfo WHERE user1 = #{user1} AND user2 = #{user2} AND isActive = TRUE ORDER BY id LIMIT 1")
  MatchInfo selectActiveByUsers(@Param("user1") int user1, @Param("user2") int user2);

  @Update("UPDATE matchinfo SET isActive = FALSE WHERE id = #{id}")
  boolean updateInactiveById(int id);
}

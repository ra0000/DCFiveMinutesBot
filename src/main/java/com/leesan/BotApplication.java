package com.leesan;

import be.zvz.kotlininside.KotlinInside;
import be.zvz.kotlininside.api.article.ArticleWrite;
import be.zvz.kotlininside.api.type.Article;
import be.zvz.kotlininside.api.type.content.StringContent;
import be.zvz.kotlininside.http.DefaultHttpClient;
import be.zvz.kotlininside.session.user.Anonymous;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;

public class BotApplication {
  private static final Logger log = LoggerFactory.getLogger(Main.class);
  private final long nearestNextMinutes = getNearestNextMinutes();
  
  private long getNearestNextMinutes() {
    long currentTime = System.currentTimeMillis();
    
    return currentTime + 60000 - (currentTime % 60000);
  }
  
  private String generateContent() {
    LocalDateTime nowDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(nearestNextMinutes), ZoneId.of("Asia/Seoul"));
    
    return "지금은 " + nowDateTime.getHour() + "시 " + nowDateTime.getMinute() + "분이에요!";
  }
  
  private void createKotlinInsideInstance(String password) {
    KotlinInside.createInstance(new Anonymous("30분봇", password), new DefaultHttpClient(true, true));
  }
  
  private void waitForNearestNextMinutes() throws InterruptedException {
    while ((nearestNextMinutes - System.currentTimeMillis()) > 300) {
      Thread.sleep(500);
    }
  }
  
  private ArticleWrite getPoorArticleWrite() {
    String content = generateContent();
    
    Article article = new Article(content, Collections.singletonList(new StringContent(content)));
    
    return new ArticleWrite("programming", article, KotlinInside.getInstance().session); // 네이밍 진짜 왜 이따구냐? 클래스 네임이 ArticleWrite가 말이됨?
  }
  
  public void startBot(String password) throws InterruptedException {
    log.info("실행 예정: " + nearestNextMinutes);
    
    createKotlinInsideInstance(password);
    
    waitForNearestNextMinutes();
    
    ArticleWrite poorArticleWrite = getPoorArticleWrite();
    
    log.info("실행");
    
    ArticleWrite.WriteResult writeResult = poorArticleWrite.write();
    
    if (writeResult.getResult()) {
      log.info("글씀 글아듸: " + writeResult.getArticleId());
    } else {
      log.info("error! result must be true but got false; cause=" + writeResult.getCause());
    }
  }
}

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

public class Main {
  static final Logger log = LoggerFactory.getLogger(Main.class);
  
  private static long getNearestNextMinutes() {
    long currentTime = System.currentTimeMillis();
    
    return currentTime + 60000 - (currentTime % 60000);
  }
  
  public static void main(String[] args) throws InterruptedException {
    long nearestNextMinutes = getNearestNextMinutes();
    log.info("실행 예정: " + nearestNextMinutes);
    
    KotlinInside.createInstance(new Anonymous("30분봇", args[0]), new DefaultHttpClient(true, true));
  
    LocalDateTime nowDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(nearestNextMinutes), ZoneId.of("Asia/Seoul"));
  
    String content = "지금은 " + nowDateTime.getHour() + "시 " + nowDateTime.getMinute() + "분이에요!";
    Article article = new Article(content, Collections.singletonList(new StringContent(content)));
    
    ArticleWrite articleWrite = new ArticleWrite("programming", article, KotlinInside.getInstance().session);
    
    while ((nearestNextMinutes - System.currentTimeMillis()) > 300) {
      Thread.sleep(500);
    }
    
    log.info("실행");
  
    ArticleWrite.WriteResult writeResult = articleWrite.write();
    
    if (writeResult.getResult()) {
      log.info("글씀 글아듸: " + writeResult.getArticleId());
    } else {
      log.info("error! result must be true; cause=" + writeResult.getCause());
    }
  }
}
package com.stevanrose.carbon_two.dates;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DatesTest {

  @Test
  void date() {

    Date date = new Date();
    log.info("java.util.Date: {}", date);

    LocalDateTime localDateTime = LocalDateTime.now();
    log.info("java.time.LocalDateTime: {}", localDateTime);
  }

  @Test
  void dateConvertToLocalDateTime() {
    Date date = new Date();
    log.info("java.util.Date: {}", date);
    LocalDateTime localDateTime =
        date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
    log.info("Converted java.util.Date to java.time.LocalDateTime: {}", localDateTime);
    Date backToDate = Date.from(localDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant());
    log.info("Converted back to java.util.Date: {}", backToDate);
  }

  @Test
  void dateToOffsetDateTime() {
    Date date = new Date();
    log.info("java.util.Date: {}", date);
    OffsetDateTime offsetDateTime = date.toInstant().atOffset(java.time.ZoneOffset.UTC);
    log.info("Converted java.util.Date to java.time.OffsetDateTime: {}", offsetDateTime);
    Date backToDate = Date.from(offsetDateTime.toInstant());
    log.info("Converted back to java.util.Date: {}", backToDate);
  }
}

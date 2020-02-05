package com.daniel.util.debug;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**计时器
 * @author Daniel
 *
 */
public class TimeKeeper {

  private static SimpleDateFormat simpleDateFormat;
  
  public static SimpleDateFormat getSdf() {
    return simpleDateFormat;
  }

  public static void setSdf(SimpleDateFormat sdf) {
    TimeKeeper.simpleDateFormat = sdf;
  }

  private static long currentTime = System.currentTimeMillis();
  
  static {
    simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC+08:00"));
  }
  
  /**距离上次调用step()经过的时间
   * @return
   */
  public static synchronized Long during() {
    Long during = System.currentTimeMillis() - currentTime;
    currentTime = System.currentTimeMillis();
    return during;
  }

  /**距离上次调用step()经过的时间
   * @param stepName
   */
  public static void step(String stepName) {
    System.out.println(stepName + ": " + simpleDateFormat.format(new Date(during())));
  }

  /**距离上次调用step()经过的时间
   * @param stepNo
   */
  public static void step(Number stepNo) {
    step(stepNo + "");
  }

}

/*
 * mseeworld工作室，致力于人工智能研究。Email: xyag.902@163.com
 */
package com.mseeworld.linefind;

/**
 *
 * @author xy
 */
public class LineParameter {

  /**image*/
  public final int imgWidth = 3072;
  public final int imgHeight = 3072;
  /**hough*/
  public final int thetaSize = 180;
  public final int rhoSize = 100;
  public final int thetaRange = 36;
  public final int rhoRange = 10;
  public final int maxHoughFrameNunmber = 10;
  /**obj*/
  public final int objInitMinPoint = 5;
  public final int validLineMinPoint = 5;
  public final float maxDistance = 100;
  public final float rhoErrorTimes = (float) 0.2;
}

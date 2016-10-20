/*
 * mseeworld工作室，致力于人工智能研究。Email: xyag.902@163.com
 */
package com.mseeworld.gwac;

/**
 *
 * @author xy
 */
public class HoughtPoint implements Comparable, Cloneable {

  private int pIdx;
  private int frameNumber;
  private float theta;
  private float rho;
  private float ktheta;
  private float theta2;
  private float rho2;
  private float x;
  private float y;

  public void printInfo() {
    System.out.println(String.format("%4d %5d %8.2f %8.2f %8.2f %8.2f %8.2f %8.2f %8.2f",
            this.pIdx, this.frameNumber, this.x, this.y, this.rho, this.getRho2(),
            this.theta * 180 / Math.PI, this.ktheta * 180 / Math.PI, this.getTheta2() * 180 / Math.PI));
  }

  public HoughtPoint(int pIdx, int frameNumber, float theta, float rho, float x, float y) {
    this.pIdx = pIdx;
    this.frameNumber = frameNumber;
    this.theta = theta;
    this.rho = rho;
    this.x = x;
    this.y = y;
  }

  public void calKtheta(HoughtPoint hp, float imgXCenter, float imgYCenter, float halfRho) {

    double xDelta = hp.x - x;
    double yDelta = hp.y - y;
    ktheta = (float) (Math.atan2(yDelta, xDelta));

    if (ktheta < 0) {
      ktheta += Math.PI;
    }

    if (ktheta < Math.PI / 2) {
      theta2 = (float) (ktheta + Math.PI / 2);
    } else {
      theta2 = (float) (ktheta - Math.PI / 2);
    }

    rho2 = (float) ((x - imgXCenter) * Math.cos(theta2) + (y - imgYCenter) * Math.sin(theta2)) + halfRho;
  }

  /**
   * @return the pIdx
   */
  public int getpIdx() {
    return pIdx;
  }

  /**
   * @param pIdx the pIdx to set
   */
  public void setpIdx(int pIdx) {
    this.pIdx = pIdx;
  }

  /**
   * @return the frameNumber
   */
  public int getFrameNumber() {
    return frameNumber;
  }

  /**
   * @param frameNumber the frameNumber to set
   */
  public void setFrameNumber(int frameNumber) {
    this.frameNumber = frameNumber;
  }

  /**
   * @return the theta
   */
  public float getTheta() {
    return theta;
  }

  /**
   * @param theta the theta to set
   */
  public void setTheta(float theta) {
    this.theta = theta;
  }

  /**
   * @return the rho
   */
  public float getRho() {
    return rho;
  }

  /**
   * @param rho the rho to set
   */
  public void setRho(float rho) {
    this.rho = rho;
  }

  @Override
  public int compareTo(Object o) {
    HoughtPoint tpoint = (HoughtPoint) o;
    int result = 1;
    if (this.frameNumber < tpoint.frameNumber) {
      result = -1;
    } else if (Math.abs(this.frameNumber - tpoint.frameNumber) == 0) {
      if (this.x < tpoint.x) {
        result = -1;
      }
    }
    return result;
  }

  /**
   * @return the x
   */
  public float getX() {
    return x;
  }

  /**
   * @param x the x to set
   */
  public void setX(float x) {
    this.x = x;
  }

  /**
   * @return the y
   */
  public float getY() {
    return y;
  }

  /**
   * @param y the y to set
   */
  public void setY(float y) {
    this.y = y;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  /**
   * @return the ktheta
   */
  public float getKtheta() {
    return ktheta;
  }

  /**
   * @param ktheta the ktheta to set
   */
  public void setKtheta(float ktheta) {
    this.ktheta = ktheta;
  }

  /**
   * @return the theta2
   */
  public float getTheta2() {
    return theta2;
  }

  /**
   * @param theta2 the theta2 to set
   */
  public void setTheta2(float theta2) {
    this.theta2 = theta2;
  }

  /**
   * @return the rho2
   */
  public float getRho2() {
    return rho2;
  }

  /**
   * @param rho2 the rho2 to set
   */
  public void setRho2(float rho2) {
    this.rho2 = rho2;
  }
}

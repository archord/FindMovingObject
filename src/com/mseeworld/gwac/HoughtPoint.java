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
  private float x;
  private float y;
  
  public void printInfo(){
    System.out.println(String.format("%d\t%d\t%f\t%f\t%f\t%f", 
            this.pIdx, this.frameNumber, this.x, this.y, this.theta, this.rho));
  }

  public HoughtPoint(int pIdx, int frameNumber, float theta, float rho, float x, float y) {
    this.pIdx = pIdx;
    this.frameNumber = frameNumber;
    this.theta = theta;
    this.rho = rho;
    this.x = x;
    this.y = y;
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
}

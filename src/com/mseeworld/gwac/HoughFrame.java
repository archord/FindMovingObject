/*
 * mseeworld工作室，致力于人工智能研究。Email: xyag.902@163.com
 */
package com.mseeworld.gwac;

import java.util.ArrayList;

/**
 *
 * @author xy
 */
public class HoughFrame {

  int frameNumber;
  ArrayList<HoughtPoint> pointList;

  HoughtPoint minX;
  HoughtPoint maxX;
  HoughtPoint minY;
  HoughtPoint maxY;
  float deltaX;
  float deltaY;

  /**
   * 必须使用该构造函数初始化，然后用addPoint添加点。
   *
   * @param hp
   * @param frameNumber
   */
  public HoughFrame(HoughtPoint hp, int frameNumber) {
    this.frameNumber = frameNumber;
    this.pointList = new ArrayList();
    this.pointList.add(hp);

    minX = hp;
    maxX = hp;
    minY = hp;
    maxY = hp;
  }

  public final void addPoint(HoughtPoint hp) {
    this.pointList.add(hp);
    findMinAndMaxXY(hp);
  }

  public void removePoint(int i) {
    this.pointList.remove(i);
    if (!pointList.isEmpty()) {
      findMinAndMaxXY();
    }
  }

  public void removeAll() {
    this.pointList.clear();
  }

  public void findMinAndMaxXY() {
    int i = 0;
    for (HoughtPoint hp : pointList) {
      if (i == 0) {
        minX = hp;
        maxX = hp;
        minY = hp;
        maxY = hp;
      } else {
        findMinAndMaxXY(hp);
      }
      i++;
    }
  }

  /**
   * 每新增加一个点，就更新一次X、Y的最大值最小值
   *
   * @param hp
   */
  public void findMinAndMaxXY(HoughtPoint hp) {

    float tx = hp.getX();
    float ty = hp.getY();
    if (tx < minX.getX()) {
      minX = hp;
    }
    if (tx > maxX.getX()) {
      maxX = hp;
    }
    if (ty < minY.getY()) {
      minY = hp;
    }
    if (ty > maxY.getY()) {
      maxY = hp;
    }
    deltaX = maxX.getX() - minX.getX();
    deltaY = maxY.getY() - minY.getY();
  }

}

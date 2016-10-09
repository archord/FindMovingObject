/*
 * mseeworld工作室，致力于人工智能研究。Email: xyag.902@163.com
 */
package com.mseeworld.gwac;

import java.util.ArrayList;

/**
 *
 * @author xy
 */
public class HoughLine {

  public float theta;
  public float rho;
  public float lastRho;
  public float lastTheta;
  public float lastRho2;
  public float lastTheta2;
  public ArrayList<HoughFrame> frameList;
  public int pointNumber;

  public int firstFrameNumber = Integer.MAX_VALUE;
  public int lastFrameNumber = Integer.MIN_VALUE;
  public HoughtPoint firstPoint; //minNumber
  public HoughtPoint lastPoint; //maxNumber
  public HoughtPoint lastPoint2; //倒数第二帧的最后一个点，用来和直线的最后一个点更新直线的theta和rho

  public float sinTheta;
  public float cosTheta;

  public float deltaX;
  public float deltaY;

  public float speedX;
  public float speedY;

  /**
   *
   * @param theta
   * @param rho
   */
  public HoughLine(float theta, float rho) {
    this.theta = theta;
    this.rho = rho;
    this.frameList = new ArrayList();
    this.pointNumber = 0;

    this.sinTheta = (float) Math.sin(this.theta);
    this.cosTheta = (float) Math.cos(this.theta);
  }

  public void addPoint(int pIdx, int frameNumber, float theta, float rho, float x, float y) {
    this.addPoint(new HoughtPoint(pIdx, frameNumber, theta, rho, x, y));
  }

  public final void addPoint(HoughtPoint hp) {

    if (frameList.isEmpty() || (lastFrameNumber != hp.getFrameNumber())) {
      lastFrameNumber = hp.getFrameNumber();
      HoughFrame hframe = new HoughFrame(hp, hp.getFrameNumber());
      frameList.add(hframe);
    } else {
      HoughFrame lastFrame = frameList.get(frameList.size() - 1);
      lastFrame.addPoint(hp);
    }
    findFirstAndLastPoint();
//    updateThetaRho();
    pointNumber++;
  }

  public void removePoint(HoughtPoint hp) {

    for (int k = 0; k < this.frameList.size(); k++) {
      HoughFrame tFrame = this.frameList.get(k);
      if (tFrame.frameNumber == hp.getFrameNumber()) {
        for (int i = 0; i < tFrame.pointList.size(); i++) {
          if (hp.getpIdx() == tFrame.pointList.get(i).getpIdx()) {
//            System.out.println(String.format("real: t=%5d, idx=%5d, number=%5d", t, tline2.pointList.get(i), tline2.frameNumberList.get(i)));
            tFrame.removePoint(i);
            this.pointNumber--;
            if (tFrame.pointList.isEmpty()) {
              this.frameList.remove(k);
            }
            break;
          }
        }
        break;
      }
    }
  }

  public void removeFirstFrame() {
    HoughFrame firstFrame = frameList.get(0);
    this.pointNumber -= firstFrame.pointList.size();
    firstFrame.removeAll();
    frameList.remove(0);
  }

  public void removeOldFrame(int toFrameNumber) {

    while (this.frameList.size() > 0) {
      HoughFrame hf = this.frameList.get(0);
      if (hf.frameNumber <= toFrameNumber) {
        this.pointNumber -= hf.pointList.size();
        hf.removeAll();
        this.frameList.remove(0);
      } else {
        break;
      }
    }
  }

  public int size() {
    return this.pointNumber;
  }

  public int validSize() {
    return this.pointNumber;
  }

  public int getLastFrameNumber() {
    return this.lastFrameNumber;
  }

  public int getLastOT1Idx() {
    return 0;
  }

  /**
   *
   * @param ot1
   * @param maxDistance 新目标与直线最后一个点的距离不超过maxDistance
   * @return
   */
  public boolean matchLastPoint(OT1 ot1, float maxDistance) {
    boolean flag = true;
    if (this.pointNumber > 0) {
      double distance = ot1.distance(lastPoint.getX(), lastPoint.getY());
      flag = distance < maxDistance;
    }
    return flag;
  }

  /**
   * 距离匹配和方向匹配
   *
   * @param ot1
   * @param maxDistance 新目标与直线最后一个点的距离不超过maxDistance
   * @return
   */
  public boolean matchLastPoint2(OT1 ot1, float maxDistance) {

    boolean flag = true;
    if (this.pointNumber > 0) {
      double distance = ot1.distance(lastPoint.getX(), lastPoint.getY());
      boolean deltaFlag = true;
      if (this.frameList.size() >= 2) {
        float xDelta = lastPoint.getX() - ot1.getX();
        float yDelta = lastPoint.getY() - ot1.getY();
        deltaFlag = (xDelta * deltaX > 0) && (yDelta * deltaY > 0);
      }
      flag = (distance < maxDistance) && deltaFlag;
    }
    return flag;
  }

  public boolean lineMatch() {

    return false;
  }

  public void updateThetaRho() {

    if (this.frameList.size() >= 4) {
      float xdelta = lastPoint.getX() - lastPoint2.getX();
      float ydelta = lastPoint.getY() - lastPoint2.getY();
      double kangle = Math.atan2(ydelta, xdelta);
      if (kangle < 0) {
        kangle += Math.PI;
      }
      if (theta < 1.57) {
        lastTheta2 = (float) (kangle - 1.57);
      } else {
        lastTheta2 = (float) (kangle + 1.57);
      }
      System.out.println(String.format("theta1= %5.3f, kangle= %5.3f, theta2= %5.3f", theta, kangle, lastTheta2));
    }
  }

  public void calculateSpeed() {

  }

  /**
   * 只有在帧数大于等于2时，才有用
   */
  public void findFirstAndLastPoint() {

    HoughFrame firstFrame = frameList.get(0);
    HoughFrame lastFrame = frameList.get(frameList.size() - 1);
    HoughFrame lastFrame2 = lastFrame;

    if (this.frameList.size() >= 2) {
      lastFrame2 = frameList.get(frameList.size() - 2);
    }

    HoughtPoint ffMinPoint, lfMinPoint;

    //PI/4=0.7854, PI*3/4=2.3562
//    if (this.frameList.size() >= 2) {
    if (theta > 0.7854 && theta < 2.3562) {
      ffMinPoint = firstFrame.minY;
      lfMinPoint = lastFrame.minY;
    } else {
      ffMinPoint = firstFrame.minX;
      lfMinPoint = lastFrame.minX;
    }

    deltaX = ffMinPoint.getX() - lfMinPoint.getX();
    deltaY = ffMinPoint.getY() - lfMinPoint.getY();

    //theta大于45度小于135度，Y方向变化大
    if (theta > 0.7854 && theta < 2.3562) {
      if (deltaX > 0 && deltaY > 0) {
        firstPoint = firstFrame.minY;
        lastPoint = lastFrame.maxY;
        lastPoint2 = lastFrame2.maxY;
      } else if (deltaX > 0 && deltaY < 0) {
        firstPoint = firstFrame.maxY;
        lastPoint = lastFrame.minY;
        lastPoint2 = lastFrame2.minY;
      } else if (deltaX < 0 && deltaY > 0) {
        firstPoint = firstFrame.minY;
        lastPoint = lastFrame.maxY;
        lastPoint2 = lastFrame2.maxY;
      } else {
        firstPoint = firstFrame.maxY;
        lastPoint = lastFrame.minY;
        lastPoint2 = lastFrame2.minY;
      }
    } else {//theta大于135度小于45度，X方向变化大
      if (deltaX > 0 && deltaY > 0) {
        firstPoint = firstFrame.minX;
        lastPoint = lastFrame.maxX;
        lastPoint2 = lastFrame2.maxX;
      } else if (deltaX > 0 && deltaY < 0) {
        firstPoint = firstFrame.minX;
        lastPoint = lastFrame.maxX;
        lastPoint2 = lastFrame2.maxX;
      } else if (deltaX < 0 && deltaY > 0) {
        firstPoint = firstFrame.maxX;
        lastPoint = lastFrame.minX;
        lastPoint2 = lastFrame2.minX;
      } else {
        firstPoint = firstFrame.maxX;
        lastPoint = lastFrame.minX;
        lastPoint2 = lastFrame2.minX;
      }
    }
  }

  public void removeAll() {

    for (HoughFrame hf : frameList) {
      hf.removeAll();
    }
    this.frameList.clear();
  }

  public void printInfo(ArrayList<OT1> historyOT1s) {

    for (HoughFrame tFrame : frameList) {
      for (HoughtPoint tPoint : tFrame.pointList) {
        tPoint.printInfo();
      }
    }
  }

  public void printOT1Info(ArrayList<OT1> historyOT1s) {

    int i = 0;
    for (HoughFrame tFrame : frameList) {
      for (HoughtPoint tPoint : tFrame.pointList) {
        OT1 ot1 = historyOT1s.get(tPoint.getpIdx());
        ot1.printInfo();
        i++;
      }
    }
  }
}

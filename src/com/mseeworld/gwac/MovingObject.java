/*
 * mseeworld工作室，致力于人工智能研究。Email: xyag.902@163.com
 */
package com.mseeworld.gwac;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author xy
 */
public class MovingObject {

  public float theta;
  public float rho;
  public float lastRho;
  public ArrayList<HoughFrame> frameList;
  public int pointNumber;

  public int firstFrameNumber = Integer.MAX_VALUE;
  public int lastFrameNumber = Integer.MIN_VALUE;
  public HoughtPoint firstPoint; //minNumber
  public HoughtPoint lastPoint; //maxNumber

  public float sinTheta;
  public float cosTheta;

  public MovingObject(HoughLine tline) {
    this.theta = tline.theta;
    this.rho = tline.rho;
    this.lastRho = tline.lastRho;
    this.pointNumber = tline.pointNumber;
    this.firstFrameNumber = tline.firstFrameNumber;
    this.lastFrameNumber = tline.lastFrameNumber;
    this.firstPoint = tline.firstPoint;
    this.lastPoint = tline.lastPoint;
    
    this.frameList = new ArrayList();

    for (HoughFrame hf : tline.frameList) {
      for (HoughtPoint hp : hf.pointList) {
        addPoint(hp);
      }
    }

    this.sinTheta = (float) Math.sin(this.theta);
    this.cosTheta = (float) Math.cos(this.theta);
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
  }

  public void addPoint(int pIdx, int frameNumber, float theta, float rho, float x, float y) {
    this.addPoint(new HoughtPoint(pIdx, frameNumber, theta, rho, x, y));
  }

  public int getLastFrameNumber() {
    return lastFrameNumber;
  }

  public int getFirstOT1Idx1() {
    return 0;
  }

  public int getLastOT1Idx1() {
    return 0;
  }

  public boolean matchLastPoint(OT1 ot1, float maxDistance) {

    findFirstAndLastPoint();
    double distance = ot1.distance(lastPoint.getX(), lastPoint.getY());
    return distance < maxDistance;
  }

  public void findFirstAndLastPoint() {

    HoughFrame firstFrame = frameList.get(0);
    HoughFrame lastFrame = frameList.get(frameList.size() - 1);

    HoughtPoint ffMinPoint, lfMinPoint;

    //PI/4=0.7854, PI*3/4=2.3562
    if (theta > 0.7854 && theta < 2.3562) {
      ffMinPoint = firstFrame.minY;
      lfMinPoint = lastFrame.minY;
    } else {
      ffMinPoint = firstFrame.minX;
      lfMinPoint = lastFrame.minX;
    }

    float deltaX = ffMinPoint.getX() - lfMinPoint.getX();
    float deltaY = ffMinPoint.getY() - lfMinPoint.getY();

    //theta大于45度小于135度，Y方向变化大
    if (theta > 0.7854 && theta < 2.3562) {
      if (deltaX > 0 && deltaY > 0) {
        firstPoint = firstFrame.minY;
        lastPoint = lastFrame.maxY;
      } else if (deltaX > 0 && deltaY < 0) {
        firstPoint = firstFrame.maxY;
        lastPoint = lastFrame.minY;
      } else if (deltaX < 0 && deltaY > 0) {
        firstPoint = firstFrame.minY;
        lastPoint = lastFrame.maxY;
      } else {
        firstPoint = firstFrame.maxY;
        lastPoint = lastFrame.minY;
      }
    } else {//theta大于135度小于45度，X方向变化大
      if (deltaX > 0 && deltaY > 0) {
        firstPoint = firstFrame.minX;
        lastPoint = lastFrame.maxX;
      } else if (deltaX > 0 && deltaY < 0) {
        firstPoint = firstFrame.minX;
        lastPoint = lastFrame.maxX;
      } else if (deltaX < 0 && deltaY > 0) {
        firstPoint = firstFrame.maxX;
        lastPoint = lastFrame.minX;
      } else {
        firstPoint = firstFrame.maxX;
        lastPoint = lastFrame.minX;
      }
    }
  }

  public void printInfo(ArrayList<OT1> historyOT1s) {

    System.out.println(String.format("theta=%10.5f, rho=%10.5f", this.theta, this.rho));
    System.out.print("X, Y, theta, rho ");
    int i = 0;
    for (HoughFrame tFrame : frameList) {
      for (HoughtPoint tPoint : tFrame.pointList) {
        OT1 ot1 = historyOT1s.get(tPoint.getpIdx());
        System.out.println(String.format("idx=%5d, number=%5d, X=%10.5f, Y=%10.5f, theta=%10.5f, rho=%10.5f",
                tPoint.getpIdx(), ot1.getFrameNumber(), ot1.getX(), ot1.getY(), tPoint.getTheta(), tPoint.getRho()));
        i++;
      }
    }
  }
}

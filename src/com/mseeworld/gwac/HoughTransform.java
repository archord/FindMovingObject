/*
 * mseeworld工作室，致力于人工智能研究。Email: xyag.902@163.com
 */
package com.mseeworld.gwac;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author xy
 */
public class HoughTransform {

  // cache of values of sin and cos for different theta values. Has a significant performance improvement. 
  private double[] sinCache;
  private double[] cosCache;

  // the number of points that have been added 
  protected int numOT1s;
  HoughLine[][] houghArray;
  ArrayList<OT1> historyOT1s;
  ArrayList<LineObject> mvObjs;

  int maxHoughFrameNunmber;
  int minValidPoint;
  int minValidFrame = 2; //完成第minValidFrame帧的添加之后，在添加下一帧的第一个点之前，计算所有直线的速度

  int imgWidth;
  int imgHeight;
  float imgXCenter;
  float imgYCenter;

  double maxTheta;
  double maxRho;
  double halfRho;
  int thetaSize;
  int rhoSize;
  int thetaRange;
  int rhoRange;
  double thetaStep;
  double rhoStep;

  float maxDistance;
  float rhoErrorTimes;
  int validLineMinPoint;

  /**
   *
   * @param imgWidth 图像宽
   * @param imgHeight 图像高
   * @param thetaSize hough变换中，theta取值个数
   * @param rhoSize hough变换中，rho取值个数
   * @param thetaRange 在hough变化矩阵中，搜寻极大值时，theta方向的搜寻半径
   * @param rhoRange 在hough变化矩阵中，搜寻极大值时，rho方向的搜寻半径
   * @param maxHoughFrameNunmber
   * hough变换中，一条hough直线的中间能够缺失的最大帧数量；如果超过这个数量，则后续的点被处理为一条新的直线。
   * @param minValidPoint hough变换中，有效hough直线中最小有效点的个数
   * @param maxDistance
   * hough变换中，一条hough直线的中间能够缺失的最大直线距离；如果超过这个数量，则后续的点被处理为一条新的直线。
   * @param rhoErrorTimes
   * 取值最好不要大于1，一个新的点P，使用一条直线L（theta0,rho0）的theta0计算过P点的直线rho，如果abs(rho0-rho)小于rhoErrorTimes*rhoStep，则认为这个点P是直线L上的点。
   */
  public HoughTransform(int imgWidth, int imgHeight, int thetaSize, int rhoSize, int thetaRange, int rhoRange,
          int maxHoughFrameNunmber, int minValidPoint, float maxDistance, float rhoErrorTimes, int validLineMinPoint) {
    this.imgWidth = imgWidth;
    this.imgHeight = imgHeight;
    this.maxTheta = Math.PI;
    this.thetaSize = thetaSize;
    this.rhoSize = rhoSize;
    this.thetaRange = thetaRange;
    this.rhoRange = rhoRange;
    this.maxHoughFrameNunmber = maxHoughFrameNunmber;
    this.minValidPoint = minValidPoint;
    this.maxDistance = maxDistance;
    this.rhoErrorTimes = rhoErrorTimes;
    this.validLineMinPoint = validLineMinPoint;

    this.imgXCenter = this.imgWidth / 2;
    this.imgYCenter = this.imgHeight / 2;
    this.maxRho = Math.sqrt(Math.pow(imgWidth, 2) + Math.pow(imgHeight, 2));
    this.halfRho = this.maxRho / 2;
    this.thetaStep = this.maxTheta / this.thetaSize;
    this.rhoStep = this.maxRho / this.rhoSize;

    this.historyOT1s = new ArrayList();
    this.mvObjs = new ArrayList();

    initialise();
    this.rhoErrorTimes = rhoErrorTimes;
    this.validLineMinPoint = validLineMinPoint;
  }

  private void initialise() {
    houghArray = new HoughLine[thetaSize][rhoSize];
    // Count how many points there are 
    numOT1s = 0;

    // cache the values of sin and cos for faster processing 
    sinCache = new double[thetaSize];
    cosCache = sinCache.clone();
    for (int t = 0; t < thetaSize; t++) {
      double realTheta = t * thetaStep;
      sinCache[t] = Math.sin(realTheta);
      cosCache[t] = Math.cos(realTheta);
    }

    for (int t = 0; t < thetaSize; t++) {
      for (int r = 0; r < rhoSize; r++) {
        houghArray[t][r] = new HoughLine((float) (t * thetaStep), (float) (r * rhoStep), imgXCenter, imgYCenter, (float) halfRho);
//        houghArray[t][r] = new HoughLine((float) (t), (float) (r));
      }
    }

  }

  public void historyAddPoint(OT1 ot1) {
    historyOT1s.add(ot1);
    numOT1s++;
  }

  public void houghAddPoint(OT1 ot1) {

    for (int t = 0; t < thetaSize; t++) {

      float fr = (float) ((((ot1.getX() - imgXCenter) * cosCache[t]) + ((ot1.getY() - imgYCenter) * sinCache[t]) + halfRho) / rhoStep);
      int r = Math.round(fr);

      if (r < 0 || r >= this.rhoSize) {
        System.out.println("x=" + ot1.getX() + ",y=" + ot1.getY() + ",theta=" + t + ",rho=" + r);
        continue;
      }

      HoughLine tline = houghArray[t][r];
      int curNumber = ot1.getFrameNumber();
      tline.removeOldFrame(curNumber - this.maxHoughFrameNunmber);

      if (tline.matchLastPoint(ot1, maxDistance)) {
        tline.addPoint(numOT1s - 1, curNumber, (float) (thetaStep * t), (float) (fr * rhoStep), ot1.getX(), ot1.getY(), ot1.getDate());
        if (tline.validSize() >= this.minValidPoint) {
          LineObject lineObj = new LineObject((float) (t * thetaStep), (float) (r * rhoStep), imgXCenter, imgYCenter, (float) halfRho);
          lineObj.cloneLine(tline);
          mvObjs.add(lineObj);
          clearAllPoint(lineObj);
          break;
        }
      }
    }

  }

  /**
   * 每添加一个点，都要同时更新直线的rho和theta，目标只更新了rho
   *
   * @param ot1
   */
  public void lineAddPoint(OT1 ot1) {

    boolean findLine = false;
    int i = 0;
    for (LineObject tmo : this.mvObjs) {

      if (!tmo.isEndLine(ot1.getFrameNumber() - this.maxHoughFrameNunmber + 1)) {

        double trho = ((ot1.getX() - imgXCenter) * tmo.cosTheta + (ot1.getY() - imgYCenter) * tmo.sinTheta + halfRho); // / rhoStep
        if ((Math.abs(trho - tmo.lastRho) < rhoErrorTimes * this.rhoStep)) {  // 范围是不是太大  this.rhoStep
          boolean matchLastPoint = tmo.matchLastPoint2(ot1, maxDistance);
          if (matchLastPoint) {
            tmo.addPoint(numOT1s - 1, ot1.getFrameNumber(), tmo.theta, (float) (trho), ot1.getX(), ot1.getY(), ot1.getDate());
            findLine = true;
            break;
          }
        }
      }
    }
    if (!findLine) {
      this.houghAddPoint(ot1);
    }
  }

  public void endFrame() {

    for (LineObject tmo : this.mvObjs) {
      if (tmo.frameList.size() >= minValidFrame) {

      }
    }
  }

  public void clearAllPoint(LineObject tline) {

    for (HoughFrame tFrame : tline.frameList) {
      ArrayList<HoughtPoint> tPoints = (ArrayList<HoughtPoint>) tFrame.pointList;
      for (HoughtPoint tPoint : tPoints) {
//      System.out.println(String.format("target %5d %5d", tPoint.getpIdx(), ot1.getFrameNumber()));
        for (int t = 0; t < thetaSize; t++) {
          int r = (int) ((((tPoint.getX() - imgXCenter) * cosCache[t]) + ((tPoint.getY() - imgYCenter) * sinCache[t]) + halfRho) / rhoStep);
          HoughLine tline2 = houghArray[t][r];
          tline2.removePoint(tPoint);
        }
      }
    }
  }

  public void drawPoint(String fName) {

    int colorLength = 1000;
    Color colors[] = new Color[colorLength];
    Random random = new Random();
    for (int i = 0; i < colorLength; i++) {
      colors[i] = new Color(100 + random.nextInt(156), 100 + random.nextInt(156), 100 + random.nextInt(156));
//      colors[i] = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

//    BufferedImage image = new BufferedImage(imgWidth*2, imgHeight*2, BufferedImage.TYPE_INT_ARGB);
    BufferedImage image = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = image.createGraphics();
    BasicStroke bs = new BasicStroke(2);
    BasicStroke bs2 = new BasicStroke(3);
    BasicStroke bs3 = new BasicStroke(7);
    Font font1 = new Font("Times New Roman", Font.BOLD, 30);
    Font font2 = new Font("Times New Roman", Font.BOLD, 12);
    g2d.translate(0, imgHeight);
    g2d.scale(1, -1);
    g2d.setBackground(Color.WHITE);
    g2d.fillRect(0, 0, imgWidth, imgHeight);
    g2d.setStroke(bs);
    g2d.setColor(Color.RED);
    g2d.drawOval(imgWidth - 40, imgHeight - 40, 20, 20);

    int pointSize = 12;
    int pointSize2 = 6;

    int j = 0;
    for (LineObject mvObj : mvObjs) {

      if (mvObj.pointNumber < validLineMinPoint) {
        continue;
      }

      HoughtPoint firstOT1 = mvObj.firstPoint;
      HoughtPoint lastOT1 = mvObj.lastPoint;

      int x1 = (int) firstOT1.getX();
      int y1 = (int) firstOT1.getY();
      int x2 = (int) lastOT1.getX();
      int y2 = (int) lastOT1.getY();

      if (mvObj.frameList.size() == 1) {
        g2d.setColor(Color.BLACK);
        g2d.setStroke(bs3);
      } else {
        g2d.setColor(colors[j % colorLength]);
        g2d.setStroke(bs);
      }
      g2d.drawLine(x1, y1, x2, y2);
      g2d.setStroke(bs3);
      g2d.drawRect(x1 - pointSize / 2, y1 - pointSize / 2, pointSize, pointSize);
      g2d.drawRect(x2 - pointSize / 2, y2 - pointSize / 2, pointSize, pointSize);

      String drawStr = "";
      g2d.setStroke(bs2);

      for (HoughFrame tFrame : mvObj.frameList) {
        for (HoughtPoint tPoint : tFrame.pointList) {
          g2d.setColor(colors[j % colorLength]);
          int x = (int) (tPoint.getX() - pointSize2 / 2);
          int y = (int) (tPoint.getY() - pointSize2 / 2);
          g2d.drawRect(x, y, pointSize2, pointSize2);
        }

        g2d.setColor(Color.BLACK);
        g2d.setFont(font1);
//        drawStr = "" + (j + 1) + "," + String.format("%.2f", mvObj.theta * 180 / Math.PI);
        drawStr = "" + (j);
        g2d.drawString(drawStr, (int) lastOT1.getX() + pointSize, (int) lastOT1.getY() + pointSize);
      }

      String debugStr = String.format("line%03d: theta=%6.2f Deg, theta=%4.2f arc, theta=%6.2f, "
              + "rho=%10.5f, rho=%10.5f, lastRho=%10.5f, lastRho=%10.5f, pnumber=%3d, fnumber=%3d, avgfNumber=%5.2f",
              j, mvObj.theta * 180 / Math.PI, mvObj.theta, mvObj.theta / thetaStep,
              mvObj.rho / rhoStep, mvObj.rho, mvObj.lastRho / rhoStep, mvObj.lastRho,
              mvObj.pointNumber, mvObj.frameList.size(), mvObj.avgFramePointNumber);
      System.out.println(debugStr);
//      mvObj.updateThetaRho();
//      mvObj.printInfo(historyOT1s);

      j++;
    }

    int singlePoint = 0;
    int singleFrame = 0;
    int multiPoint = 0;
    for (LineObject mvObj : mvObjs) {
      if (mvObj.pointNumber < validLineMinPoint) {
        continue;
      }
      if (mvObj.frameList.size() == 1) {
        singleFrame++;
      } else {
        if (mvObj.avgFramePointNumber < 2) {
          singlePoint++;
        } else {
          multiPoint++;
        }
      }
    }
    System.out.println("singleFrame=" + singleFrame + ", singlePoint=" + singlePoint + ", multiPoint=" + multiPoint);

    try {
      javax.imageio.ImageIO.write(image, "png", new File(fName));
    } catch (IOException ex) {
      Logger.getLogger(HoughTransform.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public void drawPoint2(String fName) {

    int colorLength = 1000;
    Color colors[] = new Color[colorLength];
    Random random = new Random();
    for (int i = 0; i < colorLength; i++) {
//      colors[i] = new Color(100 + random.nextInt(156), 100 + random.nextInt(156), 100 + random.nextInt(156));
      colors[i] = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    BufferedImage image = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = image.createGraphics();
    BasicStroke bs = new BasicStroke(2);
    BasicStroke bs2 = new BasicStroke(3);
    BasicStroke bs3 = new BasicStroke(7);
    Font font1 = new Font("Times New Roman", Font.BOLD, 30);
    Font font2 = new Font("Times New Roman", Font.BOLD, 12);
//    g2d.translate(0, imgHeight);
//    g2d.scale(1, -1);
    g2d.setBackground(Color.WHITE);
    g2d.fillRect(0, 0, imgWidth, imgHeight);
    g2d.setStroke(bs);
    g2d.setColor(Color.RED);
    g2d.drawOval(imgWidth - 40, imgHeight - 40, 20, 20);

    int pointSize = 12;
    int pointSize2 = 6;
    // draw the lines back onto the image 
    int totalLine = 0;

    Integer idxArray[] = {66, 88};
    ArrayList<Integer> idxList = new ArrayList(Arrays.asList(idxArray));

    int j = 0;
    for (LineObject mvObj : mvObjs) {

      if (mvObj.pointNumber < validLineMinPoint) {
        continue;
      }

      if (!(idxList.contains(new Integer(j)))) {
        j++;
        continue;
      }

      g2d.setColor(colors[j % colorLength]);

      HoughtPoint firstOT1 = mvObj.firstPoint;
      HoughtPoint lastOT1 = mvObj.lastPoint;

      int x1 = (int) firstOT1.getX();
      int y1 = (int) firstOT1.getY();
      int x2 = (int) lastOT1.getX();
      int y2 = (int) lastOT1.getY();
      g2d.setStroke(bs);
      g2d.drawLine(x1, y1, x2, y2);
      g2d.setStroke(bs3);
      g2d.drawRect(x1 - pointSize / 2, y1 - pointSize / 2, pointSize, pointSize);
      g2d.drawRect(x2 - pointSize / 2, y2 - pointSize / 2, pointSize, pointSize);

      String drawStr = "";

      g2d.setFont(font2);
      drawStr = "" + firstOT1.getpIdx();
      g2d.drawString(drawStr, (int) x1 + pointSize, (int) y1 - pointSize);
      drawStr = "" + lastOT1.getpIdx();
      g2d.drawString(drawStr, (int) x2 + pointSize, (int) y2 - pointSize);

      g2d.setStroke(bs2);

      for (HoughFrame tFrame : mvObj.frameList) {
        for (HoughtPoint tPoint : tFrame.pointList) {
          g2d.setColor(colors[j % colorLength]);
          int x = (int) (tPoint.getX() - pointSize2 / 2);
          int y = (int) (tPoint.getY() - pointSize2 / 2);
          g2d.drawRect(x, y, pointSize2, pointSize2);

          g2d.setFont(font2);
          drawStr = "" + tPoint.getpIdx();
          g2d.drawString(drawStr, (int) x + pointSize, (int) y - pointSize);
        }

        g2d.setColor(Color.BLACK);
        g2d.setFont(font1);
        drawStr = "" + (j);
        g2d.drawString(drawStr, (int) lastOT1.getX() + 3 * pointSize, (int) lastOT1.getY() + pointSize);
      }

      String debugStr = String.format("line%03d: theta=%6.2f Deg, theta=%4.2f arc, theta=%6.2f, "
              + "rho=%10.5f, rho=%10.5f, lastRho=%10.5f, lastRho=%10.5f, pnumber=%3d, fnumber=%3d, avgfNumber=%5.2f",
              j, mvObj.theta * 180 / Math.PI, mvObj.theta, mvObj.theta / thetaStep,
              mvObj.rho / rhoStep, mvObj.rho, mvObj.lastRho / rhoStep, mvObj.lastRho,
              mvObj.pointNumber, mvObj.frameList.size(), mvObj.avgFramePointNumber);
      System.out.println(debugStr);
      if (idxList.contains(j)) {
        mvObj.printInfo(historyOT1s);
      }

      j++;
    }

    try {
      javax.imageio.ImageIO.write(image, "png", new File(fName));
    } catch (IOException ex) {
      Logger.getLogger(HoughTransform.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

}

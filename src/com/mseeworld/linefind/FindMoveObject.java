/*
 * mseeworld工作室，致力于人工智能研究。Email: xyag.902@163.com
 */
package com.mseeworld.linefind;

import com.gwac.model.OtObserveRecord;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author xy
 */
public class FindMoveObject {

  // cache of values of sin and cos for different theta values. Has a significant performance improvement. 
  private double[] sinCache;
  private double[] cosCache;

  // the number of points that have been added 
  protected int numOT1s;
  HoughLine[][] houghArray;
  ArrayList<OtObserveRecord> historyOT1s;
  public ArrayList<LineObject> mvObjs;
  ArrayList<LineObject> fastObjs;
  ArrayList<LineObject> singleFrameObjs;

  int maxHoughFrameNunmber;
  int objInitMinPoint;
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
  double thetaStep;
  double rhoStep;

  float maxDistance;
  float rhoErrorTimes;
  int validLineMinPoint;

  public FindMoveObject() {

    this.imgWidth = LineParameterConfig.imgWidth;
    this.imgHeight = LineParameterConfig.imgHeight;
    this.imgXCenter = LineParameterConfig.imgWidth / 2;
    this.imgYCenter = LineParameterConfig.imgHeight / 2;

    this.maxTheta = Math.PI;
    this.maxHoughFrameNunmber = LineParameterConfig.maxHoughFrameNunmber;
    this.objInitMinPoint = LineParameterConfig.objInitMinPoint;
    this.maxDistance = LineParameterConfig.maxDistance;
    this.maxRho = Math.sqrt(Math.pow(LineParameterConfig.imgWidth, 2) + Math.pow(LineParameterConfig.imgHeight, 2));
    this.halfRho = this.maxRho / 2;
    this.thetaSize = LineParameterConfig.thetaSize;
    this.rhoSize = LineParameterConfig.rhoSize;
    this.thetaStep = this.maxTheta / LineParameterConfig.thetaSize;
    this.rhoStep = this.maxRho / LineParameterConfig.rhoSize;

    this.rhoErrorTimes = LineParameterConfig.rhoErrorTimes;
    this.validLineMinPoint = LineParameterConfig.validLineMinPoint;

    initialise();
  }

  private void initialise() {

    this.historyOT1s = new ArrayList();
    this.mvObjs = new ArrayList();
    this.fastObjs = new ArrayList();
    this.singleFrameObjs = new ArrayList();

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
        houghArray[t][r] = new HoughLine((float) (t * thetaStep), (float) (r * rhoStep));
      }
    }

  }

  public void addFrame(List<OtObserveRecord> singleFrame) {

    for (OtObserveRecord ot1 : singleFrame) {
      this.historyAddPoint(ot1);
      this.lineAddPoint(ot1);
    }
    this.endFrame();
  }
  
  public void findSingleFrameLine(List<OtObserveRecord> singleFrame){
    
  }

  public void historyAddPoint(OtObserveRecord ot1) {
    historyOT1s.add(ot1);
    numOT1s++;
  }

  public void houghAddPoint(OtObserveRecord ot1) {

    for (int t = 0; t < thetaSize; t++) {

      float fr = (float) ((((ot1.getX() - imgXCenter) * cosCache[t]) + ((ot1.getY() - imgYCenter) * sinCache[t]) + halfRho) / rhoStep);
      int r = Math.round(fr);

      if (r < 0 || r >= this.rhoSize) {
        System.out.println("x=" + ot1.getX() + ",y=" + ot1.getY() + ",theta=" + t + ",rho=" + r);
        continue;
      }

      HoughLine tline = houghArray[t][r];
      int curNumber = ot1.getFfNumber();
      tline.removeOldFrame(curNumber - this.maxHoughFrameNunmber);

      if (tline.matchLastPoint(ot1, maxDistance)) {
        tline.addPoint(numOT1s - 1, curNumber, ot1.getX(), ot1.getY(), ot1.getDateUt(), ot1.getOorId());
        if (tline.validSize() >= this.objInitMinPoint) {
          double tsigma = tline.lineRegression();
          if ((tsigma < 2 && tline.validSize() >= this.objInitMinPoint) || (tline.validSize() >= 10)) {
            LineObject lineObj = new LineObject((float) (t * thetaStep), (float) (r * rhoStep));
            lineObj.cloneLine(tline);
            mvObjs.add(lineObj);
            clearAllPoint(lineObj);
            break;
          }
        }
      }
    }

  }

  public void lineAddPoint(OtObserveRecord ot1) {

    boolean findLine = false;
    int i = 0;
    for (LineObject tline : this.mvObjs) {
      if (!tline.isEndLine(ot1.getFfNumber() - this.maxHoughFrameNunmber + 1)) {
        if (tline.isOnLine(ot1)) {
          tline.addPoint(numOT1s - 1, ot1.getFfNumber(), ot1.getX(), ot1.getY(), ot1.getDateUt(), ot1.getOorId());
          findLine = true;
          break;
        }
      }
    }
    if (!findLine) {
      this.houghAddPoint(ot1);
    }
  }

  public void lineAddPoint2(OtObserveRecord ot1) {

    boolean findLine = false;
    int i = 0;
    for (LineObject tline : this.mvObjs) {

      if (!tline.isEndLine(ot1.getFfNumber() - this.maxHoughFrameNunmber + 1)) {
        double preYDiff = Math.abs(ot1.getY() - tline.preNextYByX(ot1.getX()));
        if (preYDiff < 10) {
          double preXDiff = Math.abs(ot1.getX() - tline.preNextXByT(ot1.getDateUt().getTime()));
          double preYDiff2 = Math.abs(ot1.getY() - tline.preNextYByT(ot1.getDateUt().getTime()));
          if (preXDiff < 10 && preYDiff2 < 10) {
            tline.addPoint(numOT1s - 1, ot1.getFfNumber(), ot1.getX(), ot1.getY(), ot1.getDateUt(), ot1.getOorId());
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

  }

  public void endAllFrame() {

    for (LineObject tmo : this.mvObjs) {
      if (!tmo.endLine) {
        tmo.isEndLine(Integer.MAX_VALUE);
      }
    }

  }

  public void clearAllPoint(LineObject tline) {

    for (HoughFrame tFrame : tline.frameList) {
      ArrayList<HoughtPoint> tPoints = (ArrayList<HoughtPoint>) tFrame.pointList;
      for (HoughtPoint tPoint : tPoints) {
//      System.out.println(String.format("target %5d %5d", tPoint.getpIdx(), ot1.getFrameNumber()));
        for (int t = 0; t < thetaSize; t++) {
          int r = Math.round((float) ((((tPoint.getX() - imgXCenter) * cosCache[t]) + ((tPoint.getY() - imgYCenter) * sinCache[t]) + halfRho) / rhoStep));
          HoughLine tline2 = houghArray[t][r];
          tline2.removePoint(tPoint);
        }
      }
    }
  }

  public void saveLine(String fpath) {

    File root = new File(fpath);
    if (root.exists()) {
      root.delete();
    }
    root.mkdirs();

    Integer idxArray[] = {14, 16, 60, 99, 100};
    ArrayList<Integer> idxList = new ArrayList(Arrays.asList(idxArray));

    int j = 0;
    for (LineObject mvObj : mvObjs) {

      if (mvObj.pointNumber < validLineMinPoint || mvObj.frameList.size() <= 20) {
        j++;
        continue;
      }

//      if (!(idxList.contains(new Integer(j)))) {
//        j++;
//        continue;
//      }
      FileOutputStream out = null;
      try {
        String fname = fpath + String.format("%03d.txt", j);
        out = new FileOutputStream(new File(fname));
//        out.write("pIdx, frameNumber, x, y, xDelta, yDelta, fnDelta, timeDelta, xSpeedfn, ySpeedfn, xSpeedt, ySpeedt\n".getBytes());
        out.write("pIdx\t fmNum\t x\t y\t xDelta\t yDelta\t fnDelta\t timeDelta\t xSpeedt\t ySpeedt\t preX\t preY\t preDeltaX\t preDeltaY\n".getBytes());
        int i = 0;
        for (HoughtPoint tPoint : mvObj.pointList) {
//          OtObserveRecord tp = this.historyOT1s.get(tPoint.getpIdx());
//              out.write(String.format("%4d, %s, %10.6f, %10.6f, %11.6f, %11.6f, %6.3f\n", 
//                      tp.number, tp.dateStr, tp.ra, tp.dec, tp.x, tp.y, tp.mag).getBytes());
//          out.write(String.format("%4d\t%s\t%10.6f\t%10.6f\t%11.6f\t%11.6f\t%6.3f\n",
//                  tp.getFrameNumber(), tp.getDateStr(), tp.getRa(), tp.getDec(), tp.getX(), tp.getY(), tp.getMag()).getBytes());
//          System.out.println(tPoint.getAllInfo());
          out.write((tPoint.getAllInfo() + "\n").getBytes());
        }
        out.close();
      } catch (FileNotFoundException ex) {
        Logger.getLogger(FindMoveObject.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IOException ex) {
        Logger.getLogger(FindMoveObject.class.getName()).log(Level.SEVERE, null, ex);
      } finally {
        try {
          out.close();
        } catch (IOException ex) {
          Logger.getLogger(FindMoveObject.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
      j++;
    }
  }

}

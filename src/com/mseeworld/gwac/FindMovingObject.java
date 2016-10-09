/*
 * mseeworld工作室，致力于人工智能研究。Email: xyag.902@163.com
 */
package com.mseeworld.gwac;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author xy
 */
public class FindMovingObject {

  int imgWidth = 3072;
  int imgHeight = 3072;
  ArrayList<OT1> ot1list = new ArrayList();

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    FindMovingObject fmo = new FindMovingObject();
    fmo.findMovingObject();
  }

  public void findMovingObject() {
    int thetaSize = 180;
    int rhoSize = 100;
    int thetaRange = 36;
    int rhoRange = 10;
    int maxHoughFrameNunmber = 10;
    int minValidPoint = 4;
    float maxDistance = 100;
    float rhoErrorTimes = (float) 1;
    int validLineMinPoint = 5;

//    String[] dates = {"151218-2-34","151218-3-5", "151218-3-36", "151218-6-12",
//      "151218-6-13", "151218-7-15", "151218-8-15", "151218-9-21", "151218-11-34"};
    String[] dates = {"151218-2-34"}; //debug2line  151218-2-34

    for (String tname : dates) {
      ot1list.clear();

      HoughTransform ht = new HoughTransform(imgWidth, imgHeight, thetaSize, rhoSize, thetaRange, rhoRange, maxHoughFrameNunmber, minValidPoint, maxDistance, rhoErrorTimes, validLineMinPoint);

      String ot1File = "E:\\work\\program\\java\\netbeans\\JavaApplication2\\resources\\source-list-old\\" + tname + ".txt";
      String outImage = "E:\\work\\program\\java\\netbeans\\JavaApplication2\\resources\\" + tname + "\\" + tname + "-outline.png";
      String houghImage = "E:\\work\\program\\java\\netbeans\\JavaApplication2\\resources\\" + tname + "\\hough.png";
      String outPath = "E:\\work\\program\\java\\netbeans\\JavaApplication2\\resources\\" + tname + "\\lines2\\";

      getOT1(ot1File);

      int lastFrameNumber = 0;
      int frameCount = 0;
      int pNum = 0;
      for (OT1 ot1 : ot1list) {
//        if ((pNum >= 911 && pNum <= 923)) {
//          System.out.print("pIdx=" + (pNum + ": "));
//          ot1.printInfo();
//        }
        if (lastFrameNumber != ot1.getFrameNumber()) {
          lastFrameNumber = ot1.getFrameNumber();
          ht.endFrame();
        }
        ht.historyAddPoint(ot1);
        ht.lineAddPoint(ot1);

        pNum++;
      }

      ht.drawPoint(outImage);
//      ht.drawHoughImage(houghImage);
    }
  }

  public void getOT1(String ot1file) {

    File file = new File(ot1file);
    BufferedReader reader = null;
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
      reader = new BufferedReader(new FileReader(file));
      String tempString = null;
      int tline = 0;
      int tline2 = 0;
      //2318.9	381.724	170.284	4.01298	2015/12/18 18:25:28	10.7607	1777	34	1626.55	2273.18
      while ((tempString = reader.readLine()) != null) {
        String[] tstr = tempString.split("\t");
        float x = Float.parseFloat(tstr[0]);
        float y = Float.parseFloat(tstr[1]);  //1
        float ra = Float.parseFloat(tstr[2]); //2
        float dec = Float.parseFloat(tstr[3]);
        float mag = Float.parseFloat(tstr[5]);
        int number = Integer.parseInt(tstr[6]);
//        float xTemp = Float.parseFloat(tstr[8]);
//        float yTemp = Float.parseFloat(tstr[9]);
        float xTemp = 0;
        float yTemp = 0;
        Date tdate = sdf.parse(tstr[4]);

//        if (!(x > 2345 && x < 2370 && y > 715 && y < 750)) {
        ot1list.add(new OT1(number, x, imgHeight - y, xTemp, imgHeight - yTemp, ra, dec, mag, tdate, tstr[4]));
        tline++;
//        } else {
//          tline2++;
//        }
      }
      reader.close();
      System.out.println("total points:" + tline + ", remove:" + tline2);

    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParseException ex) {
      Logger.getLogger(FindMovingObject.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e1) {
        }
      }
    }
  }

}

/*
 * mseeworld工作室，致力于人工智能研究。Email: xyag.902@163.com
 */
package com.mseeworld.linefind;

import com.gwac.model.OtObserveRecord;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Scanner;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 *
 * @author xy
 */
public class Main {

  ArrayList<OtObserveRecord> ot1list = new ArrayList();

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    Main fmo = new Main();
    fmo.findMovingObject();
  }

  public void findMovingObject() {

//    String[] dates = {"160928-1-5", "160928-3-10", "160928-5-11", "160928-6-11", "160928-7-12",
//      "160928-7-16", "160928-8-12", "160928-8-16", "160928-11-5", "160928-12-5", "160928-1-5"};
    String[] dates = {"170108-8-30"}; //170108-1-12 170108-8-30

    for (String tname : dates) {
      ot1list.clear();

      String ot1File = "E:\\work\\program\\java\\netbeans\\LineFinder\\resources\\170108\\" + tname + ".txt";
      String outImagePoint = "E:\\work\\program\\java\\netbeans\\LineFinder\\resources\\170108\\" + tname + "-point-all.png";

      getOT1(ot1File);
//      drawPoint(outImagePoint);

      processOneDay(ot1list, tname, 0, 0);
    }
  }

  public void processOneDay(List<OtObserveRecord> oors, String dateStr, int dpmId, int skyId) {

    System.out.println(dateStr + ": " + oors.size());
    FindMoveObject fmo = new FindMoveObject();

    int lastFrameNumber = 0;
    List<OtObserveRecord> singleFrame = new ArrayList<>();
    for (OtObserveRecord oor : oors) {
      oor.setX(oor.getXTemp());
      oor.setY(oor.getYTemp());
      if (lastFrameNumber != oor.getFfNumber()) {
        lastFrameNumber = oor.getFfNumber();
        fmo.addFrame(singleFrame);
        singleFrame.clear();
      } else {
        singleFrame.add(oor);
      }
    }

    fmo.endAllFrame();

//    for (LineObject obj : ht.mvObjs) {
//      if (obj.pointNumber >= validLineMinPoint && obj.isValidLine()) {
//        saveLineObject(obj, dateStr, dpmId, skyId);
//      }
//    }
    String imgPath = "E:\\" + dateStr + "-" + dpmId + "-" + skyId + ".png";
    DrawObject dObj = new DrawObject(fmo);
    dObj.drawObjsAll(imgPath);
  }

  public void getOT1(String ot1file) {

    File file = new File(ot1file);
    BufferedReader reader = null;
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
      SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
      reader = new BufferedReader(new FileReader(file));
      String tempString = null;
      int tline = 0;
      int tline2 = 0;
      int tlen = "2017/1/3 15:54:17".length();
      //2318.9	381.724	170.284	4.01298	2015/12/18 18:25:28	10.7607	1777	34	1626.55	2273.18
      while ((tempString = reader.readLine()) != null) {
        if (tempString.isEmpty()) {
          continue;
        }
        String[] tstr = tempString.split("\t");
        float x = Float.parseFloat(tstr[0]);
        float y = Float.parseFloat(tstr[1]);  //1
        float ra = Float.parseFloat(tstr[2]); //2
        float dec = Float.parseFloat(tstr[3]);
        String dateStr = tstr[4];
        float mag = Float.parseFloat(tstr[5]);
        int number = Integer.parseInt(tstr[6]);
        float xTemp = Float.parseFloat(tstr[8]);
        float yTemp = Float.parseFloat(tstr[9]);
        Date tdate = null;
        if (tstr[4].trim().length() == tlen) {
          tdate = sdf.parse(tstr[4]);
        } else {
          tdate = sdf2.parse(tstr[4]);
        }

        OtObserveRecord ot1 = new OtObserveRecord();
        ot1.setX(x);
        ot1.setY(y);
        ot1.setRaD(ra);
        ot1.setDecD(dec);
        ot1.setMagAper(mag);
        ot1.setFfNumber(number);
        ot1.setXTemp(x);
        ot1.setYTemp(y);
        ot1.setDateUt(tdate);
        ot1.setDateStr(dateStr);

        ot1list.add(ot1);
        tline++;
      }
      reader.close();

    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParseException ex) {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e1) {
        }
      }
    }
  }

  public void drawPoint(String fName) {

    BufferedImage image = new BufferedImage(LineParameterConfig.imgWidth, LineParameterConfig.imgHeight, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = image.createGraphics();
    BasicStroke bs = new BasicStroke(2);
    g2d.setBackground(Color.WHITE);
    g2d.fillRect(0, 0, LineParameterConfig.imgWidth, LineParameterConfig.imgHeight);
    g2d.setStroke(bs);
    g2d.setColor(Color.RED);
    int pointSize2 = 6;

    for (OtObserveRecord ot1 : ot1list) {
      int x = (int) (ot1.getX() - pointSize2 / 2);
      int y = (int) (ot1.getY() - pointSize2 / 2);
      g2d.drawRect(x, y, pointSize2, pointSize2);
    }

    try {
      javax.imageio.ImageIO.write(image, "png", new File(fName));
    } catch (IOException ex) {
      Logger.getLogger(FindMoveObject.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

}

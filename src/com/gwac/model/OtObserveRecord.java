package com.gwac.model;
// Generated 2015-10-2 9:40:37 by Hibernate Tools 3.6.0

import java.util.Date;

/**
 * OtObserveRecord generated by hbm2java
 */
public class OtObserveRecord implements java.io.Serializable {

  private long oorId;
  private Long otId;
  private Long ffId;
  private Long ffcId;
  private Short otTypeId;
  private Float raD;
  private Float decD;
  private Float x;
  private Float y;
  private Float XTemp;
  private Float YTemp;
  private Date dateUt;
  private Float flux;
  private Boolean flag;
  private Float flagChb;
  private Float background;
  private Float threshold;
  private Float magAper;
  private Float magerrAper;
  private Float ellipticity;
  private Float classStar;
  private Boolean otFlag;
  private Integer ffNumber;
  private Integer dpmId;
  private String dateStr;
  private Boolean requestCut;
  private Boolean successCut;
  private Short skyId;
  private Float distance;
  private Float deltamag;
  private Character dataProduceMethod;

  public OtObserveRecord() {
  }

  public OtObserveRecord(long oorId) {
    this.oorId = oorId;
  }

  public OtObserveRecord(long oorId, Long otId, Long ffId, Long ffcId, Short otTypeId, Float raD, Float decD, Float x, Float y, Float XTemp, Float YTemp, Date dateUt, Float flux, Boolean flag, Float flagChb, Float background, Float threshold, Float magAper, Float magerrAper, Float ellipticity, Float classStar, Boolean otFlag, Integer ffNumber, Integer dpmId, String dateStr, Boolean requestCut, Boolean successCut, Short skyId, Float distance, Float deltamag, Character dataProduceMethod) {
    this.oorId = oorId;
    this.otId = otId;
    this.ffId = ffId;
    this.ffcId = ffcId;
    this.otTypeId = otTypeId;
    this.raD = raD;
    this.decD = decD;
    this.x = x;
    this.y = y;
    this.XTemp = XTemp;
    this.YTemp = YTemp;
    this.dateUt = dateUt;
    this.flux = flux;
    this.flag = flag;
    this.flagChb = flagChb;
    this.background = background;
    this.threshold = threshold;
    this.magAper = magAper;
    this.magerrAper = magerrAper;
    this.ellipticity = ellipticity;
    this.classStar = classStar;
    this.otFlag = otFlag;
    this.ffNumber = ffNumber;
    this.dpmId = dpmId;
    this.dateStr = dateStr;
    this.requestCut = requestCut;
    this.successCut = successCut;
    this.skyId = skyId;
    this.distance = distance;
    this.deltamag = deltamag;
    this.dataProduceMethod = dataProduceMethod;
  }

  public long getOorId() {
    return this.oorId;
  }

  public void setOorId(long oorId) {
    this.oorId = oorId;
  }

  public Long getOtId() {
    return this.otId;
  }

  public void setOtId(Long otId) {
    this.otId = otId;
  }

  public Long getFfId() {
    return this.ffId;
  }

  public void setFfId(Long ffId) {
    this.ffId = ffId;
  }

  public Long getFfcId() {
    return this.ffcId;
  }

  public void setFfcId(Long ffcId) {
    this.ffcId = ffcId;
  }

  public Short getOtTypeId() {
    return this.otTypeId;
  }

  public void setOtTypeId(Short otTypeId) {
    this.otTypeId = otTypeId;
  }

  public Float getRaD() {
    return this.raD;
  }

  public void setRaD(Float raD) {
    this.raD = raD;
  }

  public Float getDecD() {
    return this.decD;
  }

  public void setDecD(Float decD) {
    this.decD = decD;
  }

  public Float getX() {
    return this.x;
  }

  public void setX(Float x) {
    this.x = x;
  }

  public Float getY() {
    return this.y;
  }

  public void setY(Float y) {
    this.y = y;
  }

  public Float getXTemp() {
    return this.XTemp;
  }

  public void setXTemp(Float XTemp) {
    this.XTemp = XTemp;
  }

  public Float getYTemp() {
    return this.YTemp;
  }

  public void setYTemp(Float YTemp) {
    this.YTemp = YTemp;
  }

  public Date getDateUt() {
    return this.dateUt;
  }

  public void setDateUt(Date dateUt) {
    this.dateUt = dateUt;
  }

  public Float getFlux() {
    return this.flux;
  }

  public void setFlux(Float flux) {
    this.flux = flux;
  }

  public Boolean getFlag() {
    return this.flag;
  }

  public void setFlag(Boolean flag) {
    this.flag = flag;
  }

  public Float getFlagChb() {
    return this.flagChb;
  }

  public void setFlagChb(Float flagChb) {
    this.flagChb = flagChb;
  }

  public Float getBackground() {
    return this.background;
  }

  public void setBackground(Float background) {
    this.background = background;
  }

  public Float getThreshold() {
    return this.threshold;
  }

  public void setThreshold(Float threshold) {
    this.threshold = threshold;
  }

  public Float getMagAper() {
    return this.magAper;
  }

  public void setMagAper(Float magAper) {
    this.magAper = magAper;
  }

  public Float getMagerrAper() {
    return this.magerrAper;
  }

  public void setMagerrAper(Float magerrAper) {
    this.magerrAper = magerrAper;
  }

  public Float getEllipticity() {
    return this.ellipticity;
  }

  public void setEllipticity(Float ellipticity) {
    this.ellipticity = ellipticity;
  }

  public Float getClassStar() {
    return this.classStar;
  }

  public void setClassStar(Float classStar) {
    this.classStar = classStar;
  }

  public Boolean getOtFlag() {
    return this.otFlag;
  }

  public void setOtFlag(Boolean otFlag) {
    this.otFlag = otFlag;
  }

  public Integer getFfNumber() {
    return this.ffNumber;
  }

  public void setFfNumber(Integer ffNumber) {
    this.ffNumber = ffNumber;
  }

  public Integer getDpmId() {
    return this.dpmId;
  }

  public void setDpmId(Integer dpmId) {
    this.dpmId = dpmId;
  }

  public String getDateStr() {
    return this.dateStr;
  }

  public void setDateStr(String dateStr) {
    this.dateStr = dateStr;
  }

  public Boolean getRequestCut() {
    return this.requestCut;
  }

  public void setRequestCut(Boolean requestCut) {
    this.requestCut = requestCut;
  }

  public Boolean getSuccessCut() {
    return this.successCut;
  }

  public void setSuccessCut(Boolean successCut) {
    this.successCut = successCut;
  }

  public Short getSkyId() {
    return this.skyId;
  }

  public void setSkyId(Short skyId) {
    this.skyId = skyId;
  }

  public Float getDistance() {
    return this.distance;
  }

  public void setDistance(Float distance) {
    this.distance = distance;
  }

  public Float getDeltamag() {
    return this.deltamag;
  }

  public void setDeltamag(Float deltamag) {
    this.deltamag = deltamag;
  }

  public Character getDataProduceMethod() {
    return this.dataProduceMethod;
  }

  public void setDataProduceMethod(Character dataProduceMethod) {
    this.dataProduceMethod = dataProduceMethod;
  }

  public void distance(float x, float y) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  public void printInfo() {
    System.out.println(String.format("%f\t%f\t%f\t%f\t%s\t%f\t%d\t0\t%d",
            this.x, this.y, this.raD, this.decD, this.dateStr, this.magAper, this.ffNumber, this.oorId));
  }

  @Override
  public String toString() {
    return String.format("%f\t%f\t%f\t%f\t%s\t%f\t%d\t0\t%d",
            this.x, this.y, this.raD, this.decD, this.dateStr, this.magAper, this.ffNumber, this.oorId);
  }
}

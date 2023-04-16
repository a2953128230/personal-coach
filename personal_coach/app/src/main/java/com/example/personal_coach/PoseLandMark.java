package com.example.personal_coach;

import android.util.Log;

import com.google.mediapipe.formats.proto.LandmarkProto;

import java.util.ArrayList;

public class PoseLandMark {
    float poseCounter;    // 計數器
    static int direction;
    private static final String TAG = "PoseLandMark";
    float x,y, visible;
    PoseLandMark(){
        poseCounter = 0;
        direction = 0;
    }
    PoseLandMark(float x, float y, float visible) {
        this.x = x;
        this.y = y;
        this.visible = visible;
        poseCounter = 0;
        direction = 0;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setVisible(float visible) {
        this.visible = visible;
    }
    public void setPoseCounter(float count){
        this.poseCounter = count;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getVisible() {
        return visible;
    }
    public float getPoseCounter(){
        return poseCounter;
    }

    /* 解析人體骨架的關鍵點 */
    public static ArrayList<PoseLandMark> getPoseMarkers(LandmarkProto.NormalizedLandmarkList key) {
        ArrayList<PoseLandMark> poseMarkers = new ArrayList<PoseLandMark>();
        int landmarkIndex = 0;
        for (LandmarkProto.NormalizedLandmark landmark : key.getLandmarkList()) {
            PoseLandMark marker = new PoseLandMark(landmark.getX(), landmark.getY(), landmark.getVisibility());
            ++landmarkIndex;
            poseMarkers.add(marker);
        }
        return poseMarkers;
    }

    /* 偵測到的姿勢關鍵點的數量 */
    public static String getkey(LandmarkProto.NormalizedLandmarkList key){
        ArrayList<PoseLandMark> poseMarkers = getPoseMarkers(key);
        return "Pose landmarks: " + key.getLandmarkCount() + "\n";
    }



    /* 計算並回傳關鍵點之間的角度 */
    public static double getAngle(PoseLandMark firstPoint, PoseLandMark midPoint, PoseLandMark lastPoint) {
        double result =
                Math.toDegrees(
                        Math.atan2(lastPoint.getY() - midPoint.getY(),lastPoint.getX() - midPoint.getX())
                                - Math.atan2(firstPoint.getY() - midPoint.getY(),firstPoint.getX() - midPoint.getX()));
        result = Math.abs(result);
        if (result > 180) {
            result = (360.0 - result);
        }
        return result;
    }

    /* 伏地挺身 */
    public static String pushup(LandmarkProto.NormalizedLandmarkList key){
        ArrayList<PoseLandMark> poseMarkers = getPoseMarkers(key);
        double left_elbow = getAngle(poseMarkers.get(11),poseMarkers.get(13),poseMarkers.get(15));   // 手肘
        double left_greater = getAngle(poseMarkers.get(11), poseMarkers.get(23), poseMarkers.get(25));   // 軀幹
        double right_elbow = getAngle(poseMarkers.get(12),poseMarkers.get(14),poseMarkers.get(16));   // 手肘
        double right_greater = getAngle(poseMarkers.get(12), poseMarkers.get(24), poseMarkers.get(26));   // 軀幹

        String back;
        if( left_elbow <= 90  || right_elbow <= 90  ){
            back = "DOWN";
        }
        else if( left_elbow >= 125  || right_elbow >= 125 ){
            back = "UP";
        }
        else{
            back = "Fix Form";
        }

        /* 輸出關鍵點的角度 */
        Log.v(TAG,"======[Degree Of Position]======\n"+
                "elbow :" + left_elbow + "\n"+
                "greater :" + left_greater + "\n"+
                back
        );

        return back;
    }

    /* 臀橋 */
    public static String GluteBridge(LandmarkProto.NormalizedLandmarkList key){
        ArrayList<PoseLandMark> poseMarkers = getPoseMarkers(key);
        double left_knee = getAngle(poseMarkers.get(23),poseMarkers.get(25),poseMarkers.get(27));   // 膝蓋
        double left_greater = getAngle(poseMarkers.get(11), poseMarkers.get(23), poseMarkers.get(25));   // 軀幹
        double right_knee = getAngle(poseMarkers.get(24),poseMarkers.get(26),poseMarkers.get(28));   // 膝蓋
        double right_greater = getAngle(poseMarkers.get(12), poseMarkers.get(24), poseMarkers.get(26));   // 軀幹

        String back;
        if ( ((left_greater >= 140 && left_greater <= 155) && left_knee >= 50) || ((right_greater >= 140 && right_greater <= 155) && right_knee >= 50) ) {
            back = "DOWN";
        } else if ( (left_greater >= 170 && left_knee >= 50) || (right_greater >= 170 && right_knee >= 50) ) {
            back = "UP";
        } else {
            back = "Fix Form";
        }

        /* 輸出關鍵點的角度 */
        Log.v(TAG,"======[Degree Of Position]======\n"+
                "knee :" + left_knee + "\n" +
                "greater :" + left_greater + "\n" +
                back
        );

        return back;
    }

    /* 仰臥起坐 */
    public static String situp(LandmarkProto.NormalizedLandmarkList key){
        ArrayList<PoseLandMark> poseMarkers = getPoseMarkers(key);
        double left_greater = getAngle(poseMarkers.get(11), poseMarkers.get(23), poseMarkers.get(25));   // 軀幹
        double right_greater = getAngle(poseMarkers.get(12), poseMarkers.get(24), poseMarkers.get(26));   // 軀幹

        String back;
        if ( left_greater <= 90 || right_greater <= 90 ) {
            back = "UP";
        } else if (left_greater <= 135 || right_greater <= 135) {
            back = "DOWN";
        } else {
            back = "Fix Form";
        }

        /* 輸出關鍵點的角度 */
        Log.v(TAG,"======[Degree Of Position]======\n"+
                "greater :" + left_greater + "\n" +
                back
        );

        return back;
    }

    /* 深蹲 */
    public static String squat(LandmarkProto.NormalizedLandmarkList key){
        ArrayList<PoseLandMark> poseMarkers = getPoseMarkers(key);
        double left_knee = getAngle(poseMarkers.get(23),poseMarkers.get(25),poseMarkers.get(27));   // 膝蓋
        double right_knee = getAngle(poseMarkers.get(24),poseMarkers.get(26),poseMarkers.get(28));   // 膝蓋

        String back;
        if ( left_knee <= 105 || right_knee <= 105 ) {
            back = "DOWN";
        } else if (left_knee >= 170 || right_knee >= 170) {
            back = "UP";
        } else {
            back = "Fix Form";
        }

        /* 輸出關鍵點的角度 */
        Log.v(TAG,"======[Degree Of Position]======\n"+
                "knee :" + left_knee + "\n" +
                back
        );

        return back;
    }

    /* 俯臥背伸 */
    public static String prone_back_extension(LandmarkProto.NormalizedLandmarkList key){
        ArrayList<PoseLandMark> poseMarkers = getPoseMarkers(key);
        double left_greater = getAngle(poseMarkers.get(11), poseMarkers.get(23), poseMarkers.get(25));   // 軀幹
        double right_greater = getAngle(poseMarkers.get(12), poseMarkers.get(24), poseMarkers.get(26));   // 軀幹

        String back;
        if ( left_greater <= 165 || right_greater <= 165 ) {
            back = "UP";
        } else if (left_greater >= 170 || right_greater >= 170) {
            back = "DOWN";
        } else {
            back = "Fix Form";
        }

        /* 輸出關鍵點的角度 */
        Log.v(TAG,"======[Degree Of Position]======\n"+
                "greater :" + left_greater + "\n" +
                back
        );

        return back;
    }

}
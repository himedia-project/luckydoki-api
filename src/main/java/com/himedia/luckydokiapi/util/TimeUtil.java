package com.himedia.luckydokiapi.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class TimeUtil {

    /**
     * 시간만큼 대기
     */
    public static void timeSleep(int time) {
        // 브라우저 로딩될 때까지 time만큼  대기
        // HTTP 응답보다 자바의 컴파일 속도가 더 빠르기에
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    /**
     * 랜덤 시간만큼 대기
     * @param time1 시작 시간
     *        time2 끝 시간
     */
    public static void timeSleep(int time1, int time2) {
        Random random = new Random();
        int randomTime = random.nextInt(time2 - time1 + 1) + time1; // time1부터 time2까지의 랜덤 시간 생성
        try {
            Thread.sleep(randomTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getNowTimeStr(String format) {
        LocalDateTime now = LocalDateTime.now();
        return now.format(DateTimeFormatter.ofPattern(format));
    }

    public static String datetimeFormatToString(LocalDateTime localDateTime) {
        if (localDateTime != null) {
            return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        return "";
    }

}

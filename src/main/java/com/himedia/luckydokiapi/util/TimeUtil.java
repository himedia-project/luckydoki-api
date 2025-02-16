package com.himedia.luckydokiapi.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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

    /**
     * 시간이 1시간 미만으로 남았는지 체크
     *
     * @param exp 만료시간
     * @return 1시간 미만이면 true, 아니면 false
     */
    public static boolean checkTime(Integer exp) {

        // JWT exp를 날짜로 변환
        Date expDate = new Date((long) exp * 1000);
        // 현재 시간과의 차이 계산 - 밀리세컨즈
        long gap = expDate.getTime() - System.currentTimeMillis();
        // 분단위 계산
        long leftMin = gap / (1000 * 60);
        return leftMin < 60;
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

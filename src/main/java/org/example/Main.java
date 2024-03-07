package org.example;

import java.io.*;
import java.net.BindException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        int numRepetitions = 1; // 작업 반복 횟수
        double totalTPS = 0;
        int userCount = 500;

        // 여러 번 반복하여 평균 TPS 측정
        for (int i = 0; i < numRepetitions; i++) {
            double tps = runAndMeasureTPS(userCount);
//            System.out.println(tps);
            totalTPS += tps;
        }

        // 평균 TPS 계산
        double averageTPS = totalTPS / numRepetitions;
        System.out.println("Average TPS: " + averageTPS);
    }

    // 작업 실행 및 TPS 측정 메서드
    private static double runAndMeasureTPS(int userCount) {
        long startTime = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(16); // 최대 10개의 스레드를 가지는 스레드 풀 생성

        // 작업을 스레드 풀에 제출
        for (int i = 0; i < userCount; i++) {
            Runnable worker = new TaskRunnable("Task " + i);
            executor.execute(worker); // 스레드 풀에 작업 제출
        }

        // 스레드 풀 종료
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS); // 작업이 모두 종료될 때까지 대기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        //tps 계산
        return userCount / (duration / 1000.0);
    }
}


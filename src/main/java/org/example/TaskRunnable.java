package org.example;

import java.io.*;
import java.net.*;

public class TaskRunnable implements Runnable {
    private final String taskName;
    private static final String SERVER_ADDRESS = "disaster-tcp.alancorp.co.kr";
    private static final int SERVER_PORT = 13579; // 변경 가능한 포트 번호
    private static final String HEX_STRING = "0217000105020021092ED445638E3D5161BBFB10AB7F0098C9EF03";

    public TaskRunnable(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public void run() {
        // 작업 내용
//        System.out.println("Starting task: " + taskName);
        int socketLocalPort = send(SERVER_ADDRESS, SERVER_PORT, HEX_STRING);
//        System.out.println("Completed task: " + taskName + " : " + socketLocalPort);
    }

    private static int send(String serverAddress, int serverPort, String hexString) {
        int socketLocalPort = 0;
        try {
            // 서버에 연결
            SocketAddress socketAddress = new InetSocketAddress(serverAddress,serverPort);

            Socket socket = new Socket();
//            SocketAddress localAddress = new InetSocketAddress(4444);
//            socket.bind(localAddress);
            socket.setReuseAddress(true);
            socket.setSoTimeout(15000);
            socket.connect(socketAddress, 10000);
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream(); // 서버로부터 응답을 받기 위한 InputStream

            // 서버로 메시지 보내기
            byte[] bytes = hexStringToByteArray(hexString);

            // 데이터 송신
            outputStream.write(bytes);
            outputStream.flush();

            // 서버로부터 응답 받기
            byte[] responseBytes = new byte[11];
            socket.getInputStream().read(responseBytes);
//            String responseHexString = byteArrayToHexString(responseBytes);
//            System.out.println("Response from server: " + responseHexString);
            socketLocalPort = socket.getLocalPort();

            // 소켓 닫기
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return socketLocalPort;
    }

    private static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    public static String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}

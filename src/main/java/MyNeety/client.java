package MyNeety;//package com.zcc.io;
import java.io.*;
import java.net.Socket;

public class client {
    public static void main(String[] args) {
        try {
            Socket client = new Socket("192.168.1.147", 9090);
            OutputStream out = client.getOutputStream();

            while (true) {
                InputStream inputStream = System.in;
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String s = reader.readLine();
                if (s != null) {
                    byte[] bb = s.getBytes();
                    out.write(bb);
                }
                InputStream inputStream1 = client.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream1));
                char[] bb = new char[1024];
                int num = in.read(bb);
                System.out.println("HHHHHHHHHHHHHHHHHHHHHHH"+ num);
                if (num > 0) {
                    for (int i = 0; i < num; i++) {
                        System.out.print(bb[i]);
                        Thread.sleep(50);
                    }
                    System.out.println("");
                    client.close();
                    break;
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

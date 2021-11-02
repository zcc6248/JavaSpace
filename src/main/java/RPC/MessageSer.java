package RPC;

import java.io.*;

public class MessageSer {

    static ByteArrayInputStream inputStream = null;
    static ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    public synchronized static <T>T messageDecode(byte[] msg)
    {
        inputStream = new ByteArrayInputStream(msg);
        T obj = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            obj = (T) objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public synchronized static byte[] messageEncode(Object msg){
        outputStream.reset();
        byte[] result = null;
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(msg);
            result = outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}

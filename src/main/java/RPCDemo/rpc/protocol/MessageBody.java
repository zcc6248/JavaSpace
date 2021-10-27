package RPCDemo.rpc.protocol;

import java.io.Serializable;
import java.util.Arrays;

public class MessageBody implements Serializable {
    private String name;
    private String methodname;
    private Class<?>[] methodType;
    private Object[] args;
    private String result;

    public String getName() {
        return name;
    }

    public String getMethodname() {
        return methodname;
    }

    public Class<?>[] getMethodType() {
        return methodType;
    }

    public Object[] getArgs() {
        return args;
    }

    public String getResult() {
        return result;
    }

    public MessageBody(String name, String methodname, Class<?>[] methodType, Object[] args) {
        this.name = name;
        this.methodname = methodname;
        this.methodType = methodType;
        this.args = args;
    }

    public MessageBody(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "content{" +
                "name='" + name + '\'' +
                ", methodname='" + methodname + '\'' +
                ", methodType=" + Arrays.toString(methodType) +
                ", args=" + Arrays.toString(args) +
                ", result='" + result + '\'' +
                '}';
    }
}

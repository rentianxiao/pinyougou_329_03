package cn.itcast.core.pojo.entity;

import java.io.Serializable;

/**
 * 封装操作的结果
 */
public class Result implements Serializable {

    private static final long serialVersionUID = 1L;


    private boolean flag;   //是否成功
    private String message; //操作的信息

    public Result() {
    }

    public Result(boolean flag, String message) {
        this.flag = flag;
        this.message = message;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

package util;
//自定义的异常 可以跑出一些自己提示的错误信息 方便调试问题
public class OrderSystemException extends Exception {
    public OrderSystemException(String message) {
        super(message);
    }
}

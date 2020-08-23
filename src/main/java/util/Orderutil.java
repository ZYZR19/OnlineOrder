package util;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class Orderutil {
    //需要实现读取body的功能
    //需要把整个body读取出来
    public  static  String readBody (HttpServletRequest request) throws UnsupportedEncodingException {
        //先去获取body 的长度
        int length =  request.getContentLength();//http中约定了这个保存body长度 单位是字节
        byte[] buffer = new byte[length] ;
        try {
            InputStream inputStream = request.getInputStream();
            //得到request中内置的字节流
            inputStream.read(buffer,0,length);
            //io流 参数表示缓冲区是谁 往缓冲区哪个位置开始写 写多长 对应三个参数
        } catch (IOException e) {
            e.printStackTrace();
        }
        //把字节数组构造成string  构造的时候必须要指定该字符串的编码方式
        //相当于把字节数据转成字符数据
        return  new String(buffer,"utf-8");

    }
}

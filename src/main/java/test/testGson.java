package test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//演示json
    public class testGson {
       static  class  Student {
           private  String  name;
           private  int id;
           private  double score;

           public Student (String name, int id , double score) {
               this.name = name;
               this.id = id;
               this.score = score;
           }
       }
    public static void main(String[] args) {
        //1.实例化Gson对象(工厂模式)
        //常见的设计模式 是为了规避构造方法的一些缺陷
        //可提供多个版本 必须重载 受限于参数限制
        Gson gson = new GsonBuilder().create();
        //把一个断行转成json字符串
       /* Student student = new Student("张雨蓉",10,100) ;
        String jsonString = gson.toJson(student);
        System.out.println(jsonString);*/
        //也是一个键值对结构

        //把json字符串转回对象
        String str = "{\"name\":\"张雨蓉\",\"id\":10,\"score\":100.0}";
        Student s = gson.fromJson(str,Student.class) ;//参数中类对象也要加入
        System.out.println(s.id + "," + s.name+ "," + s.score);



    }

}

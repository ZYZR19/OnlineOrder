package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.User;
import model.UserDao;
import util.OrderSystemException;
import util.Orderutil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@WebServlet("/register")
public class RegisterSevlet extends HttpServlet {
    private Gson gson = new GsonBuilder().create();
    //要去读取的json请求对象
    static class  Request {
        public  String name;
        public   String password;

    }
    //要去构造的json响应对象
    static class Response {
        public int ok;
        public String reason;

    }

    //在代吗中的某一层统一处理议程 api层处理 返回给用户错误信息
    @Override //注解本质上是一个特殊的类 注解语法的时候就会再加载某个类执行某个方法的时候执行其他的逻辑

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
     //1.读取body中数据
        //2把body数据解析成request对象
        //3,查询数据库 看看当前的用户名是否存在( 如果存在就提示已经被注册了
        //4,吧提交的用户名密码构造成user对象,插入数据库)
        //5.构造响应数据
        Response response = new Response();
        try {
            String body = Orderutil.readBody(req);
        Request request = gson.fromJson(body,Request.class);

        UserDao userDao = new UserDao() ;


            User existuser = userDao.selectByName(request.name);
            if (existuser!=null) {
                //当前用户名重复,直接返回一个表示注册失败的信息
                throw new OrderSystemException("当前用户名存在");
            }

            User user = new User();
            user.setName(request.name);
            user.setPassword(request.password);
            user.setIsAdmin(0);
            userDao.add(user);
            response.ok = 1;
            response.reason = "";

        } catch (OrderSystemException e) {
            response.ok =0;
            response.reason=e.getMessage();
        }finally {
            //5.构造响应数据
        String jsonString = gson.toJson(response);
        resp.setContentType("application/json;charset=utf-8");
        resp.getWriter().write(jsonString);
        }
    }
}

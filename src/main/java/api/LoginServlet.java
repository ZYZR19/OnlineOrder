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
import javax.servlet.http.HttpSession;
import java.io.IOException;
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private Gson gson = new GsonBuilder().create();
    static class  Request {
        public  String name;
        public   String password;

    }
    //要去构造的json响应对象
    static class Response {
        public int ok;
        public String reason;
        public String name;
        public int isAdmin;

    }

    //登录api
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       //1.读取body数据,读取到的数据解析成对象
        //3.按照用户名查找并校验密码
        //4.如果登录失败就返回错误提示
        //5.如果登陆成功就创建session对象 (服务器保存用户信息)
        //6.结果协会给客户端
        req.setCharacterEncoding("utf-8") ;
        resp.setContentType("application/json;charset=urf-8");

        Response response = new Response();
        try {
        String body = Orderutil.readBody(req);
        Request request = gson.fromJson(body,Request.class);
        UserDao userDao = new UserDao();

            User user = userDao.selectByName(request.name);
            if (user==null|| !user.getPassword().equals(request.password)) {
                throw new OrderSystemException("用户名或密码错误");
            }
            HttpSession session = req.getSession(true);
            session.setAttribute("user",user);
            response.ok =1;
            response.reason="";
            response.name=user.getName();
            response.isAdmin = user.getIsAdmin();
        } catch (OrderSystemException e) {
           response.ok =0;
           response.reason=e.getMessage();
        }finally {
        String jsonString = gson.toJson(response);
        resp.getWriter().write(jsonString);
        }

    }




    //检测登录状态api
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1获取用户当前session 如果不存在未登录
        //2从session中获取user对象
        //3.吧user中的信息填充进返回值结果中
        req.setCharacterEncoding("utf-8") ;
        resp.setContentType("application/json;charset=utf-8");
        Response response = new Response();
        try {
            HttpSession session = req.getSession(false);
                      if (session==null) {

                          throw new OrderSystemException("当前未登录");
                      }
                      User user = (User) session.getAttribute("user");
                      if (user==null) {
                          throw new OrderSystemException("当前未登录");
                      }
                      response.ok = 1;
                      response.reason = "";
                      response.name=user.getName();
                      response.isAdmin = user.getIsAdmin();
            } catch (OrderSystemException e) {
             response.ok = 0;
            response.reason = e.getMessage();
            }finally {

            String jsonString =gson.toJson(response);
                    resp.getWriter().write(jsonString);
        }
    }
}

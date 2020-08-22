package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Dish;
import model.DishDao;
import model.User;
import util.OrderSystemException;
import util.Orderutil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/dish")
public class DishServlet  extends HttpServlet {
    private Gson gson = new GsonBuilder().create();
    static class  Request {
        public  String name;
        public   int price;

    }
    //要去构造的json响应对象
    static class Response {
        public int ok;
        public String reason;

    }


    //对应5号api新增菜品
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1.检查用户的登录状态
        //2.考察用户是否是管理员
        //3.读取请求body
        //4.把body转成request对象
        //5.构造dish对象,插入到数据库中
        //6.返回结果给客户端
        Response response = new Response();
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset= utf-8");
       HttpSession session = req.getSession(false);
        try {
            if (session==null) {
                throw new OrderSystemException("当前未登录");
            }

            User user = (User) session.getAttribute("user");
            if (user==null) {
                throw new OrderSystemException("当前未登录");
            }
            if (user.getIsAdmin()==0) {
                //s是否是管理员
                throw new OrderSystemException("您不是管理员");
            }

            String body = Orderutil.readBody(req);
            Request request = gson.fromJson(body,Request.class);
            Dish dish = new Dish();
            dish.setPrice(request.price);
            dish.setName(request.name);
            DishDao dishDao = new DishDao();
            dishDao.add(dish);

            response.ok =1;
            response.reason="";
        } catch (OrderSystemException e) {
         response.ok =0;
         response.reason=e.getMessage();
        } finally {
            String jsonString = gson.toJson(response);
            resp.getWriter().write(jsonString);
        }

    }




    //对应6号api 删除菜品
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
           //读取到id 根据id 进行删除操作 管理员才可以
          //1.先去检查用户是否登录
        //2.检查用户是否是管理员
        //3读取dishid
        //4删除数据库中对应记录
        //5,返回一个响应结果
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        Response response = new Response();

            try {
                HttpSession session = req.getSession(false);
                if (session==null) {
                    throw new OrderSystemException("您尚未登陆");
                }
                User user = (User) session.getAttribute("user");
                if (user==null) {
                    throw new OrderSystemException("您尚未登录");
                }
                if (user.getIsAdmin()==0) {
                    throw new OrderSystemException("您不是管理员");
                }
                String disheIdstr = req.getParameter("dishId");
                if (disheIdstr ==null) {
                    throw new OrderSystemException("dishId 参数不正确");
                }
                int dishId = Integer.parseInt(disheIdstr);
                DishDao dishDao = new DishDao();
                dishDao.delete(dishId);

                response.ok = 1;
                response.reason="";
            } catch (OrderSystemException e) {
                response.reason=e.getMessage();
                response.ok = 0;

            }finally {
                String jsonString = gson.toJson(response);
                resp.getWriter().write(jsonString);
            }

        }





        //7api查看所有菜品 //登录才可以查看


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Response response = new Response();
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");

        try {
            HttpSession session = req.getSession(false);
            if (session==null) {
                throw new OrderSystemException("您尚未登录");
            }
            User user = (User) session.getAttribute("user");
            if (user == null) {
                throw new OrderSystemException("您尚未登录");
            }

            DishDao dishDao = new DishDao();
            List<Dish> dishes = dishDao.selectAll();
            String jsonString = gson.toJson(dishes);
            resp.getWriter().write(jsonString);

        } catch (OrderSystemException e) {
            response.reason = e.getMessage();
            response.ok = 0;
            String jsonString = gson.toJson(response);
            resp.getWriter().write(jsonString);
        }
    }
}


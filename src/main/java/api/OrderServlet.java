package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Dish;
import model.Order;
import model.OrderDao;
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

@WebServlet("/order")
public class OrderServlet extends HttpServlet {
    private Gson gson = new GsonBuilder().create();
    static class Response {
        public int ok;
        public String reason;
    }



    //对应第八个api 新增订单 普通用户新增管理员不行

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Response response = new Response();
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        //1.检查用户登录状态
        HttpSession session = req.getSession(false);
        try {
            if (session==null) {
                throw new OrderSystemException("您尚未登录");
            }
            User user = (User) session.getAttribute("user");
            if (user==null) {
                throw new OrderSystemException("您尚未登录");
            }

            //2.判断用户是否是管理员
            if (user.getIsAdmin()==1) {
                throw new OrderSystemException("您是管理员不能新增");
            }
            //3读取body中的数据,进行解析
            String body = Orderutil.readBody(req);
            //4,按照json格式解析body
            Integer[] dishIds = gson.fromJson(body,Integer[].class);
            //5,构造订单对象 不需要插入详细信息
                Order order = new Order();
                order.setUserId(user.getUserId());
            List<Dish> dishes = new ArrayList<>();
            for (Integer dishId : dishIds) {
                Dish dish = new Dish();
                dish.setDishId(dishId);
                dishes.add(dish);
            }
            order.setDishes(dishes);
            //6.b把order对象插入到数据库
            OrderDao orderDao = new OrderDao();
            orderDao.add(order);
            response.ok = 1;
            response.reason="";
        } catch (OrderSystemException e) {
           response.ok = 0;
           response.reason = e.getMessage();
        } finally {
            resp.setContentType("application/json;charset=utf-8");
            String jsonString = gson.toJson(response);
            resp.getWriter().write(jsonString);
        }
    }


    //第九个.查看所有订单
 //普通用户自己的 管理员查看所有

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        Response response = new Response();
        try {
            HttpSession session = req.getSession(false);
            if (session == null) {
                throw new OrderSystemException("您尚未登录");
            }
            User user = (User) session.getAttribute("user");
            if (user == null) {
                throw new OrderSystemException("您尚未登录");

            }
            OrderDao orderDao = new OrderDao();
            List<Order> orders = null;

            if(user.getIsAdmin()==0) {
                    orders = orderDao.selectByUserId(user.getUserId());
                }else{
                    orders = orderDao.selectAll();//查看所有
                }

                String jsonString= gson.toJson(orders);
                resp.getWriter().write(jsonString);

        } catch (OrderSystemException e) {
            response.ok = 0;
            response.reason = e.getMessage();
            String jsonString = gson.toJson(response);
            resp.getWriter().write(jsonString);
        }
    }


    //修改订单状态

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        Response response = new Response();
        try {
            // 1. 检查用户的登陆状态.
            HttpSession session = req.getSession(false);
            if (session == null) {
                throw new OrderSystemException("您尚未登陆");
            }
            User user = (User) session.getAttribute("user");
            if (user == null) {
                throw new OrderSystemException("您尚未登陆");
            }
            // 2. 判断用户是否是管理员
            if (user.getIsAdmin() == 0) {
                throw new OrderSystemException("您不是管理员");
            }
            // 3. 读取请求中的字段 orderId 和 isDone
            String orderIdStr = req.getParameter("orderId");
            String isDoneStr = req.getParameter("isDone");
            if (orderIdStr == null || isDoneStr == null) {
                throw new OrderSystemException("参数有误");
            }
            // 4. 修改数据库.
            OrderDao orderDao = new OrderDao();
            int orderId = Integer.parseInt(orderIdStr);
            int isDone = Integer.parseInt(isDoneStr);
            orderDao.changeState(orderId, isDone);
            // 5. 返回响应结果.
            response.ok = 1;
            response.reason = "";
        } catch (OrderSystemException e) {
            response.ok = 0;
            response.reason = e.getMessage();
        } finally {
            resp.setContentType("application/json; charset=utf-8");
            String jsonString = gson.toJson(response);
            resp.getWriter().write(jsonString);
        }
    }
}



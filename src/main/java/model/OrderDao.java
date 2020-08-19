package model;

//操作订单
//1.新增订单
// 2查看所有订单 (管理员的功能
// 3查看指定用户订单 (普通用户只能查看自己的订单)
// 4查看订单的详细信息
// 5修改订单状态(管理员)

import com.sun.org.apache.xpath.internal.operations.Or;
import sun.rmi.log.LogInputStream;
import util.OrderSystemException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDao {
    //新增订单
    //订单是和两个表关联的
    //第一个表order _user
    //第二个表order_dish 一个订单会有多个菜品 给这个表一次插入多个记录
    public void  add (Order order) throws OrderSystemException {
         //1.先操作order_user表
        //2.再操作order_dish表
        addOrderUser(order);
        addOrderDish(order);
        //如果第一个方法执行成功 第二个失败数据就会出现问题 第二次失败就进行回滚
    }
//菜品信息插入到表中
    private void addOrderDish(Order order) throws OrderSystemException {
         //遍历order表把list中所有菜品插入进来  插入时候需要知道orderid 和dishid
        //dishid保存在order.getdishes这个list中 orderid在此处没有
        //在执行add方法的时候order的orderid字段是空 这个字段是数据库自增的
        //执行adduser的sql语句时候 数据库有了orderid 但是代码中是不知道orderid的
//1.获取连接
        Connection connection = DBUtil .getConnection();
        //新增orderdish表的数据 要插入多个菜品
//2.拼装sql
        String sql = "insert into order_dish values(?,?)";
        PreparedStatement statement  = null;
        try {
//3.关闭自动提交
            connection.setAutoCommit(false);//关闭自动提交 调用execute是自动的把sql发给服务器//这种情况要先关闭 手动提交
            statement = connection.prepareStatement(sql);
            //一个订单对应多个菜品 遍历order中包含的菜品数 把每个记录取出来
//4,遍历dishes给sql添加多个values值
            List<Dish> dishes = order.getDishes();
            for (Dish dish : dishes) {
                //orderid是进行插入order_user表的时候获取的自增主键
               statement.setInt(1,order.getOrderId());
               statement.setInt(2,dish.getDishId());
               statement.addBatch();//给sql新增一个片段,可以累加 每循环都多加一个values(?,?)
                //把多组数据合并成一个sql语句

            }
//5.执行sql(不是真的)
            statement.executeBatch();//把刚才的sql进行执行(不是真的执行)
//6,提交给服务器      调用之前可以执行多个sql  把多个sql一次发送给服务器
            connection.commit();//执行sql (发送给服务器)
        } catch (SQLException e) {
            e.printStackTrace();
            //如果上面的操作出现异常 整体的新增订单就失败 回滚到order_user表的内容
            deleteOrderUser(order.getOrderId());
        }finally {
            DBUtil.close(connection,statement,null);
        }

    }

    private void deleteOrderUser(int orderId) throws OrderSystemException {
        //用于删除order_user表的记录
        Connection connection = DBUtil.getConnection();
        String sql = "delete from order_user where orderId = ?";
            PreparedStatement statement = null;
        try {
            statement=connection.prepareStatement(sql);
            statement.setInt(1,orderId);
            int ret =statement.executeUpdate();
            if (ret!=1) {
                throw new OrderSystemException("回滚失败");
            }
            System.out.println("回滚成功");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new OrderSystemException("回滚失败");

        }finally {
            DBUtil.close(connection,statement,null);
        }
    }

    //用户信息插入表中
    private void addOrderUser(Order order) throws OrderSystemException {
        //1.获取数据库连接
        Connection connection = DBUtil.getConnection();
        //拼装sql
        String sql = "insert into order_user values(null,?,now(),0)";//userid
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
            //加入RETURN_GENERATED_KEYS这个会插入的同时返回自增主键的值
            statement.setInt(1,order.getUserId());
            //执行sql
            int ret = statement.executeUpdate();
            if (ret!=1) {
                throw  new OrderSystemException("插入订单失败");

            }
            //读取自增主键的值
            resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                //理解参数 读取result的结果时,可以使用列名 也可以使用下标
                //由于一个表中的自增列可以有多个返回的时候都返回回来
                //下标1 表示想获取到第一个自增列生成的值
                order.setOrderId(resultSet.getInt(1));
            }
            System.out.println("插入订单第一步成功");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new OrderSystemException("插入订单失败");
        }finally {
            DBUtil.close(connection,statement,resultSet);
        }

    }



    //查看所有订单 订单信息包含 orderId userId这些属性 这些属性借助order_user表可以获取到
    //dishes 是一个list 要获取详细信息
    //先根据order_dish表获取到所有相关的dishId 然后再dishes表中查找
    //只获取到订单信息就可以 菜品信息有其他接口实现
    //返回的order对象中不包含dishes详细数据
    //让代码简单高效
    public  List<Order> selectAll() {
           List<Order> orders = new ArrayList<>();
           Connection connection = DBUtil.getConnection();
            ResultSet resultSet = null;
            PreparedStatement statement = null;
           String sql = "select * from order_user";
        try {
             statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Order order = new Order();
                //没有dishes字段 dishes字段为空
                order.setOrderId(resultSet.getInt("orderId"));
                order.setUserId(resultSet.getInt("userId"));
                order.setTime(resultSet.getTimestamp("time"));
                order.setIsDone(resultSet.getInt("isDone"));
                orders.add(order);
                }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(connection, statement,resultSet);
        }
        return orders;
    }



    //查看指定用户的订单
      public  List<Order> selectByUserId (int userId) {
          List<Order> orders = new ArrayList<>();
        Connection connection = DBUtil.getConnection();
        String  sql = "select * from order_user where userId = ?" ;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
          try {
              statement = connection.prepareStatement(sql);
              statement.setInt(1,userId);
              resultSet = statement.executeQuery();
              while(resultSet.next()) {
                  Order order = new Order();
                  order.setOrderId(resultSet.getInt("orderId"));
                  order.setUserId(resultSet.getInt("userId"));
                  order.setTime(resultSet.getTimestamp("time"));
                  order.setIsDone(resultSet.getInt("isDone"));
                  orders.add(order);
              }
          } catch (SQLException e) {
              e.printStackTrace();
          }finally {
              DBUtil.close(connection,statement,resultSet);
          }
          return  orders;
      }





      //查看指定订单的详细信息 获取dishes中的信息
    public Order selectById (int orderId) throws OrderSystemException {
        //1根据orderid得到order对象
        //根据orderid得到orderid对应的菜品id列表
        //根据菜品id列表 查询dishes表获取到菜品详情
        Order order = buildOrder(orderId);
        List<Integer> dishIds = selectDishIds(orderId);
       order = getDishDetail(order,dishIds);
       return order;
    }

    //查找order_dish表 查找订单中对应的菜品id
    private List<Integer> selectDishIds(int orderId) {
        List<Integer> dishIds = new ArrayList<>();
        Connection connection = DBUtil.getConnection();
        String sql = "select * from order_dish where orderId = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement=connection.prepareStatement(sql);
            statement.setInt(1,orderId);
            resultSet = statement.executeQuery();
            while(resultSet.next() ) {
                dishIds.add(resultSet.getInt("dishId" ));
            }
            return dishIds;
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(connection,statement,resultSet);
        }
        return  dishIds;
    }


    //根据orderId来查询对应order对象的基本信息
    private Order buildOrder(int orderId) {
        Connection connection = DBUtil.getConnection();
        String sql = "select * from order_user where orderId = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1,orderId);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Order order = new Order();
                order.setOrderId(resultSet.getInt("orderId"));
                order.setUserId(resultSet.getInt("userId"));
                order.setTime(resultSet.getTimestamp("time"));
                order.setIsDone(resultSet.getInt("isDone"));
                return order;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(connection,statement,resultSet);

        }
        return null;

    }

    private Order getDishDetail(Order order, List<Integer> dishIds) throws OrderSystemException {
        //1.准备返回的结果
        List<Dish> dishes = new ArrayList<>();
        //2,遍历dishIds 在dishes 表中查找
        DishDao dishDao = new DishDao();
        for (Integer dishId : dishIds) {
            Dish dish = dishDao.selectById(dishId);
            dishes.add(dish);
        }
        //3.把dishes设置到order对象中
        order.setDishes(dishes);
        return order;

    }



    public void changeState ( int orderId,int isDone) throws OrderSystemException {
        Connection connection = DBUtil.getConnection();
        String sql= "update order_user set isDone = ? where orderId = ?";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1,isDone);
            statement.setInt(2,orderId);
            int ret = statement.executeUpdate();
            if (ret!=1) {
                throw new OrderSystemException("修改订单状态失败");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new OrderSystemException("修改订单状态失败");

        }finally {
            DBUtil.close(connection,statement,null);
        }

    }
}

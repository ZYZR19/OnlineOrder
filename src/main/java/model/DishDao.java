package model;

import com.sun.org.apache.bcel.internal.generic.NEW;
import util.OrderSystemException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//操作菜品表
//1.新增菜品 2.删除菜品 3.查询所有菜品 4,查询指定菜品
public class DishDao {
    public void add(Dish dish) throws OrderSystemException {
        //1.获取数据库连接
        Connection connection = DBUtil.getConnection();
        //2.拼装sql语句
        String sql = "insert into dishes values (null,?,?)";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1,dish.getName());
            statement.setInt(2,dish.getPrice());
            //3.执行sql
            int ret = statement.executeUpdate();
            if (ret!=1) {
                throw new OrderSystemException("插入新菜品失败");
            }
            System.out.println("插入新菜品成功");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new OrderSystemException("插入菜品失败");
        }finally {
            DBUtil.close(connection,statement,null);
        }
    }



    public void delete (int dishId) throws OrderSystemException {
       //1.获取数据库连接
       Connection connection = DBUtil.getConnection();
       //2.拼装sql
        String sql = "delete from dishes where dishId = ?";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1,dishId);
            //3.执行sql
            int ret = statement.executeUpdate();
            if (ret!=1) {
                throw new OrderSystemException("删除菜品失败");
            }
            System.out.println("删除菜品成功");
        } catch (SQLException e) {
            e.printStackTrace();
            throw  new OrderSystemException("删除菜品失败");
        }finally {
            DBUtil.close(connection,statement,null);
        }
    }




    public List<Dish> selectAll () throws OrderSystemException {
        List<Dish> dishes = new ArrayList<>();
        //1建立连接
        Connection connection = DBUtil.getConnection();
        //2拼装sql
        String  sql = "select * from dishes";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql);
            //3执行sql
            resultSet = statement.executeQuery();
            while(resultSet.next()) {
                Dish dish = new Dish();
                dish.setDishId(resultSet.getInt("dishId"));
                dish.setName(resultSet.getString("name"));
                dish.setPrice(resultSet.getInt("price"));
                dishes.add(dish);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new OrderSystemException("查询所有菜品失败");
        }finally {
            DBUtil.close(connection,statement,resultSet);
        }
        return dishes;
    }



    public Dish selectById (int dishId) throws OrderSystemException {
        //1.建立数据库连接
        Connection connection = DBUtil.getConnection();
        //2.拼装sql
        String sql = "select * from dishes where dishId = ?";
        PreparedStatement statement = null;
        ResultSet resultSet =null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1,dishId);
            //3.执行sql语句
            resultSet = statement.executeQuery();
            //4,遍历结果集
            if (resultSet.next()) {
                Dish dish = new Dish();
                dish.setDishId(resultSet.getInt("dishId"));
                dish.setName(resultSet.getString("name"));
                dish.setPrice(resultSet.getInt("price"));
                return dish;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new OrderSystemException("按照id查找菜品出错");

        }finally {
            DBUtil.close(connection,statement,resultSet);
        }
        return  null;
    }


    public static void main(String[] args) throws OrderSystemException {
        DishDao dishDao = new DishDao();
        //1.测试新增
      /*  Dish dish = new Dish();
        dish.setName("羊肉串");
        dish.setPrice(4000);
        dishDao.add(dish);*/

        /*Dish dish1 = new Dish();
        dish1.setName("火锅");
        dish1.setPrice(5000);
        dishDao.add(dish1);
        Dish dish2 = new Dish();
        dish2.setName("油焖大虾");
        dish2.setPrice(4500);
        dishDao.add(dish2);*/

        //2,测试查找所有
        List<Dish> dishes= dishDao.selectAll();
        System.out.println("查看所有");
        System.out.println(dishes);

        Dish dish = dishDao.selectById(1);
        System.out.println("查看指定");
        System.out.println(dish);

        dishDao.delete(1);
        System.out.println("删除成功"
        );



    }

}

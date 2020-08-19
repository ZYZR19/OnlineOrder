package model;


import util.OrderSystemException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//实现三个功能
// 1.插入新用户--注册时候使用
//2.按名字查找用户--登录时候使用
//3.按用户id查找--展示信息使用
// jdbc编程的基本流程
//        //1.先获取和数据库的连接DataSource
//        //创建mysqldatasource这样一个实例 然后seturl setpassword等方法设定相关属性
//        //再调用getconnection方法 失败就抛出异常 会提示一些错误
//        //2.拼装sql语句PrepareStatement
//        //创建preparestatement这样的实例 在构造实例传入一个字符串相当于sql语句的模板
//        //变化的部分用问号代替
//        //3.执行sql语句executeQuery,executeUpdate
//        //调用exequery(查询)或者update(插入)方法 返回的类型不一样
//        //4.关闭连接close(如果是查询还又遍历结果集)
//        //调用close关闭连接
public class UserDao {
    //插入新用户
    public void  add(User user) throws OrderSystemException {

        Connection connection = DBUtil.getConnection();
        String  sql = "insert into user values (null,?,?,?)";
        PreparedStatement statement = null;
        try {
            statement=connection.prepareStatement(sql);
            statement.setString(2,user.getPassword());
            statement.setInt(3,user.getIsAdmin());
            statement.setString(1,user.getName());
            int ret = statement.executeUpdate();
            if (ret!=1) {
                throw new OrderSystemException("插入用户失败");
            }
            System.out.println("插入用户成功");
        } catch (SQLException e) {
            e.printStackTrace();
            throw  new OrderSystemException("插入用户失败");
        }finally {
            DBUtil.close(connection,statement,null);
        }
        }




        //按照名字查找用户
        public  User selectByName (String name) throws OrderSystemException {
         Connection connection = DBUtil.getConnection();
            ResultSet resultSet = null;
         String  sql = "select * from user where name = ?";
         PreparedStatement statement = null;
            try {
                statement=connection.prepareStatement(sql);
                statement.setString(1,name);
                resultSet = statement.executeQuery();
                if (resultSet.next()) {//用户名不能重复 只能找到一个
                   User user = new User();
                   user.setUserId(resultSet.getInt("userId"));
                   user.setName(resultSet.getString("name"));
                   user.setPassword(resultSet.getString("password"));
                   user.setIsAdmin(resultSet.getInt("isAdmin"));
                   return  user;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new OrderSystemException("按名字查找失败");
            }finally {
                DBUtil.close(connection,statement,resultSet);
            }
            return  null;
        }




        //根据id查找用户
     public  User selectById ( int userId) throws OrderSystemException {
        //1.获取连接
         Connection connection = DBUtil.getConnection();
         ResultSet resultSet = null;
         //2,拼装sql
         String  sql = "select * from user where userId = ?";
         PreparedStatement statement = null;
         try {
             statement=connection.prepareStatement(sql);
             statement.setInt(1,userId);
             //3.执行sql
             resultSet = statement.executeQuery();
             if (resultSet.next()) {
                 User user = new User();
                 user.setUserId(resultSet.getInt("userId"));
                 user.setName(resultSet.getString("name"));
                 user.setPassword(resultSet.getString("password"));
                 user.setIsAdmin(resultSet.getInt("isAdmin"));
                 return  user;
             }

         } catch (SQLException e) {
             e.printStackTrace();
             throw new OrderSystemException("按照id查找失败");
         }finally {
             DBUtil.close(connection,statement,resultSet);

         }
         return  null;
     }


    public static void main(String[] args) throws OrderSystemException {
        UserDao userDao = new UserDao();
        /*User user = new User();

        user.setPassword("123456");
        user.setIsAdmin(1);
        user.setName("张雨蓉");
        userDao.add(user);*/

        /*User user= userDao.selectByName("张雨蓉");
        System.out.println("按照名字查找");
        System.out.println(user);*/

        User user = userDao.selectById(1);
        System.out.println("按照id查找");
        System.out.println(user);


    }





}

package model;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//本质上是一个管理单例的DataSource的类
public class DBUtil {
    private  static  final String URL = "jdbc:mysql://127.0.0.1:3306/onlineorder?characterEncoding=utf-8&useSSL=true";
    private  static  final String USERNAME ="root";
    private  static  final String password=" ";

    private  static  volatile DataSource dataSource = null;
    public static  DataSource getDataSource () {
        if (dataSource==null) {
            synchronized (DBUtil.class) {
                if (dataSource ==null) {
                    dataSource = new MysqlDataSource();
                    ((MysqlDataSource)dataSource).setURL(URL);
                    ((MysqlDataSource)dataSource).setUser(USERNAME);
                    ((MysqlDataSource)dataSource).setPassword(password);

                }
            }

        }

        return dataSource;
    }
//数据库连接失败后序操作就失败
    //如果connection为null数据库连接失败 要查看错误信息 在tomcat日志中

    public static Connection getConnection () {
        try {
            return  getDataSource().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("数据库连接失败,请检查数据库是否启动正确 路径是否正确");
        return  null;
    }

    public static  void close(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {

            try {
                if (resultSet!= null) {
                    resultSet.close();
                }
                if (preparedStatement!=null) {
                    preparedStatement.close();
                }
                if (connection!=null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


}

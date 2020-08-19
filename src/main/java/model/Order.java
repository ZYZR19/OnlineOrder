package model;

import java.sql.Timestamp;
import java.util.List;

public class Order {
    //表示一个完整的订单
    //订单中都有哪些菜
    //order类对应order_user和order_dish表
    private  int orderId;
    private  int userId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public int getIsDone() {
        return isDone;
    }

    public void setIsDone(int isDone) {
        this.isDone = isDone;
    }

    public int getOrderId() {

        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    private Timestamp time;//时间戳
    private  int isDone;

    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

    private List<Dish> dishes;//一个订单中包含的所有菜品信息

}

package team6.skku_fooding.models;

import android.util.Pair;

import java.util.List;

public class User {
    public int userId;
    public String nickname;
    public String UID;
    public List<Order> orderList;
    public List<Survey> surveyList;
    public List<Pair<Product, Integer>> shoppingCart;
    private String password;

    public User() {}
    public User(int userId) {
        // TODO: Retrieve Data from Database and make object.
    }
}

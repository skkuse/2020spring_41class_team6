package team6.skku_fooding.models;

import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    public String user_id;
    public String pw;
    public String nickname;
    public String UID;
    public List<Pair<Product, Integer>> shopping_cart; // discuss with Algi
    public List<Order> order_list;
    public int category_id;
    public List<String> filter_list;

    public User() {}

    public User(String user_id, String pw, String nickname, String UID) {
        this.user_id=user_id;
        this.pw=pw;
        this.nickname=nickname;
        this.UID=UID;
        this.shopping_cart=new ArrayList<Pair<Product, Integer>>();
        this.order_list=new ArrayList<Order>();
        this.filter_list = new ArrayList<String>();
        // shopping cart, order_list, category_id, filter_list는 각 activity에서 child 추가
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("user_id", user_id);
        result.put("pw", pw);
        result.put("nickname", nickname);
        result.put("UID", UID);
        result.put("shopping_cart", shopping_cart);
        result.put("order_list", order_list);
        result.put("category_id", category_id);
        result.put("filter_list", filter_list);
        return result;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getPw() {
        return pw;
    }

    public String getNickname() {
        return nickname;
    }

    public String getUID() {
        return UID;
    }

    public List<Pair<Product, Integer>> getShopping_cart() {
        return shopping_cart;
    }

    public List<Order> getOrder_list() {
        return order_list;
    }

    public int getCategory_id() {
        return category_id;
    }

    public List<String> getFilter_list() {
        return filter_list;
    }
}

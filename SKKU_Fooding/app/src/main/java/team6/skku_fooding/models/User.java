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
    public String shopping_cart;
    public int category_id;
    public String filter;

    public User() {}

    public User(String user_id, String pw, String nickname, String UID) {
        this.user_id=user_id;
        this.pw=pw;
        this.nickname=nickname;
        this.UID=UID;
        this.shopping_cart= "@"; // initial value : discuss with Algi
        // category_id, filter_list는 SurveyActivity에서 child 추가
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("user_id", user_id);
        result.put("pw", pw);
        result.put("nickname", nickname);
        result.put("UID", UID);
        result.put("shopping_cart", shopping_cart);
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

    public String getShopping_cart() {
        return shopping_cart;
    }

    public int getCategory_id() {
        return category_id;
    }

    public String getFilter() {
        return filter;
    }
}

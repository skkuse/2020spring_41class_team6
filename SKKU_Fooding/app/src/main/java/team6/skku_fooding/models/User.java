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
    public List<Pair<Product, Integer>> shopping_cart;
    public List<Order> order_list;
    public List<Survey> survey_list;

    public User() {}

    public User(String user_id, String pw, String nickname, String UID) {
        this.user_id=user_id;
        this.pw=pw;
        this.nickname=nickname;
        this.UID=UID;
        this.shopping_cart=new ArrayList<Pair<Product, Integer>>();
        this.order_list=new ArrayList<Order>();
        this.survey_list=new ArrayList<Survey>();
        /*
            Firebase에 value가 null이면 안들어가므로 초기값으로 뭘 넣어둘지 상의해야함!!
         */
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("user_id", user_id);
        result.put("pw", pw);
        result.put("nickname", nickname);
        result.put("UID", UID);
        result.put("shopping_cart", shopping_cart);
        result.put("order_list", order_list);
        result.put("survey_list", survey_list);
        return result;
    }
}

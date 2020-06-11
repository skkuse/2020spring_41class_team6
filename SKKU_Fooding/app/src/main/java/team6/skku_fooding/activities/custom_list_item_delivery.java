package team6.skku_fooding.activities;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

public class custom_list_item_delivery {
    private String order_id;
    private String date;
    private String status;
    private String product_name;
    private String product_id;

    public custom_list_item_delivery(String order_id, String date, String status,String product_name,String product_id) {
        this.order_id = order_id;
        this.date = date;
        this.status = status;
        this.product_name = product_name;
        this.product_id=product_id;

    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}

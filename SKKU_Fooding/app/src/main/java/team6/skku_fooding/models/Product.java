package team6.skku_fooding.models;

import android.graphics.Bitmap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Product {
    public int productId;
    public String companyName;
    public Date uploadedDate;
    public List<Ingredient> ingredients;
    // Image container: String URI
    public String image;
    public double overallScore;
    public String productName;
    public int price;

    public Product() {
        ingredients=new ArrayList<Ingredient>();
    }
    public Product(int productId) {
        // TODO: Retrieve Data from Database and make object.
    }
    public Date stringToDate(String str){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(str);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}



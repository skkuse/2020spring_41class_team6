package team6.skku_fooding.models;

import android.graphics.Bitmap;

import java.util.Date;
import java.util.List;

public class Product {
    public int productId;
    public String companyName;
    public String uploadedDate;
    public List<Ingredient> ingredients;
    // Image container: String URI
    public String image;
    public double overallScore;
    public String productName;
    public int price;

    public Product() {}
    public Product(int productId) {
        // TODO: Retrieve Data from Database and make object.
    }
}

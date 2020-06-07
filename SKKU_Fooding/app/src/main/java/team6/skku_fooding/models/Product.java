package team6.skku_fooding.models;

import android.graphics.Bitmap;

import java.util.Date;
import java.util.List;

public class Product {
    public int productId;
    public String company;
    public String image;
    public String ingredients;
    public String name;
    public int price;
    public String uploadedDate;
    // Image container: Base64 String
    public double overallScore;

    public Product() {}
    public Product(int pid, String comp, String img, String ing, String pn, int p, String upd) {
        this.productId = pid;
        this.company = comp;
        this.image = img;
        this.ingredients = ing;
        this.name = pn;
        this.price = p;
        this.uploadedDate = upd;
    }
}

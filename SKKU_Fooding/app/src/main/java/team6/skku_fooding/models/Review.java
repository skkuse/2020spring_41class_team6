package team6.skku_fooding.models;

import java.util.Date;

public class Review {
    public int reviewId;
    public int userId;
    public int productId;
    public int categoryId;
    public Date writtenDate;
    public Date modifiedDate;
    public String description;
    public String title;
    public int score;

    public Review() {}
    public Review(int reviewId) {
        // TODO: Retrieve Data from Database and make object.
    }
}

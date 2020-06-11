package team6.skku_fooding.activities;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Date;
public class Review {

        public int reviewId;
        public String userId;
        public int productId;
        public int categoryId;
        public Date writtenDate;
        public String modifiedDate;
        public String description;
        public String title;
        public String score;
        public ArrayList<Bitmap>images;

        public Review() {}
        public Review(int reviewId) {
            // TODO: Retrieve Data from Database and make object.
        }
    }


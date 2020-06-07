package team6.skku_fooding.models;

import java.util.ArrayList;
import java.util.List;

public class Review {
    public int reviewId;
    public String userId;
    public int productId;
    public int categoryId;
    public String writtenDate;
    public String modifiedDate;
    public String description;
    public String title;
    public int rate;
    public List<String> b64Imgs;

    public Review() {}
    public Review(int rid, String uid, int pid, String wDate, String mDate, String desc, String tit, int score, List<String> imgs) {
        this.reviewId = rid;
        this.userId = uid;
        this.productId = pid;
        this.writtenDate = wDate;
        this.modifiedDate = mDate;
        this.description = desc;
        this.title = tit;
        this.rate = score;
        this.b64Imgs = imgs;
    }
}

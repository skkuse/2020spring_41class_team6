package team6.skku_fooding.models;

import java.util.HashMap;
import java.util.Map;

public class Survey {
    public int categoryId;
    public String filteringList;

    public Survey(){

    }
    public Survey(int categoryId,String filteringList){
        this.categoryId=categoryId;
        this.filteringList=filteringList;
    }

}

package team6.skku_fooding.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Survey {
    public int categoryId;
    public List<String> filteringList;

    public Survey(int categoryId,List<String>filteringList){
        this.categoryId=categoryId;
        this.filteringList=filteringList;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("category_id", categoryId);
        result.put("filter",filteringList);
        return result;
    }
}

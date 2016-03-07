package innatemobile.storymakerevents.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by rphovley on 3/6/2016.
 */
public class LinkedHashMapIndex<K, V> extends LinkedHashMap {

    HashMap<String,Integer> index;
    int curr = 0;

    public LinkedHashMapIndex() {
        index = new HashMap<>();
    }

    /*   @Override
    public void put(K key,V val){
        super.put(key, val);
        index.put(curr++, key);
    }*/

    @Override
    public Object put(Object key, Object value) {
        Object ob = super.put(key, value);
        index.put(key.toString(), curr++);
        return ob;
    }

    /*public Object getindexed(int i){
        return super.get(index.get(i));
    }*/
    public int getKeyIndex(String key){
        return index.get(key);
    }

}

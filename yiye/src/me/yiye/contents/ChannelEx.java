package me.yiye.contents;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.yiye.utils.MLog;

public class ChannelEx{
    private final static String TAG = "ChannelEx";
    public String id;
    public String logo;
    public String name;
    public String description;
    public String time;
    public String type;
    public String banner;
    public String creator;
    public int bmkNum;
    public int subNum;
    public List<String> tags;

    @Override
    public String toString() {
        return "[id:" + id + " logo:" + logo + " name:" + name + " description:" + description + " time:" + time + " type:" + type +
                " banner:" + banner + " creator:" + creator + " bmkNum:" + bmkNum + " subNum" + subNum + " tags:" + tags + "]";
    }

    public static ChannelEx buildFromJosnObject(JSONObject o) throws JSONException {
        ChannelEx c = new ChannelEx();
        c.id = o.getString("_id");
        c.logo = o.getString("logo");
        c.name = o.getString("name");
        c.description = o.getString("description");
        c.time = o.getString("time");
        c.type = o.getString("type");
        c.banner = o.getString("banner");
        c.creator = o.getString("creator");
        c.bmkNum = o.getInt("bmkNum");
        c.subNum = o.getInt("subNum");

        c.tags = new ArrayList<String>();
        JSONArray tagsJsonArray = o.getJSONArray("tags");
        for(int i = 0;i < tagsJsonArray.length();++ i) {
            String tag = tagsJsonArray.getString(i);
            c.tags.add(tag);
        }
        MLog.d(TAG, "buildFromJosnObject### " + c.toString());
        return c;
    }
}

package ebiz.homework3;
 
import java.util.ArrayList;
import java.util.List;

import ebiz.homework3.bean.RowItem;
import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
 
public class ImageTextListBaseAdapterActivity extends Activity implements
        OnItemClickListener {
 
    public static final String[] titles = new String[] { "Straw Berry",
            "Banana Lee", "Orange Huang", "Apple Dai" };
 
    public static final String[] phones = new String[] {
        "4124781515",
        "5289654859", "4785236985",
        "1589632587" };
    public static final String[] descriptions = new String[] {
            "Going for North Oakland",
            "Drop me off at GreenPepper", "5 Metal Street at S.Oakland",
            "Shadyside Park" };
 
    public static final Integer[] images = { R.drawable.driver,
            R.drawable.passenger, R.drawable.passenger, R.drawable.passenger };
 
    ListView listView;
    List<RowItem> rowItems;
 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
 
        rowItems = new ArrayList<RowItem>();
        for (int i = 0; i < titles.length; i++) {
            RowItem item = new RowItem(images[i], titles[i], phones[i], descriptions[i]);
            rowItems.add(item);
        }
 
        listView = (ListView) findViewById(R.id.list);
        CustomBaseAdapter adapter = new CustomBaseAdapter(this, rowItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }
 
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        Toast toast = Toast.makeText(getApplicationContext(),
                "Item " + (position + 1) + ": " + rowItems.get(position),
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }
}
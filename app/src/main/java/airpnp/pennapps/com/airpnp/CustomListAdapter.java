package airpnp.pennapps.com.airpnp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class CustomListAdapter extends ArrayAdapter {
    public Context mContext;
    public String[] mListText;
    public int[] mListDrawable;

    public CustomListAdapter(Context context, String[] listText, int[] listDrawable){
        super(context,R.layout.drawer_list_item,listText);

        this.mContext = context;
        this.mListText = listText;
        this.mListDrawable = listDrawable;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.drawer_list_item,parent,false);

        TextView textView = (TextView)rowView.findViewById(R.id.textView);
        ImageView imageView = (ImageView)rowView.findViewById(R.id.list_image_view);

        textView.setText(mListText[position]);
        imageView.setImageResource(mListDrawable[position]);

        return rowView;
    }



}

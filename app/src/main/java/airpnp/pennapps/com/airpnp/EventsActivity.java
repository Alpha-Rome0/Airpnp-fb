package airpnp.pennapps.com.airpnp;

import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v13.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EventsActivity extends AppCompatActivity {
    public LatLng getLatLng() {
        return latLng;
    }

    private LatLng latLng;
    private LocationManager m_LocationManager;
    private Location m_Location;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 6;
    private Fragment fragment;

    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mListText;
    private int[] mListDrawable = {R.drawable.ic_menu_compass,R.drawable.ic_menu_directions,R.drawable.ic_menu_share,
            R.drawable.ic_menu_add,R.drawable.ic_menu_start_conversation,R.drawable.ic_menu_help,R.drawable.ic_menu_preferences};
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mTitle;
    private TextView t_name;
    private DatabaseReference mDatabase;
    String a;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        FragmentManager fm = getSupportFragmentManager();
        fragment = new EventsFragment();
        fm.beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .commit();

        String value="";
        if (savedInstanceState != null) {
            value = savedInstanceState.getString("user_email");
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        ValueEventListener postListener = new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.child("First_Name").getValue(String.class);
                String value1 = dataSnapshot.child("Last_Name").getValue(String.class);
                t_name=(TextView)findViewById(R.id.u_name);
                t_name.setText(value+" "+value1);
                Log.w("!!!!!!!!!!!!!!!!!!", value+value1);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        };

        mDatabase.child("user").child("luoyin").addValueEventListener(postListener);


        //Slinding menu
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTitle = getTitle();

        mListText = getResources().getStringArray(R.array.list_item_array);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.option_list);

        CustomListAdapter customListAdapter = new CustomListAdapter(this,mListText,mListDrawable);
        mDrawerList.setAdapter(customListAdapter);

        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.action_settings,R.string.app_name){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                Toast.makeText(getBaseContext(),"drawer opened",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
                Toast.makeText(getBaseContext(),"drawer closed",Toast.LENGTH_LONG).show();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);


    }
    @Override
    public void onAttachFragment(Fragment fragment){
        getLocationPermission();
    }

    public void getLocationPermission() {
        Log.d("!!!", "getting permissions");

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EventsActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        getLocation();

    }

    public void getLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            m_LocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String bestProvider = m_LocationManager.getBestProvider(criteria, true);
            m_Location = m_LocationManager.getLastKnownLocation(bestProvider);

            latLng = new LatLng(m_Location.getLatitude(), m_Location.getLongitude());
            ((EventsFragment)fragment).getEventList();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Map", "Permission granted");
                    getLocation();
                } else {
                    Log.d("Map", "Permission denied");
                }
                return;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void selectItem(int position){

    }

    public class DrawItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);

    }

}


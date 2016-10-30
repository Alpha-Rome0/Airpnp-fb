package airpnp.pennapps.com.airpnp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class ParkingDetailsActivity extends AppCompatActivity {

    private JSONObject tempJSONObject;
    private JSONArray tempJSONArray;

    Button arrivalStartDateBtn;
    Button arrivalStartTimeBtn;
    Button arrivalEndDateBtn;
    Button arrivalEndTimeBtn;

    private String phone;
    private Button book;

    private double hourlyRate,latitude,longitude;

    public Calendar startDate;
    public Calendar endDate;

    private long hours;

    private DatabaseReference firebase;

    private String userCustomerId;
    private String ownerCustomerId;
    private String userAccountId;
    private String ownerAccountId;
    private String userEmail;
    private String ownerEmail;
    private static String TAG = "DETAILS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_details);
        userEmail=getIntent().getStringExtra("user_email");
        ownerEmail=getIntent().getStringExtra("owner_email");

        firebase = FirebaseDatabase.getInstance().getReference();

        // Setting initial calendar values
        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();

        userEmail = getIntent().getStringExtra("user_email");

        SimpleDateFormat sdfDateFormatter = new SimpleDateFormat("MMM dd, yyyy");
        SimpleDateFormat sdfTimeFormatter = new SimpleDateFormat("h:mm a");

        book = (Button) findViewById(R.id.btn_book);
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookParking(v);
            }
        });
        arrivalStartDateBtn = (Button) findViewById(R.id.btn_start_date);
        arrivalStartDateBtn.setText(sdfDateFormatter.format(startDate.getTime()));
        arrivalStartDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(true);
            }
        });
        arrivalStartTimeBtn = (Button) findViewById(R.id.btn_start_time);
        arrivalStartTimeBtn.setText(sdfTimeFormatter.format(startDate.getTime()));
        arrivalStartTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime(true);
            }
        });
        arrivalEndDateBtn = (Button) findViewById(R.id.btn_end_date);
        arrivalEndDateBtn.setText(sdfDateFormatter.format(endDate.getTime()));
        arrivalEndDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(false);
            }
        });
        arrivalEndTimeBtn = (Button) findViewById(R.id.btn_end_time);
        arrivalEndTimeBtn.setText(sdfTimeFormatter.format(endDate.getTime()));
        arrivalEndTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime(false);
            }
        });

        ValueEventListener mapListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, dataSnapshot.toString());
                String ownerFirstName = dataSnapshot.child("firstname").getValue(String.class);
                String ownerLastName = dataSnapshot.child("lastname").getValue(String.class);
                String street = dataSnapshot.child("street").getValue(String.class);
                String city = dataSnapshot.child("city").getValue(String.class);
                String state = dataSnapshot.child("state").getValue(String.class);
                latitude = dataSnapshot.child("latitude").getValue(Double.class);
                longitude = dataSnapshot.child("longitude").getValue(Double.class);
                hourlyRate = dataSnapshot.child("rate").getValue(Double.class);
                phone = dataSnapshot.child("phone").getValue(String.class);
                TextView textView1 = (TextView) findViewById(R.id.tv_owner_name);
                TextView textView2 = (TextView) findViewById(R.id.tv_phone);
                TextView textView3 = (TextView) findViewById(R.id.tv_rate);
                TextView textView4 = (TextView) findViewById(R.id.tv_rules);
                TextView textView5 = (TextView) findViewById(R.id.tv_house_name);
                TextView textView6 = (TextView) findViewById(R.id.tv_house_addr);
                textView1.setText("Name: " + ownerFirstName + " " + ownerLastName);
                textView2.setText("Contact: " + phone);
                textView3.setText("Rate: $" + hourlyRate + " / hr");
                textView4.setText("Remarks: No Minivans please");
                textView5.setText(street);
                textView6.setText(city);
                }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        firebase.child("owners/"+ownerEmail).addValueEventListener(mapListener);


    }


    public void setDate(final boolean isArrival) {
        Calendar currCalendar;
        if (isArrival) {
            currCalendar = startDate;
        } else {
            currCalendar = endDate;
        }
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
                        // Set the new calendar dates
                        if (isArrival) {
                            startDate.set(Calendar.YEAR, year);
                            startDate.set(Calendar.MONTH, monthOfYear);
                            startDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                            arrivalStartDateBtn.setText(sdf.format(startDate.getTime()));
                        } else {
                            endDate.set(Calendar.YEAR, year);
                            endDate.set(Calendar.MONTH, monthOfYear);
                            endDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                            arrivalEndDateBtn.setText(sdf.format(endDate.getTime()));
                        }

                    }
                },
                currCalendar.get(Calendar.YEAR),
                currCalendar.get(Calendar.MONTH),
                currCalendar.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    public void setTime(final boolean isArrival) {
        Calendar currCalendar;
        if (isArrival) {
            currCalendar = startDate;
        } else {
            currCalendar = endDate;
        }
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
                        // Set the new calendar times
                        if (isArrival) {
                            startDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            startDate.set(Calendar.MINUTE, minute);
                            startDate.set(Calendar.SECOND, second);

                            arrivalStartTimeBtn.setText(sdf.format(startDate.getTime()));
                        } else {
                            endDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            endDate.set(Calendar.MINUTE, minute);
                            endDate.set(Calendar.SECOND, second);

                            arrivalEndTimeBtn.setText(sdf.format(endDate.getTime()));
                        }
                    }
                },
                currCalendar.get(Calendar.HOUR_OF_DAY),
                currCalendar.get(Calendar.MINUTE),
                false
        );
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    public void bookParking(View view) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("rest.nexmo.com")
                .appendPath("sms")
                .appendPath("json")
                .appendQueryParameter("api_key", getString(R.string.nexmo_id))
                .appendQueryParameter("api_secret", getString(R.string.nexmo_secret))
                .appendQueryParameter("from", "12675097486")
                .appendQueryParameter("to", phone)
                .appendQueryParameter("text", "Hi! This is AirPnP notifying you that " + userEmail + " has booked your parking spot!");
        String url = builder.build().toString();
        Log.d("!!!", url);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=" + latitude + "," + longitude));
        startActivity(intent);

        String ownerEmail = getIntent().getStringExtra("owner_email");

        String startTime = arrivalStartTimeBtn.getText().toString();
        String endTime = arrivalEndTimeBtn.getText().toString();
        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");

        int startYear = startDate.get(Calendar.YEAR);
        int startMonth = startDate.get(Calendar.MONTH);
        int startDay = startDate.get(Calendar.DAY_OF_MONTH);

        int endYear = endDate.get(Calendar.YEAR);
        int endMonth = endDate.get(Calendar.MONTH);
        int endDay = endDate.get(Calendar.DAY_OF_MONTH);

        try {
            Date date = parseFormat.parse(startTime);
            startTime = displayFormat.format(date);
            date = parseFormat.parse(endTime);
            endTime = displayFormat.format(date);
            int startHour = Integer.parseInt(startTime.split(":")[0]);
            int startMinute = Integer.parseInt(startTime.split(":")[1]);
            int endHour = Integer.parseInt(endTime.split(":")[0]);
            int endMinute = Integer.parseInt(endTime.split(":")[1]);
            DateTime dateTime1 = new DateTime(startYear, startMonth, startDay, startHour, startMinute, 0);
            DateTime dateTime2 = new DateTime(endYear, endMonth, endDay, endHour, endMinute, 0);
            Interval interval = new Interval(dateTime1, dateTime2);
            Duration duration = interval.toDuration();
            hours=duration.getStandardHours();
            long hours = duration.getStandardHours();
        } catch (Exception e) {
            Toast.makeText(ParkingDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
        }
        finish();
    }



}
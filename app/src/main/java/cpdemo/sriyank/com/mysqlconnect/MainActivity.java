package cpdemo.sriyank.com.mysqlconnect;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ArrayList <ClassListItems> itemArrayList;  //List items Array
    private MyAppAdapter myAppAdapter; //Array Adapter
    private ListView listView; // ListView
    private boolean success = false; // boolean
    public static Connection conn;
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView); //ListView Declaration
        itemArrayList = new ArrayList <ClassListItems>(); // Arraylist Initialization

        // Calling Async Task
        SyncData orderData = new SyncData();
        orderData.execute("");
    }

    // Async Task has three overrided methods,
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private class SyncData extends AsyncTask <String, String, String> {
           ProgressDialog progressDialog;
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details!";

        @Override
        protected void onPreExecute() //Starts the progress dailog
        {
            progressDialog=new ProgressDialog(MainActivity.this);
            progressDialog.show();
            progressDialog.setMessage("Synchronizing");

        }

        @Override
        protected String doInBackground(String... strings)  // Connect to the database, write query and add items to array list
        {

             conn = null;
            try {


                conn = Connect.coonectdb();
                Log.i("Value is", String.valueOf(conn));
            Log.i("Connected", "Connected");
            if (conn == null) {
                success = false;
            } else {
                // Change below query according to your own database.
                String query = "SELECT [Product name],[Product name] FROM products";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                if (rs != null) // if resultset not null, I add items to itemArraylist using class created
                {
                    while (rs.next()) {
                        try {
                            itemArrayList.add(new ClassListItems(rs.getString("Product name"), rs.getString("Product name")));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                    msg = "Found";
                    success = true;
                } else {
                    msg = "No Data found!";
                    success = false;
                }
            }
        } catch(Exception e)

        {
            e.printStackTrace();
            Writer writer = new StringWriter();
            Log.i("Mymessage", e.getLocalizedMessage());

            Log.i("Notconnected", "Notconnected");
            e.printStackTrace(new PrintWriter(writer));
            msg = writer.toString();
            success = false;
        }
            return msg;
    }

    @Override
    protected void onPostExecute(String msg) // disimissing progress dialoge, showing error and setting up my ListView
    {
        Toast.makeText(MainActivity.this, msg + "", Toast.LENGTH_LONG).show();
        progressDialog.dismiss();
        if (success == false) {
        } else {
            try {
                myAppAdapter = new MyAppAdapter(itemArrayList, MainActivity.this);
                listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                listView.setAdapter(myAppAdapter);
            } catch (Exception ex) {

            }

        }
    }
}

public class MyAppAdapter extends BaseAdapter         //has a class viewholder which holds
{
    public class ViewHolder {
        TextView textName;
        ImageView imageView;
    }

    public List <ClassListItems> parkingList;

    public Context context;
    ArrayList <ClassListItems> arraylist;

    private MyAppAdapter(List <ClassListItems> apps, Context context) {
        this.parkingList = apps;
        this.context = context;
        arraylist = new ArrayList <ClassListItems>();
        arraylist.addAll(parkingList);
    }

    @Override
    public int getCount() {
        return parkingList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) // inflating the layout and initializing widgets
    {

        View rowView = convertView;
        ViewHolder viewHolder = null;
        if (rowView == null) {
            LayoutInflater inflater = getLayoutInflater();
            rowView = inflater.inflate(R.layout.list_content, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textName = (TextView) rowView.findViewById(R.id.textName);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // here setting up names and images
        viewHolder.textName.setText(parkingList.get(position).getName() + "");
        viewHolder.textName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),parkingList.get(position).getName(),Toast.LENGTH_LONG).show();
                Updata updata=new Updata();
                updata.show(getSupportFragmentManager(),"Update data");

            }
        });
        return rowView;
    }
}
}
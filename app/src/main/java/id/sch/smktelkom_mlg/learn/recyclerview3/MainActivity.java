package id.sch.smktelkom_mlg.learn.recyclerview3;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import id.sch.smktelkom_mlg.learn.recyclerview3.adapter.HotelAdapter;
import id.sch.smktelkom_mlg.learn.recyclerview3.model.Hotel;

import static id.sch.smktelkom_mlg.learn.recyclerview3.InputActivity.REQUEST_IMAGE_GET;
import static id.sch.smktelkom_mlg.learn.recyclerview3.R.drawable.a;

public class MainActivity extends AppCompatActivity implements HotelAdapter.IHotelAdapter
{
    public static final int REQUEST_CODE_ADD = 88;
    public static final int REQUEST_CODE_EDIT = 99;
    public static String HOTEL;

    ArrayList<Hotel> mList = new ArrayList<>();
    HotelAdapter mAdapter;
    int itemPos;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new HotelAdapter(this, mList);
        recyclerView.setAdapter(mAdapter);

        fillData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                goAdd();
            }
        });
    }

    private void goAdd()
    {
        startActivityForResult(new Intent(this,InputActivity.class) ,REQUEST_CODE_ADD);
    }

    private void fillData()
    {
        Resources resources = getResources();
        String [] arJudul = resources.getStringArray(R.array.places);
        String [] arDeskripsi = resources.getStringArray(R.array.place_desc);
        String [] arDetail = resources.getStringArray(R.array.place_details);
        String [] arLokasi = resources.getStringArray(R.array.place_locations);
        TypedArray a = resources.obtainTypedArray(R.array.places_picture);
        String[] arFoto = new String[a.length()];
        for (int i = 0; i < arFoto.length; i++)
        {
            int id = a.getResourceId(i,0);
            arFoto[i] = ContentResolver.SCHEME_ANDROID_RESOURCE+"://"
                    +resources.getResourcePackageName(id)+'/'
                    +resources.getResourceTypeName(id)+'/'
                    +resources.getResourceEntryName(id);
        }
        a.recycle();

        for (int i = 0; i < arJudul.length; i++)
        {
            mList.add(new Hotel(arJudul[i],arDeskripsi[i], arDetail[i], arLokasi[i], arFoto[i]));
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    @Override
    public void doClick(int pos)
    {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(HOTEL,mList.get(pos));
        startActivity(intent);
    }

    @Override
    public void doEdit(int pos)
    {
        itemPos = pos;
        Intent intent = new Intent(this, InputActivity.class);
        intent.putExtra(HOTEL, mList.get(pos));
        startActivityForResult(intent, REQUEST_CODE_EDIT);
    }

    @Override
    public void doDelete(int pos)
    {
        itemPos = pos;
        final Hotel hotel = mList.get(pos);
        mList.remove(itemPos);
        mAdapter.notifyDataSetChanged();

        Snackbar.make(findViewById(R.id.fab),hotel.judul+" Terhapus",Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mList.add(itemPos,hotel);
                mAdapter.notifyDataSetChanged();
            }
        })
                .show();
    }

    @Override
    public void doFav(int pos) {

    }

    @Override
    public void doShare(int pos) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD && resultCode == RESULT_OK)
        {
            Hotel hotel = (Hotel) data.getSerializableExtra(HOTEL);
            mList.add(hotel);
            mAdapter.notifyDataSetChanged();
        }
        else if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK)
        {
            Hotel hotel = (Hotel) data.getSerializableExtra(HOTEL);
            mList.remove(itemPos);
            mList.add(itemPos,hotel);
            mAdapter.notifyDataSetChanged();
        }
    }
}

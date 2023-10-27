package com.androidapp.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    String[] items;

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);



       listView=findViewById(R.id.listviewson);
        runtimepermission();



    }
    public void runtimepermission() {
        Dexter.withContext(this).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                displaysongs();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }


    public ArrayList<File> findsong(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();
        if (files != null) {
            for (File singlefile : files) {
                if (singlefile.isDirectory() && !singlefile.isHidden()) {
                    arrayList.addAll(findsong(singlefile));
                } else {
                    if (singlefile.getName().endsWith(".mp3") || singlefile.getName().endsWith(".wav")) {
                        arrayList.add(singlefile);
                    }
                }
            }
        }
        return arrayList;
    }
    void displaysongs()
    {
        final ArrayList<File>mysongs=findsong(Environment.getExternalStorageDirectory());
        items=new String[mysongs.size()];
        for(int i=0;i< items.length;i++)
        {
            items[i]=mysongs.get(i).getName().toString().replace(".mp3","").replace(".wav","");

        }
//        ArrayAdapter<String> myadapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,items);
//        listView.setAdapter(myadapter);
 customAdapter customAdapter=new customAdapter();
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int p, long id) {
                String songName=items[p];
                startActivity(new Intent(getApplicationContext(), playerActivity.class)
                        .putExtra("songs",mysongs)
                        .putExtra("songname",songName)
                        .putExtra("pos",p));
            }
        });
    }
    class customAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View myview=getLayoutInflater().inflate(R.layout.listitems,null);
            TextView textsong=myview.findViewById(R.id.textsongname);
            textsong.setSelected(true);
            textsong.setText(items[position]);
            return myview;
        }

    }

}
package com.example.test_ssdp.second;

import androidx.annotation.NonNull;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com._8rine.upnpdiscovery.UPnPDevice;
import com._8rine.upnpdiscovery.UPnPDiscovery;
import com.example.test_ssdp.R;

import java.util.ArrayList;
import java.util.HashSet;


public class SecondImplementationActivity extends AppCompatActivity {

    private RecyclerView.Adapter mAdapter;
    private Context mContext;

    private final ArrayList<String> myDataSet = new ArrayList<>();

    private final String TAG = "Second Implementation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_implementation);

        mContext = this;
        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(myDataSet);
        mRecyclerView.setAdapter(mAdapter);

        String customQuery = "M-SEARCH * HTTP/1.1" + "\r\n" +
                "HOST: 239.255.255.250:1900" + "\r\n" +
                "MAN: \"ssdp:discover\"" + "\r\n" +
                "MX: 60"+ "\r\n" +
                //"ST: urn:schemas-upnp-org:service:AVTransport:1" + "\r\n" + // Use for Sonos
                //"ST: urn:schemas-upnp-org:device:InternetGatewayDevice:1" + "\r\n" + // Use for Routers
                "ST: ssdp:all" + "\r\n" + // Use this for all UPnP Devices (DEFAULT)
                "\r\n";
        int customPort = 1900;
        String customAddress = "239.255.255.250";

        Log.d(TAG, "Custom query: \r\n" + customQuery);

        UPnPDiscovery.discoveryDevices(this, new UPnPDiscovery.OnDiscoveryListener() {
            @Override
            public void OnStart() {
                Log.d(TAG, "Start discovery");
                Toast.makeText(mContext, "Start discovery", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnFoundNewDevice(UPnPDevice device) {
                Log.d(TAG, device.getLocation());
                Toast.makeText(mContext, "Found new device", Toast.LENGTH_SHORT).show();
                myDataSet.add(device.toString());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void OnFinish(HashSet<UPnPDevice> devices) {
                // To do something
                Log.d(TAG, "Discovery finished");
                Toast.makeText(mContext, "Discovery finished", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnError(Exception e) {
                Log.d(TAG, "Error");
                Toast.makeText(mContext, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
            // esses 3 parametros extras (customQuery, customAddress, customPort é para uma consulta personalizada, caso queira a padrão, basta não envia-los)
        }, customQuery, customAddress, customPort);

    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private final ArrayList<String> mDataset;

        public class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mTextView;
            ViewHolder(View view) {
                super(view);
                mTextView = view.findViewById(R.id.textView);
            }
        }

        MyAdapter(ArrayList<String> myDataset) {
            mDataset = myDataset;
        }

        @NonNull
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_row_item, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mTextView.setText(mDataset.get(position));
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }




}

package com.example.test_ssdp.third;

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
import com.example.test_ssdp.second.SecondImplementationActivity;

import java.util.ArrayList;
import java.util.HashSet;

import io.resourcepool.ssdp.client.SsdpClient;
import io.resourcepool.ssdp.model.DiscoveryListener;
import io.resourcepool.ssdp.model.DiscoveryRequest;
import io.resourcepool.ssdp.model.SsdpRequest;
import io.resourcepool.ssdp.model.SsdpService;
import io.resourcepool.ssdp.model.SsdpServiceAnnouncement;


public class ThirdImplementationActivity extends AppCompatActivity {

    private RecyclerView.Adapter mAdapter;
    private Context mContext;

    private final ArrayList<String> myDataSet = new ArrayList<>();

    private final String TAG = "Third Implementation";

    private SsdpClient ssdpClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_implementation);

        mContext = this;
        RecyclerView mRecyclerView = findViewById(R.id.recyclerView2);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new Adapter(myDataSet);
        mRecyclerView.setAdapter(mAdapter);

        discoverAllSSDPServices();

    }

    private void discoverAllSSDPServices() {
        Log.d(TAG, "Start discovery");
        Toast.makeText(mContext, "Start discovery", Toast.LENGTH_SHORT).show();

        ssdpClient = SsdpClient.create();
        DiscoveryRequest all = SsdpRequest.discoverAll();
        ssdpClient.discoverServices(all, new DiscoveryListener() {
            @Override
            public void onServiceDiscovered(SsdpService service) {
                Log.d(TAG, service.getLocation());
                Toast.makeText(mContext, "Found new device", Toast.LENGTH_SHORT).show();
                myDataSet.add(service.toString());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onServiceAnnouncement(SsdpServiceAnnouncement announcement) {
                Log.d(TAG, announcement.getLocation());
                Toast.makeText(mContext, "Found new announcement", Toast.LENGTH_SHORT).show();
                myDataSet.add(announcement.toString());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Exception ex) {
                Log.d(TAG, "Error");
                Toast.makeText(mContext, "Error: " + ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "Finish");

        if (ssdpClient != null) {
            ssdpClient.stopDiscovery();
        }

        super.onDestroy();
    }

    public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        private final ArrayList<String> mDataset;

        public class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mTextView;
            ViewHolder(View view) {
                super(view);
                mTextView = view.findViewById(R.id.textView);
            }
        }

        Adapter(ArrayList<String> myDataset) {
            mDataset = myDataset;
        }

        @NonNull
        @Override
        public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                                    int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_row_item, parent, false);
            return new Adapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(Adapter.ViewHolder holder, int position) {
            holder.mTextView.setText(mDataset.get(position));
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }




}

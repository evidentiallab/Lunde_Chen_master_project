package com.mk.wipower.kma;

import java.util.Arrays;

import org.apache.commons.math3.distribution.NormalDistribution;

import weka.estimators.KernelEstimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
     
        Intent intent = new Intent(MainActivity.this, KMAService.class);
        intent.putExtra("what", "start");
        startService(intent);
        
    }

    public void onDestroy()
    {
    	Intent intent = new Intent(MainActivity.this, KMAService.class);
        intent.putExtra("what", "stop");
        startService(intent);
    	super.onDestroy();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
    
}

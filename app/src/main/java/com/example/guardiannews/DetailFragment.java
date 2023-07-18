package com.example.guardiannews;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private Bundle dataFromActivity;
    TextView tvID;

    TextView tvNewsTitle;
    TextView tvApi;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dataFromActivity = getArguments();

        String id = dataFromActivity.getString("id" );
        String title = dataFromActivity.getString("title" );
        String api = dataFromActivity.getString("api" );
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_detail, container, false);
        tvID=view.findViewById(R.id.tvID);
        tvNewsTitle =view.findViewById(R.id.tvNewsTitle);
        tvApi=view.findViewById(R.id.tvApi);
        tvID.setText(id);
        tvNewsTitle.setText(title);
        tvApi.setText(api);

        return view;
    }
}
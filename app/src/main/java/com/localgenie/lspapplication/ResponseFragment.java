package com.localgenie.lspapplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.localgenie.R;

import java.util.ArrayList;

import com.pojo.ResponsePojo;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResponseFragment extends Fragment {


    public ResponseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout=inflater.inflate(R.layout.fragment_response, container, false);
        initialize(layout);
        return layout;
    }

    private void initialize(View layout) {
        RecyclerView rvResponses=layout.findViewById(R.id.mRecyclerView);
        rvResponses.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        ArrayList<ResponsePojo> responseList=new ArrayList<>();
        ResponsePojo responsePojo=new ResponsePojo();
        for (int i=0;i<10;i++){
            responseList.add(responsePojo);
        }
     /*   ResponsesAdapter responsesAdapter=new ResponsesAdapter(getContext(),responseList);
        rvResponses.setAdapter(responsesAdapter);*/

    }

}

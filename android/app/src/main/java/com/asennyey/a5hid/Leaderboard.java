package com.asennyey.a5hid;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Leaderboard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Leaderboard extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ViewGroup cont;

    private ArrayList<String> names;
    private ArrayList<String> buildings;
    private ArrayList<Integer> shits;
    private ListView listView;
    private View view;
    private Context mContext;

    public Leaderboard() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Leaderboard.
     */
    // TODO: Rename and change types and number of parameters
    public static Leaderboard newInstance(String param1, String param2) {
        Leaderboard fragment = new Leaderboard();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext=context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        this.cont = container;
        this.view = v;

        buildings = new ArrayList<String>();
        names = new ArrayList<String>();
        shits = new ArrayList<Integer>();
        buildings.add("bldg1");
        buildings.add("bldg2");
        names.add("name1");
        names.add("name2");
        shits.add(3);
        shits.add(1);

        List<HashMap<String, String>> row = new ArrayList<HashMap<String, String>>();
        for (int i=0; i<buildings.size(); i++) {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("Building", buildings.get(i));
            hm.put("Person", names.get(i));
            hm.put("Shits", shits.get(i).toString());
            row.add(hm);
        }

        String[] from ={"Building", "Person", "Shits"};
        int[] to = {R.id.building, R.id.person, R.id.shits};

        SimpleAdapter simpleAdapter = new
                SimpleAdapter(v.getContext(), row, R.layout.leaderboard_row, from, to);

        listView = (ListView) view.findViewById(R.id.leaderboard_list_view);
        listView.setAdapter(simpleAdapter);

        return v;
    }
}
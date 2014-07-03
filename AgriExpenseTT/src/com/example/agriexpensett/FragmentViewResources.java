package com.example.agriexpensett;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.ListFragment;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FragmentViewResources extends ListFragment{
	SQLiteDatabase db;
	DbHelper dbh;
	ArrayList<String> rList;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbh=new DbHelper(this.getActivity().getBaseContext());
		db=dbh.getReadableDatabase();
		
		populateList();
		ArrayAdapter<String> listAdapt=new ArrayAdapter<String>(getActivity().getBaseContext(),android.R.layout.simple_list_item_1, rList);
		setListAdapter(listAdapt);
	}
	
	private void populateList() {
		rList=new ArrayList<String>();
		DbQuery.getResources(db, dbh, null, rList);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		//returns the inflated layout which contains the listview
		return inflater.inflate(R.layout.fragment_choose_purchase, container, false);
	}
	
	
}

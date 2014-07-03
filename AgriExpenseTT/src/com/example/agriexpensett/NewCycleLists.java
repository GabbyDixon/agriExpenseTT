package com.example.agriexpensett;

import helper.DHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NewCycleLists extends ListFragment {
	String type;
	 ArrayList<String> list;
	SQLiteDatabase db;
	DbHelper dbh;
	int cycleId;
	TextView et_main;
	View view;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbh=new DbHelper(this.getActivity().getBaseContext());
		db=dbh.getReadableDatabase();
		type=getArguments().getString("type");
		populateList();
		Collections.sort(list);
		ArrayAdapter<String> listAdapt = new ArrayAdapter<String>(this.getActivity().getBaseContext(),android.R.layout.simple_list_item_1,list);
		setListAdapter(listAdapt);
	}
		
	private void populateList() {
		list=new ArrayList<String>();
		if(type.equals(DHelper.cat_plantingMaterial)){
			DbQuery.getResources(db, dbh,DHelper.cat_plantingMaterial, list);
		}else if(type.equals("land")){
			list.add("Acre");
			list.add("Hectre");
			list.add("Bed");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		//returns the inflated layout which contains the listview
		view= inflater.inflate(R.layout.list_reuse, container, false);

		et_main=(TextView)view.findViewById(R.id.tv_frag_mainHead_new);
		if(type.equals(DHelper.cat_plantingMaterial)){
			et_main.setText("Select the crop to plant for this cycle");
		}else if(type.equals("land")){
			et_main.setText("Select the type of land you are using");
		}
		return view;
	}
		
	
		 
	
	 @Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			Toast.makeText(getActivity(), getListView().getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();
			Fragment newFragment=null;
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			Bundle b=new Bundle();
			
			
			if(type.equals(DHelper.cat_plantingMaterial)){
				b.putString("type","land");//passes the type of the data we want in the new listfragment
				b.putString(DHelper.cat_plantingMaterial, list.get(position));//passes the crop chosen to the land listfragment
				((NewCycleRedesigned)getActivity()).appendSub(" "+list.get(position));
				newFragment =new NewCycleLists();
			}else if(type.equals("land")){
				b.putString(DHelper.cat_plantingMaterial, getArguments().getString(DHelper.cat_plantingMaterial));
				System.out.println("planting material: "+getArguments().getString(DHelper.cat_plantingMaterial));
				System.out.println(list.get(position));
				b.putString("land", list.get(position));
				((NewCycleRedesigned)getActivity()).appendSub(", "+list.get(position));
				newFragment =new fragmentNewCycleLast();
			}
			newFragment.setArguments(b);
			// Replace whatever is in the fragment_container view with this fragment,
			// and add the transaction to the back stack
			transaction.replace(R.id.NewCycleListContainer, newFragment);
			transaction.addToBackStack(null);
			
			// Commit the transaction
			transaction.commit();
		}

}
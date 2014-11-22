package uwi.dcit.AgriExpenseTT.fragments.help;

import uwi.dcit.AgriExpenseTT.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class HelpManageResourceFragment extends Fragment {

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view =  inflater.inflate(R.layout.fragment_help_article_view_layout4, container, false);
		
		TextView txtHeading = (TextView)view.findViewById(R.id.article_heading);
		txtHeading.setText("Managing Resources");
		
		ImageView img = (ImageView)view.findViewById(R.id.article_image);
		img.setImageDrawable(getResources().getDrawable(R.drawable.help_manage_resources));
		
		TextView txtContent = (TextView)view.findViewById(R.id.article_text); 
		txtContent.setText(getResources().getString(R.string.help_manage_resources));
		
		ImageView imgView2 =(ImageView)view.findViewById(R.id.article_image_2);
		imgView2.setImageDrawable(getResources().getDrawable(R.drawable.help_use_resource_home));
		
		TextView txt2 = (TextView)view.findViewById(R.id.article_text_2);
		txt2.setText(getResources().getString(R.string.help_manage_resources_2));
		
		ImageView imgView3 =(ImageView)view.findViewById(R.id.article_image_3);
		imgView3.setImageDrawable(getResources().getDrawable(R.drawable.help_use_resource_item));
		
		TextView txt3 = (TextView)view.findViewById(R.id.article_text_3);
		txt3.setText(getResources().getString(R.string.help_manage_resources_3));
		
		ImageView imgView4 =(ImageView)view.findViewById(R.id.article_image_4);
		imgView4.setImageDrawable(getResources().getDrawable(R.drawable.help_use_resource_item));
		
		TextView txt4 = (TextView)view.findViewById(R.id.article_text_4);
		txt4.setText(getResources().getString(R.string.help_manage_resources_4));
		return view;
	}
	
}

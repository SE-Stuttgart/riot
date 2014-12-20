package de.uni_stuttgart.riot.android;

import java.util.ArrayList;
import java.util.List;

import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.communication.TestString;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CodeLearnAdapter extends BaseAdapter {

	private MainActivity mainActivity;

	List<TestString> codeLearnChapterList;

	public CodeLearnAdapter(MainActivity mainActivity, List<TestString> list) {
		this.mainActivity = mainActivity;
		codeLearnChapterList = list;
	}


	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {

		if (arg1 == null) {
			LayoutInflater inflater = (LayoutInflater) mainActivity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arg1 = inflater.inflate(R.layout.listitem, arg2, false);
		}

		TextView chapterName = (TextView) arg1.findViewById(R.id.textView1);
		TextView chapterDesc = (TextView) arg1.findViewById(R.id.textView2);

		TestString chapter = codeLearnChapterList.get(arg0);

		chapterName.setText(chapter.getId());
		chapterDesc.setText(chapter.getContent());

		return arg1;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return codeLearnChapterList.size();
	}

	@Override
	public TestString getItem(int arg0) {
		// TODO Auto-generated method stub
		return codeLearnChapterList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

}

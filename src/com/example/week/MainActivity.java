package com.example.week;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends ListActivity {
	
	private String[] data = {"yesterday","today","tomorrow"};
	private ArrayList<ContactInfo> contactInfo;
	
	private static class ContactInfo
	{
		String name;
		String tel;
	}
	
	private boolean GetContactInfo()
	{
		Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		if(cursor == null)
		{
			return false;
		}
		Log.d("hcx","cursor ready");
		
		contactInfo = new ArrayList<ContactInfo>();
		while (cursor.moveToNext())
		{
			ContactInfo info = new ContactInfo();
			info.name = "";
			info.tel = "";
			
			info.name =	cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			String contactId =	cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			
			Cursor phones  = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId , null, null);
			if (phones == null)
			{
				continue;
			}
			while(phones.moveToNext())
			{
				info.tel = (phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))).replace(" ", "");
			}
			phones.close();
			
			contactInfo.add(info);
		}
		return true;
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if(!GetContactInfo())
        {
        	Log.d("hcx","Read contact info error");
        	return;
        }
        
        ArrayList<String> items = new ArrayList<String>();
        for(int i=0;i<contactInfo.size();i++)
        {
        	items.add(contactInfo.get(i).name + "\t" + contactInfo.get(i).tel);
        	Log.d("hcx","add contact: name = " + contactInfo.get(i).name + " tel = " + contactInfo.get(i).tel);
        }
        
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items));
        
        ListView listView = getListView();
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View item, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Log.d("hcx","item text = " + ((TextView)item).getText() + " position = " + position);
				String tel = ((String)(((TextView)item).getText())).split("\t")[1];
				
				Uri uri =  Uri.parse("tel:" + tel);
				MainActivity.this.startActivity(
						new Intent()
							.setAction(Intent.ACTION_CALL)
							.setData(uri)
				);
				Log.d("hcx","split tel = " + tel);
			}
		});
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.jonasschindler.addressbook;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends Activity {

    //private static DBAdapter dbAdapter;

    private ListView listView;
    private int contactId;
    private byte[] image;
    private String firstName, lastName;
    ArrayList names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get data from db and display
        //dbAdapter = new DBAdapter(this);
        showAllContacts();
        handleIntent(getIntent());

        //SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
    }

    public void showAllContacts() {
        // Receive the names and images from the database
        //final ArrayList contactId = dbAdapter.getAllIds();
        //final ArrayList contacts = dbAdapter.getAllNames();
        //final ArrayList images = dbAdapter.getAllImages();

        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,R.layout.rowlayout,R.id.itemname,contacts);
        //listView = (ListView) findViewById(R.id.contactsListView);
        //listView.setAdapter(arrayAdapter);

        String columns[] = new String[] {
                ContentProviderContract.ID,
                ContentProviderContract.FIRSTNAME,
                ContentProviderContract.LASTNAME,
                ContentProviderContract.IMAGE
        };

        ContentResolver cr = getContentResolver();
        final Uri contactsUri = ContentProviderContract.CONTACTS_URI;
        Cursor cursor = cr.query(contactsUri, columns, null, null, null, null);
        final ArrayList id = new ArrayList();
        names = new ArrayList();
        ArrayList images = new ArrayList();
        while(cursor.moveToNext()) {
            contactId = cursor.getInt(0);
            firstName = cursor.getString(1);
            lastName = cursor.getString(2);
            image = cursor.getBlob(3);
            id.add(contactId);
            names.add(firstName +" "+ lastName);
            images.add(image);
        }
        cursor.close();

        CustomArrayAdapter<String> customArrayAdapter = new CustomArrayAdapter<String>(this, names, images);
        listView = (ListView) findViewById(R.id.contactsListView);
        listView.setAdapter(customArrayAdapter);

        // Click Listener for listView that reads the names of the list items that are clicked
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int contactId = (int) id.get(i);
                Bundle bundle = new Bundle();
                bundle.putInt("contactId",contactId);
                Intent intent = new Intent(getApplicationContext(), com.jonasschindler.addressbook.ViewContactActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("addressApp", "Pos: "+i+"Id: "+ id.get(i));
                return false;
            }
        });
    }

    // Called when add contact button is clicked, opens the Add ContactActivity
    public void addContact(View view) {
        Intent intent = new Intent(this, AddContactActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("addressApp","onNewIntent called");
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        Log.d("addressApp","handleIntent: "+intent);

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d("addressApp","query: "+query);
            if (names.contains(query)) {

            }
            //use the query to search your data somehow
        }
    }

    // Ensuring to display the latest data when resuming the activity
    public void onResume() {
        super.onResume();
        this.showAllContacts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("addressApp", "onQueryTextSubmit");
                //callSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("addressApp", "onQueryTextChange");
//              if (searchView.isExpanded() && TextUtils.isEmpty(newText)) {
                //callSearch(newText);
//              }
                return true;
            }

            public void callSearch(String query) {
                //Do searching
            }

        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

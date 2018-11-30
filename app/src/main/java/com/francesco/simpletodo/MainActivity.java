package com.francesco.simpletodo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //these are null until we initialize
    ArrayList<String> items; //model
    ArrayAdapter<String> itemsAdapter; //wires the model to the view
    ListView lvItems; //instance of list view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readItems(); //get data
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,items); // (reference to activity, type of item, itemList)
        lvItems = (ListView) findViewById(R.id.lvItems); //referencing to the list view in activity_main
        lvItems.setAdapter(itemsAdapter); //wire the adapter to the list view

//        //mock data
//        items.add("First item");
//        items.add("Second item");
//        items.add("Third item");

        //get listener going
        setupListViewListener();
    }

    //add function
    public void onAddItem(View v) {
        //save item
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem); //get reference to edit text
        String itemText = etNewItem.getText().toString(); //get text user wrote
        itemsAdapter.add(itemText); //add to items adapter
        writeItems(); //save items
        etNewItem.setText(""); //clear user textfield

        //let user know operation worked
        Toast.makeText(getApplicationContext(), "Item added to list", Toast.LENGTH_SHORT).show(); // (application context, text we want to display, duration)
    }

    //remove item
    private void setupListViewListener() {

        Log.i("Main Activity", "Setting up listener on list view");
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("Main Activity", "Item removed from list: " + position);
                items.remove(position); //removing item from the postion it's held
                itemsAdapter.notifyDataSetChanged(); //refresh
                writeItems(); //save items
                return true; //return true because we're going to use this
            }
        });
    }

    //get the devices data file
    private File getDataFile() {
        return new File(getFilesDir(), "todo.txt");
    }

    //read items
    private void readItems() {
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset())); //reads file content one at a time and returns a list of strings
        } catch (IOException e) {
            Log.e("Main Activity", "Error reading file", e);
            items = new ArrayList<>(); //just to make sure object is instantiated correctly
        }
    }

    //write on file
    private void writeItems() {
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("Main Activity", "Error writing file", e);
        }
    }
}

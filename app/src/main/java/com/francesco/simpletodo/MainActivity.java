package com.francesco.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

    // a number code to identify activity the edit activity
    public final static int EDIT_REQUEST_CODE = 20;
    //keys used for passing data between activities
    public final static String ITEM_TEXT = "itemText";
    public final static String ITEM_POSITION = "itemPosition";

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

        //for delete
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

        //set up item listener for edit (regular click)
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //create new activity
                Intent i = new Intent(MainActivity.this, EditItemActivity.class); // (activity instance calling the intent, class we want to create)

                //pass the data being edited
                i.putExtra(ITEM_TEXT, items.get(position)); //get item text
                i.putExtra(ITEM_POSITION, position); //position pressed

                //display activity to user
                startActivityForResult(i, EDIT_REQUEST_CODE);
            }
        });
    }

    //handle result from edit activity


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //check if edit activity completed
        if (resultCode == RESULT_OK && requestCode == EDIT_REQUEST_CODE) {
            //extract updated item text from result intent extras
            String updatedItem = data.getExtras().getString(ITEM_TEXT);

            //extract original position of edited item
            int postition = data.getExtras().getInt(ITEM_POSITION);

            //update the model with the new item text at the edited position
            items.set(postition, updatedItem);

            //notifiy adapter that model changed
            itemsAdapter.notifyDataSetChanged();

            //persis the changed model
            writeItems();

            //notify user that operation completed
            Toast.makeText(this, "Item updated successfully", Toast.LENGTH_SHORT).show();
        }
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

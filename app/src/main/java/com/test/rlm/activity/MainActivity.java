package com.test.rlm.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.test.rlm.adapters.BooksAdapter;
import com.test.rlm.model.Book;
import com.test.rlm.realm.RealmController;

import java.util.ArrayList;

import app.androidhive.info.realm.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private BooksAdapter adapter;
    private FloatingActionButton fab;
    private RecyclerView recycler;
    private RealmController mRealmController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        recycler = (RecyclerView) findViewById(R.id.recycler);
        mRealmController = new RealmController();

        //set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupRecycler();
        setRealmData();

        Toast.makeText(this, "Press card item for edit, long press to remove item", Toast.LENGTH_LONG).show();

        //add new item
        fab.setOnClickListener(this);
    }


    private void setupRecycler() {
        recycler.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(layoutManager);

        // create an empty adapter and add it to the recycler view
        adapter = new BooksAdapter(this, mRealmController, mRealmController.getBooks());
        recycler.setAdapter(adapter);
    }

    private void setRealmData() {
        final ArrayList<Book> dummyBooks = DummyData.getDummyBooks();
        mRealmController.saveAllOrUpdate(dummyBooks);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                View content = getLayoutInflater().inflate(R.layout.edit_item, null);
                final EditText editTitle = (EditText) content.findViewById(R.id.title);
                final EditText editAuthor = (EditText) content.findViewById(R.id.author);
                final EditText editThumbnail = (EditText) content.findViewById(R.id.thumbnail);

                final DialogInterface.OnClickListener OkClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Book book = new Book();
                        book.setId(mRealmController.getBooks().size() + 1);
                        book.setTitle(editTitle.getText().toString());
                        book.setAuthor(editAuthor.getText().toString());
                        book.setImageUrl(editThumbnail.getText().toString());

                        if (editTitle.getText() == null || editTitle.getText().toString().equals("") || editTitle.getText().toString().equals(" ")) {
                            Toast.makeText(MainActivity.this, "Entry not saved, missing title", Toast.LENGTH_SHORT).show();
                        } else {
                            // Persist your data easily
                            mRealmController.save(book);
                            adapter.notifyDataSetChanged();
                            recycler.scrollToPosition(mRealmController.getBooks().size() - 1);

                            /**
                             * Tried to do the same in background thread
                             */
                                   /* new AsyncTask<Void, Void, Void>() {
                                        @Override
                                        protected Void doInBackground(Void... params) {
                                            mRealmController.save(book);
                                            return null;
                                        }

                                        @Override
                                        protected void onPostExecute(Void aVoid) {
                                            super.onPostExecute(aVoid);
                                            adapter.notifyDataSetChanged();
                                            recycler.scrollToPosition(mRealmController.getBooks().size() - 1);
                                        }
                                    }.execute();*/

                        }

                    }

                };
                final DialogInterface.OnClickListener cancelClickListener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(content)
                        .setTitle("Add book")
                        .setPositiveButton(android.R.string.ok, OkClickListener)
                        .setNegativeButton(android.R.string.cancel, cancelClickListener);
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }
    }


}

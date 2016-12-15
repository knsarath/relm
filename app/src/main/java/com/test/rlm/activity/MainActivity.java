package com.test.rlm.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.test.rlm.adapters.BooksAdapter;
import com.test.rlm.model.Book;
import com.test.rlm.realm.RealmController;

import java.util.ArrayList;

import app.androidhive.info.realm.R;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
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

        final boolean sampleDataAlreadyAdded = Pref.isDummyDataAdded(this);
        if (!sampleDataAlreadyAdded) {
            final ArrayList<Book> dummyBooks = DummyData.getDummyBooks();
            /**
             * save dummy books to db in background thread
             */
            mRealmController.saveAllAsync(dummyBooks, new RealmController.WriteCallback() {
                @Override
                public void onSuccess() {
                    Pref.dummyDataAdded(MainActivity.this, true); // on success set preference value to indicate dummy data has already been added. So next time it wont add again
                }

                @Override
                public void onError(Throwable error) {
                    Log.e(TAG, error.getMessage());
                }
            });

        }


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

        /**
         * read all books in sorted order of title
         */
        mRealmController.getAllRealmAsync(Book.class, new RealmController.Callback<RealmResults<Book>>() {
            @Override
            public void onSuccess(RealmResults<Book> result) {
                adapter = new BooksAdapter(MainActivity.this, mRealmController, result);
                recycler.setAdapter(adapter);
            }

            @Override
            public void onError() {

            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                new BookDialog().show(this, "Add book", new BookDialog.BookDialogListener() {
                    @Override
                    public void onOkClicked(final Book book) {
                        mRealmController.saveAsync(book, new RealmController.WriteCallback() {
                            @Override
                            public void onSuccess() {
                                adapter.notifyDataSetChanged();
                                recycler.scrollToPosition(mRealmController.getAllRealm(Book.class).size() - 1);
                            }

                            @Override
                            public void onError(Throwable error) {
                                Log.e(TAG, error.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onCancelCliked() {

                    }
                });
                break;
        }
    }


}

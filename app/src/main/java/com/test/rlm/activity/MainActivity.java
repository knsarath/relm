package com.test.rlm.activity;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

import com.test.rlm.adapters.BooksAdapter;
import com.test.rlm.model.Book;
import com.test.rlm.realm.RealmController;

import java.util.ArrayList;

import app.androidhive.info.realm.R;
import io.realm.RealmList;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SearchView.OnQueryTextListener {
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
                    setupRecycler();
                }

                @Override
                public void onError(Throwable error) {
                    Log.e(TAG, error.getMessage());
                    setupRecycler();
                }
            });

        } else {
            setupRecycler();
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
        mRealmController.getAllSortedAsync(Book.class, "title", Sort.ASCENDING, new RealmController.Callback<RealmList<Book>>() {
            @Override
            public void onSuccess(RealmList<Book> result) {
                adapter = new BooksAdapter(mRealmController, new ArrayList<>(result));
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText);
        return true;
    }
}

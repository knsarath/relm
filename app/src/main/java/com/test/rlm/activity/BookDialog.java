package com.test.rlm.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.test.rlm.model.Book;
import com.test.rlm.realm.RealmController;

import app.androidhive.info.realm.R;

/**
 * Created by sarath on 14/12/16.
 */
public class BookDialog {
    private static RealmController mRealmController;


    public interface BookDialogListener {
        void onOkClicked(Book book);

        void onCancelCliked();
    }

    public static void show(final Context context, String title, final Book book, final BookDialogListener bookDialogListener) {
        if (mRealmController == null) {
            mRealmController = new RealmController();
        }
        View content = LayoutInflater.from(context).inflate(R.layout.edit_item, null);
        final EditText editTitle = (EditText) content.findViewById(R.id.title);
        final EditText editAuthor = (EditText) content.findViewById(R.id.author);
        final EditText editThumbnail = (EditText) content.findViewById(R.id.thumbnail);
        if (book != null) {
            editTitle.setText(book.getTitle());
            editAuthor.setText(book.getAuthor());
            editThumbnail.setText(book.getImageUrl());
        }

        final DialogInterface.OnClickListener okClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /**
                 * if no book obect is passed through constructor , create a new book object with a new ID.
                 * if already one book object is passed through constructor , dont create a new Book object , but just update the fileds (Keep the id of the book because it is the primary key)
                 */
                if (book == null) {
                    final Book book = new Book();
                    book.setId(mRealmController.getBooks().size() + 1);
                }


                book.setTitle(editTitle.getText().toString());
                book.setAuthor(editAuthor.getText().toString());
                book.setImageUrl(editThumbnail.getText().toString());

                if (editTitle.getText() == null || editTitle.getText().toString().equals("") || editTitle.getText().toString().equals(" ")) {
                    Toast.makeText(context, "Entry not saved, missing title", Toast.LENGTH_SHORT).show();
                } else {
                    if (bookDialogListener != null) {
                        bookDialogListener.onOkClicked(book);
                    }

                }

            }

        };

        final DialogInterface.OnClickListener cancelClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (bookDialogListener != null) {
                    bookDialogListener.onCancelCliked();
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(content)
                .setTitle(title)
                .setPositiveButton(android.R.string.ok, okClickListener)
                .setNegativeButton(android.R.string.cancel, cancelClickListener);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void show(final Context context, String title, final BookDialogListener bookDialogListener) {
        show(context, title, null, bookDialogListener);
    }
}
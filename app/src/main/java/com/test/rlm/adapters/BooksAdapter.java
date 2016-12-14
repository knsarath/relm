package com.test.rlm.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.test.rlm.activity.BookDialog;
import com.test.rlm.model.Book;
import com.test.rlm.realm.RealmController;

import app.androidhive.info.realm.R;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;


public class BooksAdapter extends RealmRecyclerViewAdapter<Book, BooksAdapter.CardViewHolder> {

    private RealmController mRealmController;

    public BooksAdapter(@NonNull Context context, RealmController realmController, @Nullable OrderedRealmCollection<Book> data) {
        super(context, data, true);
        mRealmController = realmController;
        if (mRealmController == null) {
            throw new RuntimeException("RealmController cannot be null in BooksAdapter");
        }
    }

    // create new views (invoked by the layout manager)
    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate a new card view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_books, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, final int position) {
        // get the article
        final Book book = getItem(position);
        // set the title and the snippet
        holder.textTitle.setText(book.getTitle());
        holder.textAuthor.setText(book.getAuthor());
        holder.textDescription.setText(book.getDescription());

        // load the background image
        if (book.getImageUrl() != null) {
            Glide.with(context)
                    .load(book.getImageUrl().replace("https", "http"))
                    .asBitmap()
                    .fitCenter()
                    .into(holder.imageBackground);
        }

        //remove single match from realm
        holder.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final String title = getItem(position).getTitle();
                mRealmController.delete(Book.class, position);
                notifyDataSetChanged();
                Toast.makeText(context, title + " is removed from Realm", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        //update single match from realm
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Book book = mRealmController.get(Book.class, position);
                new BookDialog().show(context, "Edit Book", book, new BookDialog.BookDialogListener() {
                    @Override
                    public void onOkClicked(Book book) {
                        mRealmController.saveOrUpdate(book);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelCliked() {

                    }
                });
            }
        });

    }


    public static class CardViewHolder extends RecyclerView.ViewHolder {

        public CardView card;
        public TextView textTitle;
        public TextView textAuthor;
        public TextView textDescription;
        public ImageView imageBackground;

        public CardViewHolder(View itemView) {
            super(itemView);

            card = (CardView) itemView.findViewById(R.id.card_books);
            textTitle = (TextView) itemView.findViewById(R.id.text_books_title);
            textAuthor = (TextView) itemView.findViewById(R.id.text_books_author);
            textDescription = (TextView) itemView.findViewById(R.id.text_books_description);
            imageBackground = (ImageView) itemView.findViewById(R.id.image_background);
        }
    }
}
package com.test.rlm.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.test.rlm.activity.BookDialog;
import com.test.rlm.model.Book;
import com.test.rlm.realm.RealmController;

import java.util.ArrayList;
import java.util.List;

import app.androidhive.info.realm.R;
import io.realm.Case;


public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.CardViewHolder> implements Filterable {

    private RealmController mRealmController;
    private List<Book> mBookList = new ArrayList<>();

    public BooksAdapter(RealmController realmController, ArrayList<Book> bookArrayList) {
        mRealmController = realmController;
        mBookList = bookArrayList;
    }

    // create new views (invoked by the layout manager)
    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate a new card view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_books, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, final int position) {
        // get the article
        final Book book = mBookList.get(position);
        // set the title and the snippet
        holder.textTitle.setText(book.getTitle());
        holder.textAuthor.setText(book.getAuthor());
        holder.textDescription.setText(book.getDescription());

        // load the background image
        if (book.getImageUrl() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(book.getImageUrl().replace("https", "http"))
                    .asBitmap()
                    .fitCenter()
                    .into(holder.imageBackground);
        }

        //remove single match from realm
        holder.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final String title = mBookList.get(position).getTitle();
                mRealmController.delete(Book.class, "id", book.getId());
                notifyDataSetChanged();
                Toast.makeText(holder.itemView.getContext(), title + " is removed from Realm", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        //update single match from realm
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Book book = mRealmController.get(Book.class, position);
                new BookDialog().show(holder.itemView.getContext(), "Edit Book", book, new BookDialog.BookDialogListener() {
                    @Override
                    public void onOkClicked(final Book book) {
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

    @Override
    public int getItemCount() {
        return mBookList.size();
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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults filterResults = new FilterResults();
                List<Book> tempList = new ArrayList<>();
                if (constraint != null && constraint.length() > 0) {
                    /**
                     * select all books with title contains the search query
                     */
                    tempList = mRealmController.selectAllContains(Book.class, "title", constraint.toString(), Case.INSENSITIVE);
                    // since performFiltering method already is in seperate thread, no need for selectAllContainsAsync
                } else {
                    /**
                     * if(search query is empty , select all items from db
                     */
                    tempList = mRealmController.getAll(Book.class);
                }
                filterResults.values = tempList;
                filterResults.count = tempList.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.values != null) {
                    mBookList = (List<Book>) results.values;
                    notifyDataSetChanged();
                }
            }
        };
    }
}
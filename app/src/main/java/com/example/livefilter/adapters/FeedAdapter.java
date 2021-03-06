package com.example.livefilter.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.livefilter.Details;
import com.example.livefilter.R;
import com.example.livefilter.models.FilterPost;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private Context context;
    private List<FilterPost> filters;

    public FeedAdapter(Context context, List<FilterPost> filters) {
        this.context = context;
        this.filters = filters;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate (create) a view for every possible item on the screen
        View view = LayoutInflater.from(context).inflate(R.layout.item_filter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // get post at position
        FilterPost filterPost = filters.get(position);
        // bind it to viewholder
        holder.bind(filterPost);

    }

    @Override
    public int getItemCount() {
        return filters.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvName;
        private TextView tvDescription;
        private TextView tvUsername;
        private ImageView ivAfter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // find views for view elements of filterPost
            tvName = itemView.findViewById(R.id.tvName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivAfter = itemView.findViewById(R.id.ivAfter);

            // add OnClickListener to itemView
            itemView.setOnClickListener(this);
        }


        public void bind(FilterPost filterPost) {
            // bind post data to view elements
            tvName.setText(filterPost.getName());
            tvUsername.setText(filterPost.getUser().getUsername());
            tvDescription.setText(filterPost.getDescription());
            // load images using Glide
            ParseFile afterImage = filterPost.getAfter();
            if(afterImage != null) {
                Glide.with(context).load(afterImage.getUrl()).into(ivAfter);
            }
        }

        // when clicked, launch filter details activity
        @Override
        public void onClick(View view) {
            // get adapter position
            int position = getAdapterPosition();
            // if position is valid, show activity
            if (position != RecyclerView.NO_POSITION) {
                FilterPost filterPost = filters.get(position);
                // make intent for new activity and parcel filter details into new intent
                Intent intent = new Intent(context, Details.class);
                intent.putExtra(FilterPost.class.getSimpleName(), Parcels.wrap(filterPost));
                // start new activity
                context.startActivity(intent);
            }

        }
    }
}

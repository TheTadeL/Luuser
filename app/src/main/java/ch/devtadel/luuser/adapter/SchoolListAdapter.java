package ch.devtadel.luuser.adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ch.devtadel.luuser.R;
import ch.devtadel.luuser.SchoolActivity;
import ch.devtadel.luuser.model.School;
import ch.devtadel.luuser.model.SchoolClass;

public class SchoolListAdapter extends RecyclerView.Adapter<SchoolListAdapter.ViewHolder>{
    private List<School> dataset;

    static class ViewHolder extends RecyclerView.ViewHolder {

        CardView schoolListItem;
        ViewHolder(CardView cardView) {
            super(cardView);

            schoolListItem = cardView;
            schoolListItem.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    TextView schoolNameTV = (TextView) v.findViewById(R.id.tv_schoolname);

                    v.getContext().startActivity(new Intent(v.getContext(), SchoolActivity.class).putExtra(SchoolActivity.SCHOOL_NAME, schoolNameTV.getText()));
                }
            });
        }
    }

    public SchoolListAdapter(List<School> data) {
        dataset = data;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public SchoolListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // create a new view
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_school, parent, false);

        return new SchoolListAdapter.ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull SchoolListAdapter.ViewHolder holder, int position) {
        TextView schoolNameTV = (TextView) holder.schoolListItem.findViewById(R.id.tv_schoolname);
        schoolNameTV.setText(dataset.get(position).getName());

        TextView schoolPlaceTV = (TextView) holder.schoolListItem.findViewById(R.id.tv_place);
        schoolPlaceTV.setText(String.valueOf(dataset.get(position).getPlace()));

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}

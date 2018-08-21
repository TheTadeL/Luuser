package ch.devtadel.luuser.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ch.devtadel.luuser.R;
import ch.devtadel.luuser.model.SchoolClass;

public class ClassListAdapter extends RecyclerView.Adapter<ClassListAdapter.ViewHolder>{
    private List<SchoolClass> dataset;

    static class ViewHolder extends RecyclerView.ViewHolder {

        CardView classListItem;
        ViewHolder(CardView cardView) {
            super(cardView);

            classListItem = cardView;
            classListItem.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                   //TODO: Auf Klasse gedrückt!
                }
            });
        }
    }

    public ClassListAdapter(List<SchoolClass> data) {
        dataset = data;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ClassListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // create a new view
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_class, parent, false);

        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView klassennameTV = (TextView) holder.classListItem.findViewById(R.id.tv_card_name);
        klassennameTV.setText(dataset.get(position).getName());

        TextView cntStudentsTV = (TextView) holder.classListItem.findViewById(R.id.tv_cnt_students);
        cntStudentsTV.setText(String.valueOf(dataset.get(position).getCntStudents()));

        TextView cntChecksTV = (TextView) holder.classListItem.findViewById(R.id.tv_cnt_checks);
        cntChecksTV.setText(String.valueOf(dataset.get(position).getCntChecks()));

        TextView cntLouseTV = (TextView) holder.classListItem.findViewById(R.id.tv_cnt_louse);
        cntLouseTV.setText(String.valueOf(dataset.get(position).getCntLouse()));
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}

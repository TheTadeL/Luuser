package ch.devtadel.luuser.adapter;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;

import ch.devtadel.luuser.R;
import ch.devtadel.luuser.model.Check;
import ch.devtadel.luuser.model.SchoolClass;

public class ChecksListAdapter extends RecyclerView.Adapter<ChecksListAdapter.ViewHolder>{
    private List<Check> dataset;

    static class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout checkListItem;
        ViewHolder(ConstraintLayout constraintLayout) {
            super(constraintLayout);

            checkListItem = constraintLayout;
            checkListItem.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //TODO: Auf Check gedrückt!
                }
            });
        }
    }

    public ChecksListAdapter(List<Check> data) {
        dataset = data;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ChecksListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // create a new view
        ConstraintLayout constraintLayout = (ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.constraint_check, parent, false);

        return new ViewHolder(constraintLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        TextView schoolNameTV = (TextView) holder.checkListItem.findViewById(R.id.tv_check_school_name);
        schoolNameTV.setText(dataset.get(position).getSchoolName());

        TextView classNameTV = (TextView) holder.checkListItem.findViewById(R.id.tv_check_class_name);
        classNameTV.setText(String.valueOf(dataset.get(position).getClassName()));

        TextView dateTV = (TextView) holder.checkListItem.findViewById(R.id.tv_check_date);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String dateStr = sdf.format(dataset.get(position).getDate());
        dateTV.setText(String.valueOf(dateStr));

        TextView cntLouseTV = (TextView) holder.checkListItem.findViewById(R.id.tv_check_count_louse);
        cntLouseTV.setText(String.valueOf(dataset.get(position).getLouseCount()));

        TextView cntStudentsTV = (TextView) holder.checkListItem.findViewById(R.id.tv_check_count_students);
        cntStudentsTV.setText(String.valueOf(dataset.get(position).getStudentCount()));

        ImageButton followUpCheckBTN = holder.checkListItem.findViewById(R.id.btn_follow_up_check);
        followUpCheckBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(holder.checkListItem.getContext(), "Nachkontrolle hinzufügen", Toast.LENGTH_LONG).show();
            }
        });

        holder.checkListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}

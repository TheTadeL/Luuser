package ch.devtadel.luuser.adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ch.devtadel.luuser.CheckActivity;
import ch.devtadel.luuser.R;
import ch.devtadel.luuser.model.Check;

public class SchoolCheckListAdapter extends RecyclerView.Adapter<SchoolCheckListAdapter.ViewHolder>{
    private List<Check> dataset;

    static class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout checkListItem;
        ViewHolder(ConstraintLayout constraintLayout) {
            super(constraintLayout);

            checkListItem = constraintLayout;
        }
    }

    public SchoolCheckListAdapter(List<Check> data) {
        dataset = data;
    }

    // Views mit dem Layoutmanager erstellen
    @NonNull
    @Override
    public SchoolCheckListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Neue View erstellen.
        ConstraintLayout constraintLayout = (ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.constraint_check, parent, false);

        return new ViewHolder(constraintLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        TextView schoolNameTV = holder.checkListItem.findViewById(R.id.tv_checkitem_school_name);
        schoolNameTV.setVisibility(View.GONE);

        TextView classNameTV = holder.checkListItem.findViewById(R.id.tv_checkitem_class_name);
        classNameTV.setText(String.valueOf(dataset.get(position).getClassName()));

        TextView dateTV = holder.checkListItem.findViewById(R.id.tv_checkitem_date);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String dateStr = sdf.format(dataset.get(position).getDate());
        dateTV.setText(String.valueOf(dateStr));

        TextView cntLouseTV = holder.checkListItem.findViewById(R.id.tv_checkitem_count_louse);
        cntLouseTV.setText(String.valueOf(dataset.get(position).getLouseCount()));

        TextView cntStudentsTV = holder.checkListItem.findViewById(R.id.tv_checkitem_count_students);
        cntStudentsTV.setText(String.valueOf(dataset.get(position).getStudentCount()));

        final TextView documentRefTV = holder.checkListItem.findViewById(R.id.tv_document_ref);
        documentRefTV.setText(dataset.get(position).getDocumentId());

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
                holder.checkListItem.getContext().startActivity(new Intent(holder.checkListItem.getContext(), CheckActivity.class)
                        .putExtra(CheckActivity.DOCUMENT_ID, documentRefTV.getText().toString()));
            }
        });

        holder.checkListItem.setBackgroundColor(holder.checkListItem.getResources().getColor(R.color.common_google_signin_btn_text_dark_default, null));
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}

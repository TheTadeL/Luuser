package ch.devtadel.luuser.DAL.FireStore;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ch.devtadel.luuser.AddSchoolActivity;
import ch.devtadel.luuser.MainActivity;
import ch.devtadel.luuser.NewCheckActivity;
import ch.devtadel.luuser.SchoolActivity;
import ch.devtadel.luuser.model.School;
import ch.devtadel.luuser.model.SchoolClass;

public class SchoolDao {
    private static final String TAG = "SchoolDao";

    private static final String FS_NAME = "Name";
    private static final String FS_PLACE = "Ort";
    private static final String FS_SCHOOL_NAME = "Schulname";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String DB_SCHOOLS = "Schulen";
    private final String DB_CLASSES = "Klassen";

    public void loadSchoolList(final RecyclerView.Adapter adapter){
        db.collection(DB_SCHOOLS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            MainActivity.data.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                School school = new School(document.getData().get(FS_NAME).toString(), document.getData().get(FS_PLACE).toString());
                                MainActivity.data.add(school);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void newCheck(SchoolClass schoolClass, Date date, int students, int louse){

    }

    public void setupSchoolSpinner(final Context context){
        db.collection(DB_SCHOOLS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            NewCheckActivity.schoolNames.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                NewCheckActivity.schoolNames.add(document.getData().get(FS_NAME).toString());
                            }
                            context.sendBroadcast(new Intent()
                                    .setAction(NewCheckActivity.ACTION_STRING_SCHOOLNAMES_LOADED)
                                    .addCategory(Intent.CATEGORY_DEFAULT)
                            );
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    public void setupClassSpinner(final Context context, final String schoolName){
        db.collection(DB_CLASSES)
                .whereEqualTo(FS_SCHOOL_NAME, schoolName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            NewCheckActivity.classNames.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                NewCheckActivity.classNames.add(document.getData().get(FS_NAME).toString());
                            }
                            context.sendBroadcast(new Intent()
                                    .setAction(NewCheckActivity.ACTION_STRING_CLASSNAMES_LOADED)
                                    .addCategory(Intent.CATEGORY_DEFAULT)
                            );
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void createSchoolInFS(School newSchool){
        //Schule erstellen
        Map<String, String> schoolMap = new HashMap<>();
        schoolMap.put(FS_NAME, newSchool.getName());
        schoolMap.put(FS_PLACE, newSchool.getPlace());

        //User in der Datenbank abspeichern
        db.collection(DB_SCHOOLS)
                .add(schoolMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void getSchoolToPage(final String name, final Context context){
        db.collection(DB_SCHOOLS)
                .whereEqualTo(FS_NAME, name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                School school = new School(document.getData().get(FS_NAME).toString(), document.getData().get(FS_PLACE).toString());
                                SchoolActivity.school = school;

                                Intent intent = new Intent();
                                intent.setAction(SchoolActivity.ACTION_STRING_SCHOOL_LOADED);
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                context.sendBroadcast(intent);

                                return;
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void getClassesToPage(School school, final RecyclerView.Adapter adapter, final Context context){
        db.collection(DB_CLASSES)
                .whereEqualTo(FS_SCHOOL_NAME, school.getName())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            SchoolActivity.data.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                SchoolClass schoolClass = new SchoolClass(document.getData().get(FS_NAME).toString());
                                SchoolActivity.data.add(schoolClass);
                            }
                            Intent intent = new Intent();
                            intent.setAction(SchoolActivity.ACTION_STRING_CLASSES_LOADED);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            context.sendBroadcast(intent);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        adapter.notifyDataSetChanged();

    }

    public void addClass(SchoolClass schoolClass, final School school, final RecyclerView.Adapter adapter, final Context context){
        //Schule erstellen
        Map<String, String> classMap = new HashMap<>();
        classMap.put(FS_NAME, schoolClass.getName());
        classMap.put(FS_SCHOOL_NAME, school.getName());

        //User in der Datenbank abspeichern
        db.collection(DB_CLASSES)
                .add(classMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + task.getResult().getId());
                            getClassesToPage(school, adapter, context);
                            adapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }
}

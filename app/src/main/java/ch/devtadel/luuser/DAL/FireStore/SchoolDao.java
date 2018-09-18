package ch.devtadel.luuser.DAL.FireStore;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import ch.devtadel.luuser.CalendarActivity;
import ch.devtadel.luuser.CheckActivity;
import ch.devtadel.luuser.CheckListActivity;
import ch.devtadel.luuser.GraphActivity;
import ch.devtadel.luuser.SchoolListActivity;
import ch.devtadel.luuser.NewCheckActivity;
import ch.devtadel.luuser.SchoolActivity;
import ch.devtadel.luuser.helper.AdminHelper;
import ch.devtadel.luuser.model.Check;
import ch.devtadel.luuser.model.School;
import ch.devtadel.luuser.model.SchoolClass;
import ch.devtadel.luuser.model.User;

public class SchoolDao {
    private static final String TAG = "SchoolDao";

    public static final String FS_NAME = "Name";
    public static final String FS_PLACE = "Ort";
    public static final String FS_SCHOOL_NAME = "Schulname";
    public static final String FS_CLASS_NAME = "Klassenname";
    public static final String FS_CNT_STUDENTS = "Anzahl_Kinder";
    public static final String FS_CNT_LOUSE = "Anzahl_Laeuse";
    public static final String FS_DATE = "Datum";
    public static final String FS_NO_LOUSE = "Lausfrei";
    public static final String FS_PRENAME = "Vorname";
    public static final String FS_SURNAME = "Nachname";
    public static final String FS_EMAIL = "Email";
    public static final String FS_UID = "uid";
    public static final String FS_YEAR = "Jahr";
    public static final String FS_ERSTELLER = "Erstellt_durch";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final String DB_SCHOOLS = "Schulen";
    public static final String DB_CLASSES = "Klassen";
    public static final String DB_CHECKS = "Kontrollen";
    public static final String DB_USER = "Benutzer";
    public static final String FS_START_YEAR = "Jahrgang";
    public static final String DB_ADMINS = "Administratoren";
    public static final String[] DB_COLLECTIONS = {DB_SCHOOLS, DB_CLASSES, DB_CHECKS, DB_USER, DB_ADMINS};

    public void loadSchoolList(final RecyclerView.Adapter adapter, final RecyclerView recyclerView){
        db.collection(DB_SCHOOLS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            SchoolListActivity.data.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                School school = new School(document.getData().get(FS_NAME).toString(), document.getData().get(FS_PLACE).toString());
                                SchoolListActivity.data.add(school);
                            }
                            adapter.notifyDataSetChanged();
                            recyclerView.setVisibility(View.VISIBLE);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
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
    public void setupClassSpinner(final Context context, final String schoolName, final int startYear){
        db.collection(DB_CLASSES)
                .whereEqualTo(FS_SCHOOL_NAME, schoolName)
                .whereEqualTo(FS_START_YEAR, startYear)
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

    public void getClassesToPage(School school, final RecyclerView.Adapter adapter, final Context context, final int startYear){
        db.collection(DB_CLASSES)
                .whereEqualTo(FS_SCHOOL_NAME, school.getName())
                .whereEqualTo(FS_START_YEAR, startYear)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            SchoolActivity.class_data.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                SchoolClass schoolClass = new SchoolClass(document.getData().get(FS_NAME).toString(), Integer.valueOf(document.getData().get(FS_START_YEAR).toString()));
                                SchoolActivity.class_data.add(schoolClass);
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
    }

    //Todo: schoolClass beim erstellen mit Jahrgang ausstatten
    public void addClass(SchoolClass schoolClass, final School school, final RecyclerView.Adapter adapter, final Context context){
        //Schule erstellen
        Map<String, Object> classMap = new HashMap<>();
        classMap.put(FS_NAME, schoolClass.getName());
        classMap.put(FS_START_YEAR, schoolClass.getYear());
        classMap.put(FS_SCHOOL_NAME, school.getName());

        //User in der Datenbank abspeichern
        db.collection(DB_CLASSES)
                .add(classMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + task.getResult().getId());
                            getClassesToPage(school, adapter, context, 2018); //TODO: STARTYEAR
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

    public void createCheck(final Check check, final Context context, FirebaseUser user){
        //Schule erstellen
        Map<String, Object> checkMap = new HashMap<>();
        checkMap.put(FS_SCHOOL_NAME, check.getSchoolName());
        checkMap.put(FS_CLASS_NAME, check.getClassName());
        checkMap.put(FS_DATE, check.getDate());
        checkMap.put(FS_CNT_STUDENTS, check.getStudentCount());
        checkMap.put(FS_CNT_LOUSE, check.getLouseCount());
        checkMap.put(FS_NO_LOUSE, check.isNoLouse());
        checkMap.put(FS_ERSTELLER, user.getEmail());
        checkMap.put(FS_START_YEAR, check.getClassStartYear());

        //User in der Datenbank abspeichern
        db.collection(DB_CHECKS)
                .add(checkMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                        Toast.makeText(context, "Kontrolle erfasst", Toast.LENGTH_LONG).show();
                        context.startActivity(new Intent(context, SchoolActivity.class).putExtra(SchoolActivity.SCHOOL_NAME, check.getSchoolName()));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void keySpinnerSchools(final Context context){
        db.collection(DB_SCHOOLS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            CheckListActivity.valueList.add("Schule wählen");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CheckListActivity.valueList.add(document.getData().get(FS_NAME).toString());
                            }
                            context.sendBroadcast(new Intent()
                                    .setAction(CheckListActivity.ACTION_STRING_VALUES_LOADED)
                                    .addCategory(Intent.CATEGORY_DEFAULT));
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void keySpinnerClasses(final Context context){
        db.collection(DB_CLASSES)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            CheckListActivity.valueList.add("Klasse wählen");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CheckListActivity.valueList.add(document.getData().get(FS_NAME).toString());
                            }
                            context.sendBroadcast(new Intent()
                                    .setAction(CheckListActivity.ACTION_STRING_VALUES_LOADED)
                                    .addCategory(Intent.CATEGORY_DEFAULT));
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void keySpinnerDate(final Context context){
        db.collection(DB_CHECKS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            CheckListActivity.valueList.add("Datum wählen");
                            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Date date = (Date)document.getData().get(FS_DATE);
                                String dateStr = sdf.format(date);
                                if(!CheckListActivity.valueList.contains(dateStr)) {
                                    CheckListActivity.valueList.add(dateStr);
                                }
                            }
                            context.sendBroadcast(new Intent()
                                    .setAction(CheckListActivity.ACTION_STRING_VALUES_LOADED)
                                    .addCategory(Intent.CATEGORY_DEFAULT));
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void getChecksToPage(final RecyclerView.Adapter adapter, final Context context, String key, String value){
        Object v = "";
        if(key.equals("Datum")){
            try {
                v = new SimpleDateFormat("dd.MM.yyyy").parse(value);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            v = value;
        }

        db.collection(DB_CHECKS)
                .whereEqualTo(key, v)
                .limit(300)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            CheckListActivity.data.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Check check = new Check();

                                check.setSchoolName(document.getData().get(FS_SCHOOL_NAME).toString());
                                check.setClassName(document.getData().get(FS_CLASS_NAME).toString());
                                check.setStudentCount(Integer.valueOf(document.getData().get(FS_CNT_STUDENTS).toString()));
                                check.setLouseCount(Integer.valueOf(document.getData().get(FS_CNT_LOUSE).toString()));
                                check.setDate((Date)document.getData().get(FS_DATE));
                                check.setDocumentId(document.getId());

                                CheckListActivity.data.add(check);
                            }
                            adapter.notifyDataSetChanged();
                            context.sendBroadcast(new Intent()
                                    .setAction(CheckListActivity.ACTION_STRING_CHECKS_LOADED)
                                    .addCategory(Intent.CATEGORY_DEFAULT));

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void getAllChecksToPage(final RecyclerView.Adapter adapter, final Context context){
        db.collection(DB_CHECKS)
                .limit(300)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            CheckListActivity.data.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Check check = new Check();

                                check.setSchoolName(document.getData().get(FS_SCHOOL_NAME).toString());
                                check.setClassName(document.getData().get(FS_CLASS_NAME).toString());
                                check.setStudentCount(Integer.valueOf(document.getData().get(FS_CNT_STUDENTS).toString()));
                                check.setLouseCount(Integer.valueOf(document.getData().get(FS_CNT_LOUSE).toString()));
                                check.setDate((Date)document.getData().get(FS_DATE));
                                check.setDocumentId(document.getId());

                                CheckListActivity.data.add(check);
                            }
                            adapter.notifyDataSetChanged();
                            context.sendBroadcast(new Intent()
                                    .setAction(CheckListActivity.ACTION_STRING_VALUES_LOADED)
                                    .addCategory(Intent.CATEGORY_DEFAULT));

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void getChecksToSchool(final Context context, String schoolName){
        db.collection(DB_CHECKS)
                .whereEqualTo(FS_SCHOOL_NAME, schoolName)
                .limit(300)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            SchoolActivity.check_data.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Check check = new Check();

                                check.setSchoolName(document.getData().get(FS_SCHOOL_NAME).toString());
                                check.setClassName(document.getData().get(FS_CLASS_NAME).toString());
                                check.setStudentCount(Integer.valueOf(document.getData().get(FS_CNT_STUDENTS).toString()));
                                check.setLouseCount(Integer.valueOf(document.getData().get(FS_CNT_LOUSE).toString()));
                                check.setDate((Date)document.getData().get(FS_DATE));
                                check.setDocumentId(document.getId());

                                SchoolActivity.check_data.add(check);
                            }
                            context.sendBroadcast(new Intent()
                                    .setAction(SchoolActivity.ACTION_STRING_CHECKS_LOADED)
                                    .addCategory(Intent.CATEGORY_DEFAULT));

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void getChecksToCalendar(final Context context, final Date date){
        String year = new SimpleDateFormat("yyyy").format(date);
        final SimpleDateFormat monthFormatter = new SimpleDateFormat("MM");
        final String month = monthFormatter.format(date);
        db.collection(DB_CHECKS)
                .whereEqualTo(FS_YEAR, year)    //Todo: und nur von dem angemeldeten Ort.
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            CalendarActivity.data.clear();

                            Log.d(TAG, "Month param: " + month);

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Date checkDate = (Date)document.getData().get(FS_DATE);
                                String checkMonth = monthFormatter.format(checkDate);
                                if(true) {  //Todo: gleicher monat
                                    Log.d(TAG, "Month check: " + checkMonth);

                                    CalendarActivity.data.add(checkDate);
                                    Log.d(TAG, " "+checkDate.toString());
                                }
                            }
                            context.sendBroadcast(new Intent()
                                    .setAction(CalendarActivity.ACTION_STRING_CALENDARDATA_LOADED)
                                    .addCategory(Intent.CATEGORY_DEFAULT));

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void getLouseDataForGraph(final Context context){
        final Map<Date, String> returnMap = new HashMap<>();

        db.collection(DB_CHECKS)
                .orderBy(FS_DATE, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            GraphActivity.data.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Date date = (Date)document.getData().get(FS_DATE);
                                int value = Integer.valueOf(document.getData().get(FS_CNT_LOUSE).toString());
                                if(returnMap.get(date) != null){
                                    returnMap.replace(date, String.valueOf(Integer.valueOf(returnMap.get(date)) + value));
                                } else {
                                    returnMap.put(date, String.valueOf(value));
                                }
                            }

                            Map<Date, String> finalMap = new TreeMap<>(returnMap);
                            GraphActivity.data.putAll(finalMap);

                            context.sendBroadcast(new Intent()
                                    .setAction(GraphActivity.ACTION_STRING_GRAPH_READY)
                                    .addCategory(Intent.CATEGORY_DEFAULT));
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void getLouseDataForSchoolGraph(final Context context, String schoolname){
        final Map<Date, String> returnMap = new HashMap<>();

        db.collection(DB_CHECKS)
                .whereEqualTo(FS_SCHOOL_NAME, schoolname)
                .orderBy(FS_DATE, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            SchoolActivity.graph_data.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Date date = (Date)document.getData().get(FS_DATE);
                                int value = Integer.valueOf(document.getData().get(FS_CNT_LOUSE).toString());
                                if(returnMap.get(date) != null){
                                    returnMap.replace(date, String.valueOf(Integer.valueOf(returnMap.get(date)) + value));
                                } else {
                                    returnMap.put(date, String.valueOf(value));
                                }
                            }

                            Map<Date, String> finalMap = new TreeMap<>(returnMap);
                            SchoolActivity.graph_data.putAll(finalMap);

                            context.sendBroadcast(new Intent()
                                    .setAction(SchoolActivity.ACTION_STRING_GRAPH_LOADED)
                                    .addCategory(Intent.CATEGORY_DEFAULT));
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void createUserInFS(User user, String uid){
        //User erstellen
        Map<String, String> newUser = new HashMap<>();
        newUser.put(FS_UID, uid);
        newUser.put(FS_SURNAME, user.getSurname());
        newUser.put(FS_PRENAME, user.getPrename());
        newUser.put(FS_PLACE, user.getPlace());
        newUser.put(FS_EMAIL, user.getEmail());

        //User in der Datenbank abspeichern
        db.collection(DB_USER)
                .add(newUser)
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

    public void loadAdmins(final Context context){
        db.collection(DB_ADMINS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            String[] admins = new String[task.getResult().size()];
                            int i = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                admins[i] = document.getData().get(FS_UID).toString();
                                i++;
                            }
                            context.sendBroadcast(new Intent()
                                    .setAction(AdminHelper.ACTION_STRING_ADMINS_LOADED)
                                    .addCategory(Intent.CATEGORY_DEFAULT)
                                    .putExtra(AdminHelper.ADMINS, admins));
                        } else {
                            //Todo: Adminliste konnte nicht geladen werden.
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void getCheckById(String documentId, final Context context){
        db.collection(DB_CHECKS)
                .document(documentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            Check check = new Check();
                            check.setStudentCount(Integer.valueOf(task.getResult().get(FS_CNT_STUDENTS).toString()));
                            check.setLouseCount(Integer.valueOf(task.getResult().get(FS_CNT_LOUSE).toString()));
                            check.setDate((Date)task.getResult().get(FS_DATE));
                            check.setClassName(task.getResult().get(FS_CLASS_NAME).toString());
                            check.setSchoolName(task.getResult().get(FS_SCHOOL_NAME).toString());
                            check.setNoLouse((boolean)task.getResult().get(FS_NO_LOUSE));
                            check.setCheckerMail(task.getResult().get(FS_ERSTELLER).toString());

                            CheckActivity.check = check;
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            CheckActivity.check = null;
                        }
                        context.sendBroadcast(new Intent()
                                .setAction(CheckActivity.ACTION_STRING_CHECK_LOADED)
                                .addCategory(Intent.CATEGORY_DEFAULT));
                    }
                });
    }

    public void getCheckerToCheck(final Context context, final String email){
        db.collection(DB_USER)
                .whereEqualTo(FS_EMAIL, email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String username = "";
                        if (task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                username = document.getData().get(FS_SURNAME).toString();
                                username += " " + document.getData().get(FS_PRENAME).toString();
                            }
                            context.sendBroadcast(new Intent()
                                .setAction(CheckActivity.ACTION_STRING_CHECKER_LOADED)
                                .addCategory(Intent.CATEGORY_DEFAULT)
                                .putExtra(CheckActivity.CHECKER, username));
                        } else {
                            Log.d(TAG, "User nicht gefunden!");
                        }
                    }
                });
    }

    public void newField(final Context context, final String collectionStr, final String fieldNameStr, String datatypeStr, String defaultValue){
        boolean valid = true;

        Object data = " ";

        //Datentyp bestimmen
        switch(datatypeStr){
            case "Boolean":
                if(defaultValue.equals("true")){
                    data = true;
                } else {
                    data = false;
                }
                break;
            case "String":
                data = defaultValue;
                break;
            case "Integer":
                data = Integer.valueOf(defaultValue);
                break;
            default:
                valid = false;
        }
        final Object finalData = data;


        db.collection(collectionStr)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            WriteBatch batch = db.batch();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                batch.update(document.getReference(), fieldNameStr, finalData);
                            }
                            batch.commit()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(context, fieldNameStr + " with the Value " + finalData.toString() + "commited", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }
                });
    }
}

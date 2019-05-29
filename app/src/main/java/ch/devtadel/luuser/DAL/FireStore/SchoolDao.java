package ch.devtadel.luuser.DAL.FireStore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import ch.devtadel.luuser.AddSchoolActivity;
import ch.devtadel.luuser.CalendarActivity;
import ch.devtadel.luuser.CheckActivity;
import ch.devtadel.luuser.CheckListActivity;
import ch.devtadel.luuser.MainActivity;
import ch.devtadel.luuser.SchoolListActivity;
import ch.devtadel.luuser.NewCheckActivity;
import ch.devtadel.luuser.SchoolActivity;
import ch.devtadel.luuser.helper.UserHelper;
import ch.devtadel.luuser.model.Check;
import ch.devtadel.luuser.model.School;
import ch.devtadel.luuser.model.SchoolClass;
import ch.devtadel.luuser.model.User;

public class SchoolDao {
    private static final String TAG = "SchoolDao";

    private static final String FS_NAME = "Name";
    private static final String FS_PLACE = "Ort";
    private static final String FS_CANTON = "Kanton";
    private static final String FS_SCHOOL_NAME = "Schulname";
    private static final String FS_CLASS_NAME = "Klassenname";
    private static final String FS_CNT_STUDENTS = "Anzahl_Kinder";
    private static final String FS_CNT_LOUSE = "Anzahl_Laeuse";
    private static final String FS_CNT_LICE = "Anzahl_Nissen";
    private static final String FS_DATE = "Datum";
    public static final String FS_NO_LOUSE = "Lausfrei";
    public static final String FS_NO_LICE = "Nissfrei";
    private static final String FS_PRENAME = "Vorname";
    private static final String FS_SURNAME = "Nachname";
    private static final String FS_EMAIL = "Email";
    private static final String FS_UID = "uid";
    private static final String FS_YEAR = "Jahr";
    private static final String FS_START_YEAR = "Jahrgang";
    private static final String FS_ERSTELLER = "Erstellt_durch";
    private static final String FS_DESCRIPTION = "Beschreibung";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String DB_SCHOOLS = "Schulen";
    private static final String DB_CLASSES = "Klassen";
    private static final String DB_CHECKS = "Kontrollen";
    private static final String DB_USER = "Benutzer";
    private static final String DB_ADMINS = "Administratoren";
    public static final String DB_BUG = "Fehlermeldungen";
    public static final String DB_SUGGESTION = "Verbesserungsvorschläge";
    public static final String[] DB_COLLECTIONS = {DB_SCHOOLS, DB_CLASSES, DB_CHECKS, DB_USER, DB_ADMINS};

    public void commitMessage(String message, final String DB, String user) {
        final Map<String, Object> messageMap = new HashMap<>();
        messageMap.put(FS_DESCRIPTION, message);
        messageMap.put(FS_ERSTELLER, user);
        messageMap.put(FS_DATE, new Date(System.currentTimeMillis()));

        db.collection(DB).add(messageMap);
    }

    public void loadSchoolList(final RecyclerView.Adapter adapter, final RecyclerView recyclerView, String canton) {
        db.collection(DB_SCHOOLS)
                .whereEqualTo(FS_CANTON, canton)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            SchoolListActivity.data.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                School school = new School(document.getData().get(FS_NAME).toString(), document.getData().get(FS_PLACE).toString(), document.getData().get(FS_CANTON).toString());
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

    public void setupSchoolSpinner(final Context context, String canton) {
        if (canton.equals("")) {
            canton = UserHelper.DEFAULT_CANTON;
        }
        db.collection(DB_SCHOOLS)
                .whereEqualTo(FS_CANTON, canton)
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

    public void setupClassSpinner(final Context context, final String schoolName, final int startYear) {
        db.collection(DB_CLASSES)
                .whereEqualTo(FS_SCHOOL_NAME, schoolName)
                .whereEqualTo(FS_START_YEAR, startYear)
                .orderBy(FS_NAME)
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


    public void createSchoolInFS(School newSchool, final Context context) {
        //Schule erstellen
        final Map<String, String> schoolMap = new HashMap<>();
        schoolMap.put(FS_NAME, newSchool.getName());
        schoolMap.put(FS_PLACE, newSchool.getPlace());
        schoolMap.put(FS_CANTON, newSchool.getCanton());


        db.collection(DB_SCHOOLS)
                .whereEqualTo(FS_NAME, newSchool.getName())
                .whereEqualTo(FS_CANTON, newSchool.getCanton())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() == null || task.getResult().getDocuments().toArray().length <= 0) {
                                //Schule in der Datenbank abspeichern
                                db.collection(DB_SCHOOLS)
                                        .add(schoolMap)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                                Intent intent = new Intent();
                                                intent.setAction(AddSchoolActivity.ACTION_STRING_SCHOOL_CREATED);
                                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                                context.sendBroadcast(intent);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "!=!=!=!=!=!=!=!=! Error adding document: " + e + " !=!=!=!=!=!=!=!=!");
                                            }
                                        });
                            } else {
                                Intent intent = new Intent();
                                intent.setAction(AddSchoolActivity.ACTION_STRING_SCHOOL_CREATE_FAILED);
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                context.sendBroadcast(intent);
                            }

                        } else {
                            Log.d(TAG, "!=!=!=!=!=!=!=!=! Error getting documents !=!=!=!=!=!=!=!=!");
                        }
                    }
                });
    }


    public void getSchoolToPage(final String name, final Context context) {
        db.collection(DB_SCHOOLS)
                .whereEqualTo(FS_NAME, name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                SchoolActivity.school = new School(document.getData().get(FS_NAME).toString(), document.getData().get(FS_PLACE).toString(), document.getData().get(FS_CANTON).toString());
                            }
                            Intent intent = new Intent();
                            intent.setAction(SchoolActivity.ACTION_STRING_SCHOOL_LOADED);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            context.sendBroadcast(intent);

                        } else {
                            Log.d(TAG, "!=!=!=!=!=!=!=!=! Error getting documents: " + task.getException() + " !=!=!=!=!=!=!=!=!");
                        }
                    }
                });
    }

    public void getClassesToPage(School school, final Context context, final int startYear) {
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
                            Log.d(TAG, "!=!=!=!=!=!=!=!=! Error getting documents: " + task.getException() + " !=!=!=!=!=!=!=!=!");
                        }
                    }
                });
    }

    public void addClass(final SchoolClass schoolClass, final School school, final RecyclerView.Adapter adapter, final Context context) {
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
                            getClassesToPage(school, context, schoolClass.getYear());
                            adapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "!=!=!=!=!=!=!=!=! Error adding document: " + e + " !=!=!=!=!=!=!=!=!");
                    }
                });
    }

    public void createCheck(final Check check, final Context context, FirebaseUser user) {
        //Schule erstellen
        Map<String, Object> checkMap = new HashMap<>();
        checkMap.put(FS_SCHOOL_NAME, check.getSchoolName());
        checkMap.put(FS_CLASS_NAME, check.getClassName());
        checkMap.put(FS_DATE, check.getDate());
        checkMap.put(FS_CNT_STUDENTS, check.getStudentCount());
        checkMap.put(FS_CNT_LOUSE, check.getLouseCount());
        checkMap.put(FS_CNT_LICE, check.getLiceCount());
        checkMap.put(FS_NO_LOUSE, check.isNoLouse());
        checkMap.put(FS_NO_LICE, check.isNoLice());
        checkMap.put(FS_ERSTELLER, user.getUid());
        checkMap.put(FS_START_YEAR, check.getClassStartYear());

        //User in der Datenbank abspeichern
        db.collection(DB_CHECKS)
                .add(checkMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                        Toast.makeText(context, "Kontrolle erfasst", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "!=!=!=!=!=!=!=!=! Error adding document: " + e + " !=!=!=!=!=!=!=!=!");
                    }
                });
    }

    public void keySpinnerSchools(final Context context) {
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
                            Log.d(TAG, "!=!=!=!=!=!=!=!=! Error getting documents: " + task.getException() + " !=!=!=!=!=!=!=!=!");
                        }
                    }
                });
    }

    public void keySpinnerClasses(final Context context) {
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
                            Log.d(TAG, "!=!=!=!=!=!=!=!=! Error getting documents: " + task.getException() + " !=!=!=!=!=!=!=!=!");
                        }
                    }
                });
    }

    @SuppressLint("SimpleDateFormat")
    public void keySpinnerDate(final Context context) {
        db.collection(DB_CHECKS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            CheckListActivity.valueList.add("Datum wählen");
                            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Date date = ((Timestamp) document.getData().get(FS_DATE)).toDate();
                                String dateStr = sdf.format(date);
                                if (!CheckListActivity.valueList.contains(dateStr)) {
                                    CheckListActivity.valueList.add(dateStr);
                                }
                            }
                            context.sendBroadcast(new Intent()
                                    .setAction(CheckListActivity.ACTION_STRING_VALUES_LOADED)
                                    .addCategory(Intent.CATEGORY_DEFAULT));
                        } else {
                            Log.d(TAG, "!=!=!=!=!=!=!=!=! Error getting documents: " + task.getException() + " !=!=!=!=!=!=!=!=!");
                        }
                    }
                });
    }


    @SuppressLint("SimpleDateFormat")
    public void getChecksToPage(final RecyclerView.Adapter adapter, final Context context, String key, String value) {
        Object v;
        if (key.equals("Datum")) {
            try {
                v = new SimpleDateFormat("dd.MM.yyyy").parse(value);
            } catch (ParseException e) {
                v = null;
                e.printStackTrace();
            }
        } else if (key.equals(FS_NO_LOUSE)) {
            v = Boolean.parseBoolean(value);
        } else {
            v = value;
        }

        Log.d(TAG, v.getClass().toString() + " " + v.toString());

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
                                check.setLiceCount(Integer.valueOf(document.getData().get(FS_CNT_LICE).toString()));
                                Timestamp ts = (com.google.firebase.Timestamp) document.getData().get(FS_DATE);
                                check.setDate(ts.toDate());
                                check.setDocumentId(document.getId());

                                CheckListActivity.data.add(check);
                            }
                            adapter.notifyDataSetChanged();
                            context.sendBroadcast(new Intent()
                                    .setAction(CheckListActivity.ACTION_STRING_CHECKS_LOADED)
                                    .addCategory(Intent.CATEGORY_DEFAULT));

                        } else {
                            Log.d(TAG, "!=!=!=!=!=!=!=!=! Error getting documents: " + task.getException() + " !=!=!=!=!=!=!=!=!");
                        }
                    }
                });
    }

    public void getAllChecksToPage(final RecyclerView.Adapter adapter, final Context context) {
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
                                check.setLiceCount(Integer.valueOf(document.getData().get(FS_CNT_LICE).toString()));
                                Timestamp ts = (com.google.firebase.Timestamp) document.getData().get(FS_DATE);
                                check.setDate(ts.toDate());
                                check.setDocumentId(document.getId());

                                CheckListActivity.data.add(check);
                            }
                            adapter.notifyDataSetChanged();
                            context.sendBroadcast(new Intent()
                                    .setAction(CheckListActivity.ACTION_STRING_VALUES_LOADED)
                                    .addCategory(Intent.CATEGORY_DEFAULT));

                        } else {
                            Log.d(TAG, "!=!=!=!=!=!=!=!=! Error getting documents: " + task.getException() + " !=!=!=!=!=!=!=!=!");
                        }
                    }
                });
    }

    public void getChecksToSchool(final Context context, String schoolName) {
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
                                check.setLiceCount(Integer.valueOf(document.getData().get(FS_CNT_LICE).toString()));
                                check.setDate(((Timestamp) document.getData().get(FS_DATE)).toDate());
                                check.setDocumentId(document.getId());

                                SchoolActivity.check_data.add(check);
                            }
                            context.sendBroadcast(new Intent()
                                    .setAction(SchoolActivity.ACTION_STRING_CHECKS_LOADED)
                                    .addCategory(Intent.CATEGORY_DEFAULT));

                        } else {
                            Log.d(TAG, "!=!=!=!=!=!=!=!=! Error getting documents: " + task.getException() + " !=!=!=!=!=!=!=!=!");
                        }
                    }
                });
    }

    @SuppressLint("SimpleDateFormat")
    public void getLastCheckToSchool(final Context context, String schoolName) {
        db.collection(DB_CHECKS)
                .whereEqualTo(FS_SCHOOL_NAME, schoolName)
                .orderBy(FS_DATE, Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Date lastDate = null;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                lastDate = ((com.google.firebase.Timestamp) document.getData().get(FS_DATE)).toDate();
                            }
                            SimpleDateFormat sdf = new SimpleDateFormat("EE, dd.MM.yyyy");
                            String lastDateString;
                            if (lastDate != null) {
                                lastDateString = sdf.format(lastDate);
                            } else {
                                lastDateString = "keine";
                            }
                            context.sendBroadcast(new Intent()
                                    .setAction(SchoolActivity.ACTION_STRING_LAST_CHECK_LOADED)  // Todo: WHA!
                                    .addCategory(Intent.CATEGORY_DEFAULT)
                                    .putExtra(SchoolActivity.LAST_CHECK_DATE, lastDateString));
                        } else {
                            Log.d(TAG, "!=!=!=!=!=!=!=!=! Error getting documents: " + task.getException() + " !=!=!=!=!=!=!=!=!");
                        }
                    }
                });
    }

    @SuppressLint("SimpleDateFormat")
    public void getChecksToCalendar(final Context context, final Date date) {
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
                                Date checkDate = ((Timestamp) document.getData().get(FS_DATE)).toDate();
                                String checkMonth = monthFormatter.format(checkDate);
                                if (true) {  //Todo: gleicher monat
                                    Log.d(TAG, "Month check: " + checkMonth);

                                    CalendarActivity.data.add(checkDate);
                                    Log.d(TAG, " " + checkDate.toString());
                                }
                            }
                            context.sendBroadcast(new Intent()
                                    .setAction(CalendarActivity.ACTION_STRING_CALENDARDATA_LOADED)
                                    .addCategory(Intent.CATEGORY_DEFAULT));

                        } else {
                            Log.d(TAG, "!=!=!=!=!=!=!=!=! Error getting documents: " + task.getException() + " !=!=!=!=!=!=!=!=!");
                        }
                    }
                });
    }

    public void getLouseDataForSchoolGraph(final Context context, String schoolname) {
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
                                Date date = ((Timestamp) document.getData().get(FS_DATE)).toDate();
                                int value = Integer.valueOf(document.getData().get(FS_CNT_LOUSE).toString());
                                if (returnMap.get(date) != null) {
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
                            Log.d(TAG, "!=!=!=!=!=!=!=!=! Error getting documents: " + task.getException() + " !=!=!=!=!=!=!=!=!");
                        }
                    }
                });
    }

    public void createUserInFS(User user, String uid) {
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
                        Log.d(TAG, "!=!=!=!=!=!=!=!=! Error adding document: " + e + " !=!=!=!=!=!=!=!=!");
                    }
                });
    }

    public void getCheckById(String documentId, final Context context) {
        db.collection(DB_CHECKS)
                .document(documentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Check check = new Check();
                            check.setStudentCount(Integer.valueOf(Objects.requireNonNull(task.getResult().get(FS_CNT_STUDENTS)).toString()));
                            check.setLouseCount(Integer.valueOf(Objects.requireNonNull(task.getResult().get(FS_CNT_LOUSE)).toString()));
                            check.setDate(((Timestamp) task.getResult().get(FS_DATE)).toDate());
                            check.setClassName(Objects.requireNonNull(task.getResult().get(FS_CLASS_NAME)).toString());
                            check.setSchoolName(Objects.requireNonNull(task.getResult().get(FS_SCHOOL_NAME)).toString());
                            check.setNoLouse((boolean) task.getResult().get(FS_NO_LOUSE));
                            check.setCheckerMail(Objects.requireNonNull(task.getResult().get(FS_ERSTELLER)).toString());

                            CheckActivity.check = check;
                        } else {
                            Log.d(TAG, "!=!=!=!=!=!=!=!=! Error getting documents: " + task.getException() + " !=!=!=!=!=!=!=!=!");
                            CheckActivity.check = null;
                        }
                        context.sendBroadcast(new Intent()
                                .setAction(CheckActivity.ACTION_STRING_CHECK_LOADED)
                                .addCategory(Intent.CATEGORY_DEFAULT));
                    }
                });
    }

    public void getCheckerToCheck(final Context context, final String uid) {
        db.collection(DB_USER)
                .whereEqualTo(FS_UID, uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String username = "";
                        String userUid = "";
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                username = document.getData().get(FS_SURNAME).toString();
                                username += " " + document.getData().get(FS_PRENAME).toString();
                                userUid = document.getData().get(FS_UID).toString();
                            }
                            context.sendBroadcast(new Intent()
                                    .setAction(CheckActivity.ACTION_STRING_CHECKER_LOADED)
                                    .addCategory(Intent.CATEGORY_DEFAULT)
                                    .putExtra(CheckActivity.CHECKER, username)
                                    .putExtra(CheckActivity.CHECKER_UID, userUid));
                        } else {
                            Log.d(TAG, "!=!=!=!=!=!=!=!=! Error getting documents: " + task.getException() + " !=!=!=!=!=!=!=!=!");
                        }
                    }
                });
    }

    public void newField(final Context context, final String collectionStr, final String fieldNameStr, String datatypeStr, String defaultValue) {
        Object data = " ";

        //Datentyp bestimmen
        switch (datatypeStr) {
            case "Boolean":
                data = defaultValue.equals("true");
                break;
            case "String":
                data = defaultValue;
                break;
            case "Integer":
                data = Integer.valueOf(defaultValue);
                break;
            default:
        }
        final Object finalData = data;


        db.collection(collectionStr)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
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

    //================= ADMINISTRATOREN ====================//

    public void getAdminListToCheck(final Context context) {
        db.collection(DB_ADMINS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> UIDs = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UIDs.add(document.getData().get(FS_UID).toString());
                            }
                            CheckActivity.adminList.clear();
                            CheckActivity.adminList.addAll(UIDs);

                            context.sendBroadcast(new Intent()
                                    .setAction(CheckActivity.ACTION_STRING_ADMINLIST_LOADED)
                                    .addCategory(Intent.CATEGORY_DEFAULT));
                        }
                    }
                });
    }

    //======================================================//
}

package ch.devtadel.luuser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.devtadel.luuser.DAL.FireStore.SchoolDao;
import ch.devtadel.luuser.adapter.ChecksListAdapter;
import ch.devtadel.luuser.model.Check;
import ch.devtadel.luuser.model.Report;
import ch.devtadel.luuser.model.SchoolClass;

public class CheckListActivity extends AppCompatActivity {
    private static final String TAG = "CheckListActivity";
    public final static String ACTION_STRING_VALUES_LOADED = "values loaded";
    public final static String ACTION_STRING_CHECKS_LOADED = "values 2 loaded";

    private List<String> keyList = new ArrayList<>();
    public static List<String> valueList = new ArrayList<>();
    public static List<Check> data = new ArrayList<>();

    private Spinner keySP;
    private Spinner valueSP;
    private ProgressBar valueSpinnerPB;
    private RecyclerView.Adapter mainRecyclerAdapter;
    private RecyclerView mainRecyclerView;
    private ProgressBar mainProgressBar;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "VALUES LOADED BROADCAST RECEIVED!");

            if(intent.getAction().equals(ACTION_STRING_VALUES_LOADED)) {
                ArrayAdapter<String> valueSpinnerAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, valueList);
                valueSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown);
                valueSP.setAdapter(valueSpinnerAdapter);

                valueSpinnerPB.setVisibility(View.GONE);
            } else if(intent.getAction().equals(ACTION_STRING_CHECKS_LOADED)){
                mainRecyclerView.setVisibility(View.VISIBLE);
                mainProgressBar.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);

        Button textExcelButton = findViewById(R.id.btn_generate_excell);
        textExcelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(generateExcelFile("Kontrollen_Raport")){
                    Toast.makeText(getBaseContext(), "Raport mit dem Namen \"Kontrollen_Raport.xls\" wurde erstellt!", Toast.LENGTH_LONG).show();
                }
            }
        });

        setTitle("Kontrollen einsehen");

        valueSpinnerPB = findViewById(R.id.progressBar_value_spinner);

        setupKeyList();
        setupSpinner();
        setupRecyclerView();

        //Receiver registrieren
        if(broadcastReceiver != null){
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_STRING_VALUES_LOADED);
            intentFilter.addAction(ACTION_STRING_CHECKS_LOADED);
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Receiver registrieren
        if(broadcastReceiver != null){
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_STRING_VALUES_LOADED);
            intentFilter.addAction(ACTION_STRING_CHECKS_LOADED);
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Receiver registrieren
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    private void setupKeyList(){
        keyList.add("Alle");
        keyList.add("Schule");
        keyList.add("Klasse");
        keyList.add("Datum");
        keyList.add("Läuse gefunden");
        keyList.add("keine Läuse");
    }

    private void setupSpinner(){
        keySP = findViewById(R.id.spinner_key);
        valueSP = findViewById(R.id.spinner_value);

        //KEYS
        ArrayAdapter<String> keySpinnerAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, keyList);
        keySpinnerAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown);
        keySP.setAdapter(keySpinnerAdapter);
        keySP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "KEY SPINNER item selected!!!!");

                SchoolDao dao = new SchoolDao();
                String key = adapterView.getItemAtPosition(i).toString();

                valueList.clear();

                switch(key){
                    case "Schule":
                        valueSpinnerPB.setVisibility(View.VISIBLE);
                        valueSP.setVisibility(View.VISIBLE);

                        //Valuespinner Daten laden.
                        dao.keySpinnerSchools(getBaseContext());
                        break;
                    case "Klasse":
                        valueSpinnerPB.setVisibility(View.VISIBLE);
                        valueSP.setVisibility(View.VISIBLE);

                        //Valuespinner Daten laden.
                        dao.keySpinnerClasses(getBaseContext());
                        break;
                    case "Datum":
                        valueSpinnerPB.setVisibility(View.VISIBLE);
                        valueSP.setVisibility(View.VISIBLE);

                        //Valuespinner Daten laden.
                        dao.keySpinnerDate(getBaseContext());
                        break;
                    case "Läuse gefunden":
                        valueSP.setVisibility(View.GONE);

                        break;
                    case "keine Läuse":
                        valueSP.setVisibility(View.GONE);

                        break;
                    default:
                        dao.getAllChecksToPage(mainRecyclerAdapter, getBaseContext());
                        valueSP.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                valueList.clear();
            }
        });
        //END KEYS

        //VALUES
        valueSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "VALUE SPINNER item selected!!!!");

                mainRecyclerView.setVisibility(View.GONE);
                mainProgressBar.setVisibility(View.VISIBLE);

                String key = keySP.getSelectedItem().toString();
                boolean valid = true;
                String finalKey = "";

                switch (key) {
                    case "Schule":
                        finalKey = "Schulname";
                        break;
                    case "Klasse":
                        finalKey = "Klassenname";
                        break;
                    case "Datum":
                        finalKey = "Datum";
                        break;
                    default:
                        valid = false;
                }

                if (valid) {
                    SchoolDao dao = new SchoolDao();
                    dao.getChecksToPage(mainRecyclerAdapter, getBaseContext(), finalKey, valueSP.getSelectedItem().toString());
                }
                Toast.makeText(getBaseContext(), valueSP.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
                //END VALUES
    }

    /**
     * Prozedur um den RecyclerView für die Userliste vorzubereiten.
     */
    private void setupRecyclerView(){
        mainRecyclerView = findViewById(R.id.rv_checks_list);
        mainProgressBar = findViewById(R.id.progressBar_recyclerview);

        //Performance verbessern. Möglich, da die Listeneinträge alle die selbe grösse haben sollen.
        mainRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mainLayoutManager = new LinearLayoutManager(CheckListActivity.this);
        mainRecyclerView.setLayoutManager(mainLayoutManager);

        mainRecyclerAdapter = new ChecksListAdapter(data);
        mainRecyclerView.setAdapter(mainRecyclerAdapter);
    }

    private boolean generateExcelFile(String fileName){
//        List<Report> reports = generateReports();
        boolean success = false;

        List<Report> reportList = generateReports();

        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e(TAG, "Storage not available or read only");
            return false;
        }

        //Neues Workbook
        Workbook workbook = new HSSFWorkbook();
        Cell cell = null;

        //CELL-STYLES
        CellStyle cs = workbook.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_YELLOW.getIndex());
        cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cs.setBorderBottom(BorderStyle.THIN);
        cs.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        cs.setBorderLeft(BorderStyle.THIN);
        cs.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        cs.setBorderRight(BorderStyle.THIN);
        cs.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        cs.setBorderTop(BorderStyle.THIN);
        cs.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());

        CellStyle csTitle = workbook.createCellStyle();
        csTitle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_ORANGE.getIndex());
        csTitle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        csTitle.setBorderBottom(BorderStyle.THIN);
        csTitle.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        csTitle.setBorderLeft(BorderStyle.THIN);
        csTitle.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        csTitle.setBorderRight(BorderStyle.THIN);
        csTitle.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        csTitle.setBorderTop(BorderStyle.THIN);
        csTitle.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());

        CellStyle csBlue = workbook.createCellStyle();
        csBlue.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_CORNFLOWER_BLUE.getIndex());
        csBlue.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        csBlue.setBorderBottom(BorderStyle.THIN);
        csBlue.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        csBlue.setBorderLeft(BorderStyle.THIN);
        csBlue.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        csBlue.setBorderRight(BorderStyle.THIN);
        csBlue.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        csBlue.setBorderTop(BorderStyle.THIN);
        csBlue.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());

        CellStyle csBlueTitle = workbook.createCellStyle();
        csBlueTitle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.CORNFLOWER_BLUE.getIndex());
        csBlueTitle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        csBlueTitle.setBorderBottom(BorderStyle.THIN);
        csBlueTitle.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        csBlueTitle.setBorderLeft(BorderStyle.THIN);
        csBlueTitle.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        csBlueTitle.setBorderRight(BorderStyle.THIN);
        csBlueTitle.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        csBlueTitle.setBorderTop(BorderStyle.THIN);
        csBlueTitle.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());

        Font titleFont= workbook.createFont();
        titleFont.setFontHeightInPoints((short)12);
        titleFont.setFontName("Arial");
        titleFont.setBold(true);
        titleFont.setItalic(false);

        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex());
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setFont(titleFont);

        CellStyle verticalCS = workbook.createCellStyle();
        verticalCS.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle horizontalCS = workbook.createCellStyle();
        horizontalCS.setAlignment(HorizontalAlignment.CENTER);
        //END CELL_STYLES

        //Neue Seite
        Sheet sheet1 = null;
        sheet1 = workbook.createSheet("CHECK");

        int widthFlag = 0;
        int heightFlag = 0;
        for(Report report : reportList){
            //START REPORT
            widthFlag = 0;  //Wird immer wieder links angefangen bei jedem Report

            Row row = sheet1.createRow(heightFlag);
            sheet1.addMergedRegion(new CellRangeAddress(heightFlag, heightFlag, 0,report.getBiggestCheckCount()*2+2));

            cell = row.createCell(widthFlag);
            cell.setCellValue(report.getName());
            cell.setCellStyle(titleStyle);

            heightFlag++;
            for(SchoolClass schoolClass : report.getClasses()){
                //START KLASSE

                //Titel werden erstellt
                row = sheet1.createRow(heightFlag);
                int titleWidthFlag = 1; //Das erste Datum
                cell = row.createCell(titleWidthFlag);
                cell.setCellStyle(csTitle);
                for(Check check : schoolClass.getChecks()) {
                    cell = row.createCell(titleWidthFlag + 1);
                    cell.setCellValue(check.getDateString());
                    cell.setCellStyle(csBlueTitle);

                    cell = row.createCell(titleWidthFlag + 2);
                    cell.setCellValue("Nachkontrolle");
                    cell.setCellStyle(csTitle);

                    titleWidthFlag += 2;
                }

                widthFlag = 0;  //Wird immer wieder links angefangen bei jeder Klasse
                sheet1.addMergedRegion(new CellRangeAddress(heightFlag,heightFlag+2, 0, 0));

                //Klassenname
                cell = row.createCell(widthFlag);
                cell.setCellValue(schoolClass.getName());
                cell.setCellStyle(verticalCS);
                //
                widthFlag++;

                heightFlag++;

                Row dataRow1 = sheet1.createRow(heightFlag);
                Row dataRow2 = sheet1.createRow(heightFlag+1);


                //Positiv
                cell = dataRow1.createCell(widthFlag);
                cell.setCellValue("+");
                cell.setCellStyle(horizontalCS);
                //
                //Negativ
                cell = dataRow2.createCell(widthFlag);
                cell.setCellValue("-");
                cell.setCellStyle(horizontalCS);
                //
                widthFlag++;    //Rechts vom Klassennamen
                for(Check check : schoolClass.getChecks()){
                    //START CHECK
                    cell = dataRow1.createCell(widthFlag);
                    cell.setCellValue(check.getLouseCount());
                    cell.setCellStyle(csBlue);

                    cell = dataRow2.createCell(widthFlag);
                    cell.setCellValue(check.getStudentCount()-check.getLouseCount());
                    cell.setCellStyle(csBlue);

                    widthFlag++;

                    cell = dataRow1.createCell(widthFlag);
                    cell.setCellStyle(cs);

                    cell = dataRow2.createCell(widthFlag);
                    cell.setCellStyle(cs);

                    widthFlag++;
                    //END CHECK
                }
                heightFlag+=2;
                //END KLASSE
            }
            heightFlag+=2;
            //END REPORT
        }
        //Spaltengrössen
        int longestReportSize = 0;
        for(int i = 0; i < reportList.size(); i++){
            if(reportList.get(i).getBiggestCheckCount() > longestReportSize){
                longestReportSize = reportList.get(i).getBiggestCheckCount();
            }
        }
        sheet1.setColumnWidth(0, 15*230);
        sheet1.setColumnWidth(1, 15*50);
        for(int i = 0; i < longestReportSize+2; i+=2){
            sheet1.setColumnWidth(i+2, 15*220);
            sheet1.setColumnWidth(i+3, 15*220);
        }


        // Create a path where we will place our List of objects on external storage
        File file = new File(getBaseContext().getExternalFilesDir(null), fileName+".xls");
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            workbook.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }
       return success;
    }

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private List<Report> generateReports() {
        List<Report> reports = new ArrayList<>();
        List<String> schoolNames = new ArrayList<>();

        //Schulnamenliste vorbereiten
        for(Check check : data){
            if(!schoolNames.contains(check.getSchoolName())){
                schoolNames.add(check.getSchoolName());
            }
        }
        //DatenListe erstellen. nur checks mit dem Schulnamen
        for(int i = 0; i < schoolNames.size(); i++){
            //Für jede Schule wird ein Report erstellt.
            Report report = new Report(schoolNames.get(i));
            List<Check> repChecks = new ArrayList<>();
            for(Check check : data){
                if(check.getSchoolName().equals(report.getName())){
                    repChecks.add(check);
                }
            }
            report.getClasses().addAll(createReportClasses(repChecks));
            reports.add(report);

            Log.d(TAG, "=================================");
            Log.d(TAG, "Report für: " + report.getName());
            Log.d(TAG, "Anzahl Klassen: " + report.getClasses().size());
            Log.d(TAG, "Anzahl Checks: " + report.getCheckCount());
            Log.d(TAG, "=================================");
        }

        return reports;
    }

    public List<SchoolClass> createReportClasses(List<Check> checks){
        List<SchoolClass> classes = new ArrayList<>();
        List<String> classNames = new ArrayList<>();

        //Klassennamenliste vorbereiten
        for (Check check : checks){

            //Wenn Klassenname nicht in der Liste ist, hinzufügen;
            if(!classNames.contains(check.getClassName())){
                classNames.add(check.getClassName());
            }
        }
        //Klassen-Objekte erstellen
        for(int i = 0; i < classNames.size(); i++){
            SchoolClass newClass = new SchoolClass(classNames.get(i));
            classes.add(newClass);
        }
        //Checks den Klassen verteilen
        for(Check check : checks){
            for(int i = 0; i < classes.size(); i++){
                if(check.getClassName().equals(classes.get(i).getName())){
                    classes.get(i).addCheck(check);
                }
            }
        }

        return classes;
    }

}

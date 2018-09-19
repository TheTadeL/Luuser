package ch.devtadel.luuser.helper;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

import javax.annotation.Nullable;

import ch.devtadel.luuser.R;

public abstract class DateHelper {
    /**
     * Methode die bei einem TextChangedListener verwendet wird. Wenn ein Jahr beim EditText StartYear eingegeben wird, wird die Zahl validiert: es wird gecheckt ob es ein valides Jahr ist.
     * @return Wenn das Jahr valid ist => true
     */
    public static boolean startYearToFinal(CharSequence charSequence, Context context, EditText startYearET, @Nullable TextView finalStartYearTV ) {
        boolean valid = true;

        if (!charSequence.equals("") && charSequence.length() == 4) {
            int startYear = Integer.valueOf(startYearET.getText().toString());
            if (startYear - 2000 > 0 && startYear-2000 <= 2999) {
                if(finalStartYearTV != null)
                    finalStartYearTV.setText(DateHelper.getShortYearString(startYear));
                startYearET.setTextColor(context.getResources().getColor(R.color.colorVerified, null));
            } else {
                startYearET.setTextColor(context.getResources().getColor(R.color.colorNotVerified, null));
                if(finalStartYearTV != null)
                    finalStartYearTV.setText(" ");
                valid = false;
            }
        } else {
            startYearET.setTextColor(context.getResources().getColor(R.color.colorNotVerified, null));
            if(finalStartYearTV != null)
                finalStartYearTV.setText(" ");
            valid = false;
        }
        return valid;
    }

    public static boolean validYear(EditText editText){
        boolean valid = false;
        if (!editText.getText().toString().equals("") && editText.getText().length() == 4) {
            int startYear = Integer.valueOf(editText.getText().toString());
            if (startYear - 2000 > 0 && startYear - 2000 <= 2999) {
                valid = true;
            }
        }
        return valid;
    }

    public static int getSchoolYear(){
        Calendar cal = Calendar.getInstance();
        int schoolYear;

        if(cal.get(Calendar.MONTH) > Calendar.AUGUST){
            schoolYear = cal.get(Calendar.YEAR);
        } else {
            schoolYear = cal.get(Calendar.YEAR)-1;
        }

        return schoolYear;
    }

    public static String getShortYearString(int year){
        return "( " + (year-2000) + " / " + (year-1999) + " )";
    }

    public static String getLongYearString(int year){
        return "( " + year + " / " + (year+1) + " )";
    }
}

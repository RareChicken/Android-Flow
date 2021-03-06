package kr.hs.dgsw.flow.view.out.model;

import android.annotation.SuppressLint;
import android.content.Context;

import java.text.ParseException;
import java.util.Calendar;

import kr.hs.dgsw.flow.data.model.EditData;
import kr.hs.dgsw.flow.data.realm.login.LoginHelper;
import kr.hs.dgsw.flow.data.realm.out.OutHelper;
import kr.hs.dgsw.flow.data.realm.user.model.User;
import kr.hs.dgsw.flow.util.FlowUtils;
import kr.hs.dgsw.flow.view.out.model.Enum.OutType;
import kr.hs.dgsw.flow.util.retrofit.model.out.ResponseOut;

public class OutData {
    private LoginHelper mLoginHelper;
    private OutHelper mOutHelper;

    private OutType outType;

    private int outYear;
    private int outMonth;
    private int outDay;
    private int outHour;
    private int outMinute;

    private int inYear;
    private int inMonth;
    private int inDay;
    private int inHour;
    private int inMinute;

    private EditData reason;

    public OutData(Context context, OutType outType,
                   int outYear, int outMonth, int outDay, int outHour, int outMinute,
                   int inYear, int inMonth, int inDay, int inHour, int inMinute) {
        mLoginHelper = new LoginHelper(context);
        mOutHelper = new OutHelper(context);

        this.outType = outType;
        this.outYear = outYear;
        this.outMonth = outMonth;
        this.outDay = outDay;
        this.outHour = outHour;
        this.outMinute = outMinute;
        this.inYear = inYear;
        this.inMonth = inMonth;
        this.inDay = inDay;
        this.inHour = inHour;
        this.inMinute = inMinute;

        this.reason = new EditData();
    }

    public OutType getOutType() {
        return outType;
    }

    public void setOutType(OutType outType) {
        this.outType = outType;
    }

    public void setOutDate(int year, int month, int day) {
        this.outYear = year;
        this.outMonth = month;
        this.outDay = day;
    }

    public void setOutTime(int hour, int minute) {
        this.outHour = hour;
        this.outMinute = minute;
    }

    public void setInDate(int year, int month, int day) {
        this.inYear = year;
        this.inMonth = month;
        this.inDay = day;
    }

    public void setInTime(int hour, int minute) {
        this.inHour = hour;
        this.inMinute = minute;
    }

    public int getOutYear() {
        return outYear;
    }

    public int getOutMonth() {
        return outMonth;
    }

    public int getOutDay() {
        return outDay;
    }

    public int getOutHour() {
        return outHour;
    }

    public int getOutMinute() {
        return outMinute;
    }

    public int getInYear() {
        return inYear;
    }

    public int getInMonth() {
        return inMonth;
    }

    public int getInDay() {
        return inDay;
    }

    public int getInHour() {
        return inHour;
    }

    public int getInMinute() {
        return inMinute;
    }

    /**
     * 시작 시간을 문자열로 변환하여 반환함
     * @return 시작 시간 문자열
     */
    @SuppressLint("DefaultLocale")
    public String getOutDateTimeToString() {
        return String.format("%04d-%02d-%02d %02d:%02d",
                outYear, outMonth + 1, outDay, outHour, outMinute);
    }

    /**
     * 복귀 시간을 문자열로 변환하여 반환함
     * @return 복귀 시간 문자열
     */
    @SuppressLint("DefaultLocale")
    public String getInDateTimeToString() {
        return String.format("%04d-%02d-%02d %02d:%02d",
                inYear, inMonth + 1, inDay, inHour, inMinute);
    }

    public EditData getReason() {
        return reason;
    }

    public boolean isDateTimeValid() {
        Calendar outCal = Calendar.getInstance();
        outCal.set(outYear, outMonth, outDay, outHour, outMinute, 0);

        Calendar inCal = Calendar.getInstance();
        inCal.set(inYear, inMonth, inDay, inHour, inMinute, 0);

        if (outCal.after(inCal)) {
            return false;
        }

        switch (outType) {
            case SHORT:
                return outYear == inYear &&
                        outMonth == inMonth &&
                        outDay == inDay;
            case LONG:
                return outYear != inYear ||
                        outMonth != inMonth ||
                        outDay != inDay;
        }

        return false;
    }

    public boolean isLoggedIn() {
        return mLoginHelper.isLoggedIn();
    }

    public String getToken() {
        return mLoginHelper.getLoggedUser().getToken();
    }

    public void insertOut(ResponseOut responseOut, OutType outType) throws ParseException {
        User user = mLoginHelper.getLoggedUser();

        if (user != null) {
            mOutHelper.insertOut(
                    user,
                    responseOut.getIdx(),
                    outType == OutType.SHORT ? 0 : 1,
                    FlowUtils.dateFormat(responseOut.getStartTime()),
                    FlowUtils.dateFormat(responseOut.getEndTime()),
                    responseOut.getReason()
            );
        }
    }


}

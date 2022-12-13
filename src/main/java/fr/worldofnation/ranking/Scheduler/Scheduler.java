package fr.worldofnation.ranking.Scheduler;

import fr.worldofnation.ranking.Ranking;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Scheduler {

    private static Ranking main = Ranking.getInstance();

    public static void schedule() throws SQLException {
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        cal.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(cal.getTime());
        String[] times = time.split(":");
        int day = date.getDay();
        int hour = Integer.parseInt(times[0]);
        int minute = Integer.parseInt(times[1]);
        if(day == 6 && hour == 23 && minute == 59)
        {
            main.getRm().calculatePoint();
        }


    }
}

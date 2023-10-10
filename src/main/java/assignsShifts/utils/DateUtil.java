package assignsShifts.utils;

import java.time.Duration;
import java.util.Calendar;

public class DateUtil {

  public static boolean isCalendarEquals(Calendar calendar1, Calendar calendar2) {
    return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
        && calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
        && calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);
  }

  public static boolean isDateInRange(Calendar start, Calendar end, Calendar dateToCheck) {
    return isDateInRange(start, end, dateToCheck, dateToCheck);
  }

  public static boolean isDateInRange(Calendar start, Duration duration, Calendar dateToCheck) {
    Calendar end = Calendar.getInstance();
    end.setTimeInMillis(start.toInstant().plus(duration).toEpochMilli());

    return isDateInRange(start, end, dateToCheck, dateToCheck);
  }

  public static boolean isDateInRange(
      Calendar start, Duration duration, Calendar startDateToCheck, Calendar endDateToCheck) {
    Calendar end = Calendar.getInstance();
    end.setTimeInMillis(start.toInstant().plus(duration).toEpochMilli());

    return isDateInRange(start, end, startDateToCheck, endDateToCheck);
  }

  public static boolean isDateInRange(
      Calendar start, Calendar end, Calendar startDateToCheck, Calendar endDateToCheck) {
    return !(start.after(endDateToCheck) || end.before(startDateToCheck));
  }

  public static boolean isWeekend(Calendar date) {
    int weekday = date.get(Calendar.DAY_OF_WEEK);

    return weekday == Calendar.FRIDAY || weekday == Calendar.SATURDAY;
  }
}

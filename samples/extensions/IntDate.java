
import java.util.Date;
import java.util.Calendar;

public class IntDate
{
  public static Date getDate(String year, String month, String day)
    {
      // Date(int, int, int) has been deprecated, so use Calendar to
      // set the year, month, and day.
      Calendar c = Calendar.getInstance();
      // Convert each argument to int.
      c.set(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day));
      return c.getTime();
    }
}


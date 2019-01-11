package org.b3log.symphony.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.symphony.service.ArticleQueryService;

public class DateUtil {
	private static final Logger LOGGER = Logger.getLogger(DateUtil.class);
	
	public static Map<String, String> getFirstAndLastOfWeek(String dataStr) throws ParseException {
		
		
        Calendar cal = Calendar.getInstance();

        cal.setTime(new SimpleDateFormat("yyyyMMdd").parse(dataStr));

        int d = 0;
        if (cal.get(Calendar.DAY_OF_WEEK) == 1) {
            d = -6;
        } else {
            d = 2 - cal.get(Calendar.DAY_OF_WEEK);
        }
        cal.add(Calendar.DAY_OF_WEEK, d);
        // 所在周开始日期
        String data1 = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        // 所在周结束日期
        String data2 = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
        
        Map<String, String> dates = new HashMap<>();
        dates.put("fromdate", data1);
        dates.put("todate", data2);
        return dates;

    }
	
	public static Map<String, String> getFirstAndLastOfLastMonth(String date) {  
        Map<String, String> map = new HashMap<String, String>();  
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");  
        Calendar calendar = Calendar.getInstance();  
        try {
			calendar.setTime(df.parse(date));
		} catch (ParseException e) {
			LOGGER.log(Level.ERROR, "date parse failed", e);
		}  
        calendar.add(Calendar.MONTH, -1);  
        Date theDate = calendar.getTime();  
        // 本月第一天  
        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();  
        gcLast.setTime(theDate);  
        gcLast.set(Calendar.DAY_OF_MONTH, 1);  
        String day_first = df.format(gcLast.getTime());  
        // 上个月最后一天  
        calendar.add(Calendar.MONTH, 1); // 加一个月  
        calendar.set(Calendar.DATE, 1); // 设置为该月第一天  
        calendar.add(Calendar.DATE, -1); // 再减一天即为上个月最后一天  
        String day_last = df.format(calendar.getTime());  
        map.put("fromdate", day_first);  
        map.put("todate", day_last);  
        return map;  
    } 
	
	public static Map<String, String> getFirstAndLastOfMonth(String date) {  
        Map<String, String> map = new HashMap<String, String>();  
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");  
        Calendar calendar = Calendar.getInstance();  
        try {
			calendar.setTime(df.parse(date));
		} catch (ParseException e) {
			LOGGER.log(Level.ERROR, "date parse failed", e);
		}  
        
        Date theDate = calendar.getTime();  
        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();  
        gcLast.setTime(theDate);  
        gcLast.set(Calendar.DAY_OF_MONTH, 1); 
        
        //本月第一天
        String day_first = df.format(gcLast.getTime());  
        
        calendar.add(Calendar.MONTH, 1); // 加一个月  
        calendar.set(Calendar.DATE, 1); // 设置为下一个月第一天  
        calendar.add(Calendar.DATE, -1); // 再减一天即为本月最后一天  
        String day_last = df.format(calendar.getTime());  
        map.put("fromdate", day_first);  
        map.put("todate", day_last);  
        return map;  
    } 
	
	/***
	 * get dates by livenessType.
	 * @param livenessType  1:month  0:week.
	 * @return
	 */
	public static Map<String, String> getDates(int livenessType) {
		final String date = DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMdd");
		if (livenessType == 1) {
			return org.b3log.symphony.util.DateUtil.getFirstAndLastOfMonth(date);
		} else {
			try {
				return org.b3log.symphony.util.DateUtil.getFirstAndLastOfWeek(date);
			} catch (ParseException e) {
				LOGGER.log(Level.ERROR, "date parse failed", e);
			}
		}
		return null;
	}
	
}

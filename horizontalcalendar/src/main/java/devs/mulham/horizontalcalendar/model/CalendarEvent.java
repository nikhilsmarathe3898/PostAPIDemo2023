package devs.mulham.horizontalcalendar.model;

/**
 * @author Mulham-Raee
 * @since v1.3.2
 */
public class CalendarEvent {

    private int color;
    private Object data;
    private long dateInMillis;

    String desc;

    /*public CalendarEvent(int color){
        this.color = color;
    }*/

    public CalendarEvent(int color,long dateInMillis)
    {
        this.color = color;
        this.dateInMillis = dateInMillis;
    }
    public CalendarEvent(int color, long dateInMillis,Object data) {
        this.color = color;
        this.data = data;
        this.dateInMillis = dateInMillis;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }


    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public long getDateInMillis() {
        return dateInMillis;
    }

    public void setDateInMillis(long dateInMillis) {
        this.dateInMillis = dateInMillis;
    }
}

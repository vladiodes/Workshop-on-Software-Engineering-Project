package main.Publisher;

public class StatNotificationPush extends Notification{
    private String content;

    public StatNotificationPush(int guests,int nonStaff,int managers,int owners,int admins,int loggedIn,int purchases,int registered){
        content=String.format("Guests connected: %d\n" +
                                "Non staff members logged in: %d\n" +
                                "Managers logged in: %d\n" +
                                "Owners logged in: %d\n" +
                                "Admins logged in: %d\n" +
                                "Logged in on that day: %d\n" +
                                "Purchases: %d\n" +
                                "Number of registrations: %d\n",guests,nonStaff,managers,owners,admins,loggedIn,purchases,registered);
    }
    @Override
    public String print() {
        String[] stats = content.split("\n");
        StringBuilder builder=new StringBuilder();
        for (String stat : stats) {
            builder.append("<p>").append(stat).append("</p>");
        }
        return builder.toString();
    }
}

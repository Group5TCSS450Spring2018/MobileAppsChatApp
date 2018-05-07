package spr018.tcss450.clientapplication.utility;

/**
 * Created by Tenma Rollins on 20/04/2018.
 */
public enum Pages {
    HOME("Home"),
    CONNECTIONS("Connections"),
    WEATHER("Weather"),
    SETTINGS("Settings"),
    LOGIN("Login"),
    REGISTER("Register"),
    NEWMESSAGE("Open new chat"),
    NEWCONNECTION("Add new connection");

    private String title;
    Pages(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}

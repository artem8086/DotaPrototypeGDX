package art.soft.gui;

/**
 *
 * @author Artem
 */
public class Locale {

    public String locale;
    public String path;
    public String[] names;

    public String getName(int n){
        return n>=names.length ? names[0] : names[n];
    }
}

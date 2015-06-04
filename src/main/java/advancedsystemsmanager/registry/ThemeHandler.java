package advancedsystemsmanager.registry;

import advancedsystemsmanager.gui.theme.HexValue;
import advancedsystemsmanager.gui.theme.Theme;
import advancedsystemsmanager.gui.theme.ThemeAdapters;
import advancedsystemsmanager.gui.theme.ThemeCommand;
import advancedsystemsmanager.helpers.FileHelper;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ThemeHandler
{
    private static final Pattern JSON = Pattern.compile(".*\\.json", Pattern.CASE_INSENSITIVE);
    public static final Gson GSON = getGson();
    private final String backupLocation;
    private final File themeDir;
    private File themeFile;
    public static Theme theme = new Theme();

    public ThemeHandler(File directory, String backupLocation)
    {
        this.themeDir = directory;
        this.backupLocation = backupLocation;
    }

    public boolean setTheme(String name)
    {
        if (!name.endsWith(".json")) name += ".json";
        File theme = new File(themeDir.getAbsoluteFile() + File.separator + name);
        if (!theme.isFile())
        {
            if (FileHelper.doesFileExistInJar(getClass(), backupLocation + name))
            {
                FileHelper.copyFromJar(getClass(), backupLocation + name, themeDir.getAbsolutePath() + File.separator + name);
            } else
            {
                return false;
            }
            if (!theme.isFile()) return false;
        }
        this.themeFile = theme;
        loadTheme();
        return true;
    }

    public List<String> getThemes()
    {
        File[] files = themeDir.listFiles(new FileFilter()
        {
            @Override
            public boolean accept(File pathname)
            {
                return JSON.matcher(pathname.getName()).find();
            }
        });
        List<String> result = new ArrayList<String>();
        if (files != null)
        {
            for (File file : files)
            {
                result.add(file.getName());
            }
        }
        return result;
    }

    public JsonObject getThemeObject()
    {
        try
        {
            InputStream stream = new FileInputStream(themeFile);
            JsonReader jReader = new JsonReader(new InputStreamReader(stream));
            JsonParser parser = new JsonParser();
            return parser.parse(jReader).getAsJsonObject();
        } catch (FileNotFoundException ignored)
        {
            return null;
        }
    }

    public JsonObject toJson(Theme theme)
    {
        String test = getGson().toJson(theme);
        Theme back = getGson().fromJson(test, Theme.class);

        return null;
    }

    public static Gson getGson()
    {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(HexValue.class, ThemeAdapters.HEX_ADAPTER);
        builder.registerTypeAdapter(ThemeCommand.CommandSet.class, ThemeAdapters.COMMAND_ADAPTER);
        builder.setPrettyPrinting();
        builder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE_WITH_SPACES);
        return builder.create();
    }



    public void loadTheme()
    {

    }
}
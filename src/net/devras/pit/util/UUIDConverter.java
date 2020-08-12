package net.devras.pit.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class UUIDConverter
{
    private final static Gson GSON = new Gson();

    /**
     * Gets the current name of the player from the Mojang servers.
     * Only works for Mojang-UUIDs, not for Bukkit-Offline-UUIDs.
     *
     * @param uuid The UUID of the player.
     * @return The name of the player.
     */
    public static String getNameFromUUID(UUID uuid)
    {
        return getNameFromUUID(uuid.toString());
    }

    /**
     * Gets the current name of the player from the Mojang servers.
     * Only works for Mojang-UUIDs, not for Bukkit-Offline-UUIDs.
     *
     * @param uuid The UUID of the player.
     * @return The name of the player.
     */
    public static String getNameFromUUID(String uuid)
    {
        NameChange[] names = getNamesFromUUID(uuid);
        return names[names.length - 1].name;
    }

    /**
     * A helper class to store the name changes and dates
     */
    public class NameChange
    {
        /**
         * The name to witch the name was changed
         */
        public String name;

        /**
         * Datetime of the name change in UNIX time (without milliseconds)
         */
        public long changedToAt;

        /**
         * Gets the date of a name change
         *
         * @return Date of the name change
         */
        public Date getChangeDate()
        {
            return new Date(changedToAt);
        }
    }

    /**
     * Gets the name history of a player from the Mojang servers.
     * Only works for Mojang-UUIDs, not for Bukkit-Offline-UUIDs.
     *
     * @param uuid The UUID of the player.
     * @return The names and name change dates of the player.
     */
    public static NameChange[] getNamesFromUUID(UUID uuid)
    {
        return getNamesFromUUID(uuid.toString());
    }

    /**
     * Gets the name history of a player from the Mojang servers.
     * Only works for Mojang-UUIDs, not for Bukkit-Offline-UUIDs.
     *
     * @param uuid The UUID of the player.
     * @return The names and name change dates of the player.
     */
    public static NameChange[] getNamesFromUUID(String uuid)
    {
        NameChange[] names = null;
        try
        {
            Scanner jsonScanner = new Scanner((new URL("https://api.mojang.com/user/profiles/" + uuid.replaceAll("-", "") + "/names")).openConnection().getInputStream(), "UTF-8");
            names = GSON.fromJson(jsonScanner.next(), NameChange[].class);
            jsonScanner.close();
        }
        catch(MalformedURLException e) // There is something going wrong!
        {
            System.out.print("\nFailed to get uuid cause of a malformed url!\n UUID: \"" + uuid + "\"\n");
            e.printStackTrace();
        }
        catch(IOException e)
        {
            System.out.print("Looks like there is a problem with the connection with mojang. Please retry later.\n");
            if(e.getMessage().contains("HTTP response code: 429")) //TODO: more reliable detection
            {
                System.out.print("You have reached the request limit of the mojang api! Please retry later!\n");
            }
            e.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return names;
    }

    /**
     * @param name       The name of the player you want the uuid from.
     * @param onlineMode True the UUID should be an online mode UUID (from Mojang). False if it should be an offline mode UUID (from Bukkit).
     * @return The requested UUID.
     */
    public static String getUUIDFromName(String name, boolean onlineMode)
    {
        return getUUIDFromName(name, onlineMode, false, false, null);
    }

    /**
     * @param name          The name of the player you want the uuid from.
     * @param onlineMode    True the UUID should be an online mode UUID (from Mojang). False if it should be an offline mode UUID (from Bukkit).
     * @param lastKnownDate The last time you know that the player had this name.
     * @return The requested UUID.
     */
    public static String getUUIDFromName(String name, boolean onlineMode, Date lastKnownDate)
    {
        return getUUIDFromName(name, onlineMode, false, false, lastKnownDate);
    }

    /**
     * @param name           The name of the player you want the uuid from.
     * @param onlineMode     True the UUID should be an online mode UUID (from Mojang). False if it should be an offline mode UUID (from Bukkit).
     * @param withSeparators True will return the UUID with '-' separators (format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx).
     *                       False will return the UUID without the '-' separator (format: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx).
     * @return The requested UUID.
     */
    public static String getUUIDFromName(String name, boolean onlineMode, boolean withSeparators)
    {
        return getUUIDFromName(name, onlineMode, withSeparators, false, null);
    }

    /**
     * @param name           The name of the player you want the uuid from.
     * @param onlineMode     True the UUID should be an online mode UUID (from Mojang). False if it should be an offline mode UUID (from Bukkit).
     * @param withSeparators True will return the UUID with '-' separators (format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx).
     *                       False will return the UUID without the '-' separator (format: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx).
     * @param lastKnownDate  The last time you know that the player had this name.
     * @return The requested UUID.
     */
    public static String getUUIDFromName(String name, boolean onlineMode, boolean withSeparators, Date lastKnownDate)
    {
        return getUUIDFromName(name, onlineMode, withSeparators, false, lastKnownDate);
    }

    /**
     * @param name              The name of the player you want the uuid from.
     * @param onlineMode        True the UUID should be an online mode UUID (from Mojang). False if it should be an offline mode UUID (from Bukkit).
     * @param withSeparators    True will return the UUID with '-' separators (format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx).
     *                          False will return the UUID without the '-' separator (format: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx).
     * @param offlineUUIDonFail True if an offline UUID should be returned if the Mojang server can't resolve the name.
     *                          False if null should be returned if the Mojang server doesn't return an UUID.
     * @return The requested UUID.
     */
    public static String getUUIDFromName(String name, boolean onlineMode, boolean withSeparators, boolean offlineUUIDonFail)
    {
        return getUUIDFromName(name, onlineMode, withSeparators, offlineUUIDonFail, null);
    }

    /**
     * @param name              The name of the player you want the uuid from.
     * @param onlineMode        True the UUID should be an online mode UUID (from Mojang). False if it should be an offline mode UUID (from Bukkit).
     * @param withSeparators    True will return the UUID with '-' separators (format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx).
     *                          False will return the UUID without the '-' separator (format: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx).
     * @param offlineUUIDonFail True if an offline UUID should be returned if the Mojang server can't resolve the name.
     *                          False if null should be returned if the Mojang server doesn't return an UUID.
     * @param lastKnownDate     The last time you know that the player had this name.
     * @return The requested UUID.
     */
    public static String getUUIDFromName(String name, boolean onlineMode, boolean withSeparators, boolean offlineUUIDonFail, Date lastKnownDate)
    {
        String uuid;
        if(onlineMode)
        {
            uuid = getOnlineUUID(name, lastKnownDate);
            if(uuid == null)
            {
                if(offlineUUIDonFail)
                {
                    System.out.println("Using offline uuid for '" + name + "'. + \n");
                    uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8)).toString();
                }
                else
                {
                    return null;
                }
            }
        }
        else
        {
            uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8)).toString();
        }
        // Fixing the separators depending on setting.
        if(withSeparators)
        {
            if(!uuid.contains("-"))
            {
                uuid = uuid.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
            }
        }
        else
        {
            if(uuid.contains("-"))
            {
                uuid = uuid.replaceAll("-", "");
            }
        }
        return uuid;
    }

    /**
     * @param name       The name of the player you want the uuid from.
     * @param onlineMode True the UUID should be an online mode UUID (from Mojang). False if it should be an offline mode UUID (from Bukkit).
     * @return The requested UUID.
     */
    public static UUID getUUIDFromNameAsUUID(String name, boolean onlineMode)
    {
        return getUUIDFromNameAsUUID(name, onlineMode, false, null);
    }

    /**
     * @param name          The name of the player you want the uuid from.
     * @param onlineMode    True the UUID should be an online mode UUID (from Mojang). False if it should be an offline mode UUID (from Bukkit).
     * @param lastKnownDate The last time you know that the player had this name.
     * @return The requested UUID.
     */
    public static UUID getUUIDFromNameAsUUID(String name, boolean onlineMode, Date lastKnownDate)
    {
        return getUUIDFromNameAsUUID(name, onlineMode, false, lastKnownDate);
    }

    /**
     * @param name              The name of the player you want the uuid from.
     * @param onlineMode        True the UUID should be an online mode UUID (from Mojang). False if it should be an offline mode UUID (from Bukkit).
     * @param offlineUUIDonFail True if an offline UUID should be returned if the Mojang server can't resolve the name.
     *                          False if null should be returned if the Mojang server doesn't return an UUID.
     * @return The requested UUID.
     */
    public static UUID getUUIDFromNameAsUUID(String name, boolean onlineMode, boolean offlineUUIDonFail)
    {
        return getUUIDFromNameAsUUID(name, onlineMode, offlineUUIDonFail, null);
    }

    /**
     * @param name              The name of the player you want the uuid from.
     * @param onlineMode        True the UUID should be an online mode UUID (from Mojang). False if it should be an offline mode UUID (from Bukkit).
     * @param offlineUUIDonFail True if an offline UUID should be returned if the Mojang server can't resolve the name.
     *                          False if null should be returned if the Mojang server doesn't return an UUID.
     * @param lastKnownDate     The last time you know that the player had this name.
     * @return The requested UUID.
     */
    public static UUID getUUIDFromNameAsUUID(String name, boolean onlineMode, boolean offlineUUIDonFail, Date lastKnownDate)
    {
        UUID uuid = null;
        if(onlineMode)
        {
            String sUUID = getOnlineUUID(name, lastKnownDate);
            if(sUUID != null)
            {
                uuid = UUID.fromString(sUUID.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
            }
            else if(offlineUUIDonFail)
            {
                uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8));
            }
        }
        else
        {
            uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8));
        }
        return uuid;
    }

    private static String getOnlineUUID(String name, Date at)
    {
        String uuid = null;
        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL("https://api.mojang.com/users/profiles/minecraft/" + name + ((at != null) ? "?at=" + at.getTime() : "")).openStream()));
            uuid = (((JsonObject) new JsonParser().parse(in)).get("id")).getAsString();
            in.close();
        }
        catch(MalformedURLException e) // There is something going wrong!
        {
            System.out.print("\nFailed to get uuid cause of a malformed url!\n Name: \"" + name + "\" Date: " + ((at != null) ? "?at=" + at.getTime() : "null") + "\n");
            e.printStackTrace();
        }
        catch(IOException e)
        {
            System.out.print("Looks like there is a problem with the connection with mojang. Please retry later.\n");
            if(e.getMessage().contains("HTTP response code: 429")) //TODO: more reliable detection
            {
                System.out.print("You have reached the request limit of the mojang api! Please retry later!\n");
            }
            e.printStackTrace();
        }
        catch(Exception e)
        {
            if(at == null) // We can't resolve the uuid for the player
            {
                System.out.println("Unable to get UUID for: " + name + "!\n");
            }
            else if(at.getTime() == 0) // If it's not his first name maybe it's his current name
            {
                System.out.println("Unable to get UUID for: " + name + " at " + at.getTime() + "! Trying without date!\n");
                uuid = getOnlineUUID(name, null);
            }
            else // If we cant get the player with the date he was here last time it's likely that it is his first name
            {
                System.out.println("Unable to get UUID for: " + name + " at " + at.getTime() + "! Trying at=0!\n");
                uuid = getOnlineUUID(name, new Date(0));
            }
            //e.printStackTrace();
        }
        return uuid;
    }

    //region Multi querys
    //TODO: JavaDoc Exception handling, more parameters, fallback
    private final static int BATCH_SIZE = 100; // Limit from Mojang

    private class Profile
    {
        public String id;
        public String name;

        public UUID getUUID()
        {
            return UUID.fromString(id.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
        }
    }

    public static Map<String, String> getUUIDsFromNames(Collection<String> names, boolean onlineMode, boolean withSeparators)
    {
        Map<String, String> result = new HashMap<>();
        for(Map.Entry<String, UUID> entry : getUUIDsFromNamesAsUUIDs(names, onlineMode).entrySet())
        {
            result.put(entry.getKey(), (withSeparators) ? entry.getValue().toString() : entry.getValue().toString().replaceAll("-", ""));
        }
        return result;
    }

    public static Map<String, UUID> getUUIDsFromNamesAsUUIDs(Collection<String> names, boolean onlineMode)
    {
        if(onlineMode)
        {
            return getUUIDsFromNamesAsUUIDs(names);
        }
        Map<String,UUID> result = new HashMap<>();
        for(String name : names)
        {
            result.put(name, getUUIDFromNameAsUUID(name, false));
        }
        return result;
    }

    public static Map<String, UUID> getUUIDsFromNamesAsUUIDs(Collection<String> names)
    {
        List<String> batch = new ArrayList<>();
        Iterator<String> players = names.iterator();
        Map<String,UUID> result = new HashMap<>();
        boolean success;
        while (players.hasNext())
        {
            for (int i = 0; players.hasNext() && i < BATCH_SIZE; i++)
            {
                batch.add(players.next());
            }
            do
            {
                HttpURLConnection connection = null;
                try
                {
                    connection = (HttpURLConnection) new URL("https://api.mojang.com/profiles/minecraft").openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json; encoding=UTF-8");
                    connection.setUseCaches(false);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    try(OutputStream out = connection.getOutputStream())
                    {
                        out.write(GSON.toJson(batch).getBytes(Charsets.UTF_8));
                    }
                    Profile[] profiles;
                    try(Reader in = new BufferedReader(new InputStreamReader(connection.getInputStream())))
                    {
                        profiles = GSON.fromJson(in, Profile[].class);
                    }
                    for (Profile profile : profiles)
                    {
                        result.put(profile.name, profile.getUUID());
                    }
                }
                catch(IOException e)
                {
                    try
                    {
                        if(connection != null && connection.getResponseCode() == 429)
                        {
                            System.out.println("Reached the request limit of the mojang api!\nConverting will be paused for 10 minutes and then continue!");
                            //TODO: better fail handling
                            Thread.sleep(10*60*1000L);
                            success = false;
                            continue;
                        }
                    }
                    catch(Exception ignore) {}
                    e.printStackTrace();
                    return result;
                }
                batch.clear();
                success = true;
            } while(!success);
        }
        return result;
    }
    //endregion
}
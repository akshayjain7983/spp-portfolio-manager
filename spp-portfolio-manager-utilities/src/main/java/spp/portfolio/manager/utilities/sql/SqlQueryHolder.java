package spp.portfolio.manager.utilities.sql;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.io.ClassPathResource;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class SqlQueryHolder
{

    private static final String queryIdPrefix = "{";
    private static final String queryIdSuffix = "}";
    private static final Map<String, String> queryMap = new ConcurrentHashMap<String, String>();

    public static void loadQueries(String filename)
    {
        log.info("Loading SQL Queries from file: " + filename);
        String actualFileName = actualFileName(filename);

        try
        {
            BufferedReader br = null;
            if (filename.startsWith("/") || filename.startsWith("\\"))
                br = new BufferedReader(new FileReader(filename));
            else
            {
                ClassPathResource classPathResource = new ClassPathResource(filename);
                InputStream is = classPathResource.getInputStream();
                if (is != null)
                    br = new BufferedReader(new InputStreamReader(is));
                else
                    br = new BufferedReader(new FileReader(filename));
            }

            String queryId = null;
            String query = null;
            String line;

            while ((line = br.readLine()) != null)
            {
                if (line.trim().startsWith(queryIdPrefix))
                {
                    // put previous query in map
                    if (queryId != null)
                    {
                        queryMap.put(actualFileName + queryId, query);
                    }

                    // prepare next query
                    queryId = line.trim().replace(queryIdSuffix, "").replace(queryIdPrefix, "");
                    query = "";
                }
                else if (!"".equals(line))
                {
                    query += line + "\n";
                }
            }

            // put last query into the map
            if (queryId != null)
            {
                String key = actualFileName + queryId;

                if (queryMap.containsKey(key))
                    throw new IOException("Query is defined more than once " + key);
                else
                    queryMap.put(key, query);
            }

            br.close();
        }
        catch (IOException io)
        {
            log.error("IOException, details: " + io.getMessage());
        }
    }

    private static String actualFileName(String filename)
    {
        String actualFileName = filename;
        int index = filename.lastIndexOf("/");
        if (index < 0)
            index = filename.lastIndexOf("\\");

        if (index >= 0)
            actualFileName = actualFileName.substring(index + 1, actualFileName.length());

        return actualFileName;
    }

    public static String getSql(String sqlfilename, String tag)
    {
        String actualFileName = actualFileName(sqlfilename);
        if (!queryMap.isEmpty())
            return queryMap.get(actualFileName + tag);
        return null;
    }

}

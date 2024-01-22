package spp.portfolio.manager.utilities.sql;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class SqlQueryHolder extends QueryHolder
{

    private static final String queryIdPrefix = "{";

    private static final String queryIdSuffix = "}";

    public static void loadQueries(String filename)
    {
        log.info("Loading SQL Queries from file: " + filename);
        QueryHolder.loadQueries(filename, queryIdPrefix, queryIdSuffix);
    }

    public static String getSql(String sqlfilename, String tag)
    {
        return QueryHolder.getSql(sqlfilename, tag);
    }

}

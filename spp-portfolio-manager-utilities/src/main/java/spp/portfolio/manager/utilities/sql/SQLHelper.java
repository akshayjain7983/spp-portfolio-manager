package spp.portfolio.manager.utilities.sql;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.util.StringUtils;

import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;

public class SQLHelper
{
    public static final String IN_CLAUSE_PLACEHOLDER = "[IN[?]]";
    public static final String TABLE_PLACEHOLDER = "[TABLE[?]]";
    public static final String DATABASE_PLACEHOLDER = "[DB[?]]";
    public static final String JOIN_PLACEHOLDER = "[JOIN[?]]";

    public static Map<Class<?>, Integer> sqlTypesForJavaClasses = new HashMap<>();

    static
    {
        sqlTypesForJavaClasses.put(Integer.class, Types.INTEGER);
        sqlTypesForJavaClasses.put(Long.class, Types.BIGINT);
        sqlTypesForJavaClasses.put(BigDecimal.class, Types.NUMERIC);
        sqlTypesForJavaClasses.put(Float.class, Types.REAL);
        sqlTypesForJavaClasses.put(Double.class, Types.DOUBLE);
        sqlTypesForJavaClasses.put(Boolean.class, Types.BIT);
        sqlTypesForJavaClasses.put(String.class, Types.VARCHAR);
        sqlTypesForJavaClasses.put(Clob.class, Types.CLOB);
        sqlTypesForJavaClasses.put(byte[].class, Types.VARBINARY);
        sqlTypesForJavaClasses.put(Blob.class, Types.BLOB);
        sqlTypesForJavaClasses.put(Date.class, Types.TIMESTAMP);
        sqlTypesForJavaClasses.put(LocalDateTime.class, Types.TIMESTAMP);
        sqlTypesForJavaClasses.put(LocalDate.class, Types.DATE);
        sqlTypesForJavaClasses.put(ZonedDateTime.class, Types.TIMESTAMP_WITH_TIMEZONE);
        sqlTypesForJavaClasses.put(ZoneId.class, Types.VARCHAR);
        sqlTypesForJavaClasses.put(java.sql.Timestamp.class, Types.TIMESTAMP);
        sqlTypesForJavaClasses.put(java.sql.Date.class, Types.DATE);
        sqlTypesForJavaClasses.put(java.sql.Time.class, Types.TIME);
        sqlTypesForJavaClasses.put(LocalTime.class, Types.TIME);
        sqlTypesForJavaClasses.put(Enum.class, Types.VARCHAR);
        sqlTypesForJavaClasses.put(UUID.class, Types.VARCHAR);

        sqlTypesForJavaClasses = Collections.unmodifiableMap(sqlTypesForJavaClasses);

    }

    public static String replaceTableString(String sql, String tableName)
    {

        return sql.replaceFirst(Pattern.quote(TABLE_PLACEHOLDER), Matcher.quoteReplacement(tableName));
    }

    public static String replaceJoinString(String sql, String jointype)
    {

        return sql.replaceFirst(Pattern.quote(JOIN_PLACEHOLDER), Matcher.quoteReplacement(jointype));
    }

    public static String replaceDbString(String sql, String dbName)
    {

        return sql.replaceFirst(Pattern.quote(DATABASE_PLACEHOLDER), Matcher.quoteReplacement(dbName));
    }

    public static String replaceInClauseString(String sql, Collection<String> listOfValues)
    {

        StringBuilder inList = new StringBuilder();
        for (String val : listOfValues)
            inList.append("'").append(escapeSql(val)).append("'").append(",");
        inList.deleteCharAt(inList.length() - 1);

        return sql.replaceFirst(Pattern.quote(IN_CLAUSE_PLACEHOLDER), Matcher.quoteReplacement(inList.toString()));
    }

    public static String replaceInClauseNumber(String sql, Collection<? extends Number> listOfValues)
    {

        StringBuilder inList = new StringBuilder();
        for (Number val : listOfValues)
            inList.append(val.toString()).append(",");
        inList.deleteCharAt(inList.length() - 1);

        return sql.replaceFirst(Pattern.quote(IN_CLAUSE_PLACEHOLDER), Matcher.quoteReplacement(inList.toString()));

    }

    public static String setupDatabaseName(String sql, String databaseName)
    {

        while (sql.contains(DATABASE_PLACEHOLDER))
        {
            sql = replaceDbString(sql, databaseName);
        }

        return sql;
    }

    public static String setupJoinName(String sql, String jointype)
    {

        while (sql.contains(JOIN_PLACEHOLDER))
        {
            sql = replaceJoinString(sql, jointype);
        }

        return sql;
    }

    public static boolean isSupportedSqlTime(Class<?> timeType)
    {

        return LocalTime.class.equals(timeType);
    }

    public static boolean isSupportedSqlTime(Object date)
    {

        if (Objects.isNull(date))
        {
            return false;
        }

        return isSupportedSqlTime(date.getClass());
    }

    public static java.sql.Time convertToSqlTime(Object date)
    {

        if (Objects.isNull(date) || !(isSupportedSqlTime(date) || isConvertibleUsingSpringConversionService(date, java.sql.Time.class)))
        {
            return null;
        }

        Class<?> dateType = date.getClass();
        if (LocalTime.class.equals(dateType))
        {
            return java.sql.Time.valueOf((LocalTime) date);
        }
        else if (java.sql.Time.class.equals(dateType))
        {
            return (java.sql.Time) date;
        }
        return null;
    }

    public static <T> T convertFromSqlTime(java.sql.Time sqlTime, Class<T> timeType)
    {

        if (Objects.isNull(sqlTime) || !(isSupportedSqlTime(timeType) || isConvertibleUsingSpringConversionService(sqlTime, timeType)))
        {
            return null;
        }

        T resultTime = null;

        if (LocalTime.class.equals(timeType))
        {
            resultTime = (T) sqlTime.toLocalTime();
        }
        else if (isConvertibleUsingSpringConversionService(sqlTime, timeType))
        {
            resultTime = convertUsingSpringConversionService(sqlTime, timeType);
        }

        return resultTime;
    }

    public static boolean isSupportedSqlDate(Class<?> dateType)
    {
        if (Objects.isNull(dateType))
        {
            return false;
        }

        return Date.class.equals(dateType) || LocalDateTime.class.equals(dateType) || LocalDate.class.equals(dateType) || ZonedDateTime.class.equals(dateType) || java.sql.Date.class.equals(dateType);
    }

    public static boolean isSupportedSqlDate(Object date)
    {
        if (Objects.isNull(date))
        {
            return false;
        }

        return isSupportedSqlDate(date.getClass());
    }

    public static boolean isSupportedSqlTimestamp(Class<?> dateType)
    {
        if (Objects.isNull(dateType))
        {
            return false;
        }

        return Date.class.equals(dateType) || LocalDateTime.class.equals(dateType) || ZonedDateTime.class.equals(dateType) || java.sql.Timestamp.class.equals(dateType);
    }

    public static boolean isSupportedSqlTimestamp(Object date)
    {
        if (Objects.isNull(date))
        {
            return false;
        }

        return isSupportedSqlTimestamp(date.getClass());
    }

    public static <T> T convertToSqlDateOrTimestamp(Object date, Class<T> sqlType)
    {

        if (Objects.isNull(sqlType))
        {
            return null;
        }

        T result = null;

        if (sqlType.equals(java.sql.Date.class))
        {
            result = (T) convertToSqlDate(date);
        }
        else if (sqlType.equals(java.sql.Timestamp.class))
        {
            result = (T) convertToSqlTimestamp(date);
        }

        return result;
    }

    public static <T> T convertFromSqlDateOrTimestamp(Object sqlValue, Class<T> dateType)
    {

        if (Objects.isNull(sqlValue))
        {
            return null;
        }

        Class<?> sqlType = sqlValue.getClass();
        T result = null;

        if (sqlType.equals(java.sql.Date.class))
        {
            result = convertFromSqlDate((java.sql.Date) sqlValue, dateType);
        }
        else if (sqlType.equals(java.sql.Timestamp.class))
        {
            result = convertFromSqlTimestamp((java.sql.Timestamp) sqlValue, dateType);
        }

        return result;
    }

    public static java.sql.Date convertToSqlDate(Object date)
    {

        if (Objects.isNull(date) || !(isSupportedSqlDate(date) || isConvertibleUsingSpringConversionService(date, java.sql.Date.class)))
        {
            return null;
        }

        Class<?> dateType = date.getClass();

        if (java.sql.Date.class.equals(dateType))
        {
            return (java.sql.Date) date;
        }

        Date utilDate = null;
        java.sql.Date sqlDate = null;
        if (Date.class.equals(dateType))
        {
            utilDate = (Date) date;
        }
        else if (LocalDateTime.class.equals(dateType))
        {
            utilDate = Date.from(((LocalDateTime) date).atZone(ZoneId.systemDefault()).toInstant());
        }
        else if (LocalDate.class.equals(dateType))
        {
            utilDate = Date.from(((LocalDate) date).atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        else if (ZonedDateTime.class.equals(dateType))
        {
            utilDate = Date.from(((ZonedDateTime) date).toInstant());
        }
        else if (isConvertibleUsingSpringConversionService(date, java.sql.Date.class))
        {
            sqlDate = convertUsingSpringConversionService(date, java.sql.Date.class);
        }

        if (Objects.nonNull(utilDate))
        {
            sqlDate = new java.sql.Date(utilDate.getTime());
        }

        return sqlDate != null ? sqlDate : null;
    }

    public static <T> T convertFromSqlDate(java.sql.Date sqlDate, Class<T> dateType)
    {

        if (Objects.isNull(sqlDate) || !(isSupportedSqlDate(dateType) || isConvertibleUsingSpringConversionService(sqlDate, dateType)))
        {
            return null;
        }
        T resultDate = null;
        if (Date.class.equals(dateType))
        {
            resultDate = (T) sqlDate;
        }
        else if (LocalDateTime.class.equals(dateType))
        {
            resultDate = (T) sqlDate.toLocalDate().atStartOfDay();
        }
        else if (LocalDate.class.equals(dateType))
        {
            resultDate = (T) sqlDate.toLocalDate();
        }
        else if (ZonedDateTime.class.equals(dateType))
        {
            resultDate = (T) sqlDate.toLocalDate().atStartOfDay(ZoneId.systemDefault());
        }
        else if (isConvertibleUsingSpringConversionService(sqlDate, dateType))
        {
            resultDate = convertUsingSpringConversionService(sqlDate, dateType);
        }

        return resultDate;
    }

    public static java.sql.Timestamp convertToSqlTimestamp(Object date)
    {

        if (Objects.isNull(date) || !(isSupportedSqlTimestamp(date) || isConvertibleUsingSpringConversionService(date, java.sql.Timestamp.class)))
        {
            return null;
        }

        Class<?> dateType = date.getClass();

        if (java.sql.Timestamp.class.equals(dateType))
        {
            return (java.sql.Timestamp) date;
        }

        java.sql.Timestamp timestamp = null;
        if (Date.class.equals(dateType))
        {
            timestamp = new java.sql.Timestamp(((Date) date).getTime());
        }
        else if (LocalDate.class.equals(dateType))
        {
            timestamp = java.sql.Timestamp.valueOf((((LocalDate) date).atStartOfDay()));
        }
        else if (LocalDateTime.class.equals(dateType))
        {
            timestamp = java.sql.Timestamp.valueOf((LocalDateTime) date);
        }
        else if (ZonedDateTime.class.equals(dateType))
        {
            timestamp = java.sql.Timestamp.valueOf(((ZonedDateTime) date).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime());
        }
        else if (isConvertibleUsingSpringConversionService(date, java.sql.Timestamp.class))
        {
            timestamp = convertUsingSpringConversionService(date, java.sql.Timestamp.class);
        }

        return Objects.nonNull(timestamp) ? timestamp : null;
    }

    public static <T> T convertFromSqlTimestamp(java.sql.Timestamp sqlTimestamp, Class<T> dateType)
    {

        if (Objects.isNull(sqlTimestamp) || !(isSupportedSqlDate(dateType) || isConvertibleUsingSpringConversionService(sqlTimestamp, dateType)))
        {
            return null;
        }
        T resultDate = null;
        if (Date.class.equals(dateType))
        {
            resultDate = (T) sqlTimestamp;
        }
        else if (LocalDate.class.equals(dateType))
        {
            resultDate = (T) sqlTimestamp.toLocalDateTime().toLocalDate();
        }
        else if (LocalDateTime.class.equals(dateType))
        {
            resultDate = (T) sqlTimestamp.toLocalDateTime();
        }
        else if (ZonedDateTime.class.equals(dateType))
        {
            resultDate = (T) sqlTimestamp.toLocalDateTime().atZone(ZoneOffset.UTC);
        }
        else if (isConvertibleUsingSpringConversionService(sqlTimestamp, dateType))
        {
            resultDate = convertUsingSpringConversionService(sqlTimestamp, dateType);
        }

        return resultDate;
    }

    public static <T> T extractFromTuple(Tuple tuple, String columnName, Class<T> columnType, Object defaultValue)
    {

        T value = extractFromTuple(tuple, columnName, columnType);

        if (Objects.isNull(value) && Objects.nonNull(defaultValue) && columnType.isAssignableFrom(defaultValue.getClass()))
        {
            value = (T) defaultValue;
        }

        return value;
    }

    public static <T> T extractFromTuple(Tuple tuple, String columnName, Class<T> columnType)
    {

        if (Objects.isNull(tuple.get(columnName)))
        {
            return null;
        }

        TupleWrapper tw = TupleWrapper.of(tuple);
        T result = null;
        if (Boolean.class.equals(columnType))
        {
            result = columnType.cast(tw.getBoolean(columnName));
        }
        else if (Integer.class.equals(columnType))
        {
            result = columnType.cast(tw.getInt(columnName));
        }
        else if (Long.class.equals(columnType))
        {
            result = columnType.cast(tw.getLong(columnName));
        }
        else if (Float.class.equals(columnType))
        {
            result = columnType.cast(tw.getFloat(columnName));
        }
        else if (Double.class.equals(columnType))
        {
            result = columnType.cast(tw.getDouble(columnName));
        }
        else if (BigDecimal.class.equals(columnType))
        {
            result = columnType.cast(tw.getBigDecimal(columnName));
        }
        else if (String.class.equals(columnType))
        {
            result = columnType.cast(tw.getString(columnName));
        }
        else if (UUID.class.equals(columnType))
        {
            result = columnType.cast(UUID.fromString(tw.getString(columnName)));
        }
        else if (isSupportedSqlTimestamp(columnType))
        {
            result = convertFromSqlTimestamp(tw.getTimestamp(columnName), columnType);
        }
        else if (ZoneId.class.equals(columnType))
        {
            result = columnType.cast(ZoneId.of(tw.getString(columnName)));
        }
        else if (isSupportedSqlDate(columnType))
        {
            result = convertFromSqlDate(tw.getDate(columnName), columnType);
        }
        else if (isSupportedSqlTime(columnType))
        {
            result = convertFromSqlTime(tw.getTime(columnName), columnType);
        }
        else if (columnType.isEnum())
        {
            String enumStr = tw.getString(columnName);
            if (enumStr != null)
            {
                enumStr = enumStr.trim();
                T[] enumConstants = columnType.getEnumConstants();
                for (T enumCons : enumConstants)
                {
                    if (((Enum<?>) enumCons).name().equalsIgnoreCase(enumStr))
                    {
                        result = columnType.cast(enumCons);
                        break;
                    }
                }
            }
        }
        else if (isConvertibleUsingSpringConversionService(tuple.get(columnName), columnType))
        {
            result = columnType.cast(convertUsingSpringConversionService(tuple.get(columnName), columnType));
        }
        else if(Object.class.equals(columnType))
        {
            result = columnType.cast(tw.getObject(columnName));
        }
        else if (Objects.nonNull(tuple.get(columnName)))
        {
            throw new IllegalArgumentException("Invalid columnType:" + columnType.getName());
        }
        return result;
    }

    public static <T> T extractFromTuple(Tuple tuple, int columnIndex, Class<T> columnType, Object defaultValue)
    {

        String columnName = extractHeaders(tuple).get(columnIndex);
        T value = extractFromTuple(tuple, columnName, columnType);

        if (Objects.isNull(value) && Objects.nonNull(defaultValue) && columnType.isAssignableFrom(defaultValue.getClass()))
        {
            value = (T) defaultValue;
        }

        return value;
    }

    public static <T> T extractFromTuple(Tuple rs, int columnIndex, Class<T> columnType)
    {

        return extractFromTuple(rs, columnIndex, columnType, null);
    }

    private static <T> boolean isConvertibleUsingSpringConversionService(Object objDb, Class<T> columnType)
    {

        ConversionService defaultConversionService = DefaultConversionService.getSharedInstance();
        return Objects.nonNull(objDb) && defaultConversionService.canConvert(objDb.getClass(), columnType);
    }

    private static <T> T convertUsingSpringConversionService(Object objDb, Class<T> columnType)
    {

        ConversionService defaultConversionService = DefaultConversionService.getSharedInstance();

        if (isConvertibleUsingSpringConversionService(objDb, columnType))
        {

            return defaultConversionService.convert(objDb, columnType);
        }

        return null;
    }

    public static int getSqlType(Object object)
    {

        int sqlType = Types.JAVA_OBJECT;
        if (Objects.nonNull(object))
        {
            Class<?> objectType = object.getClass();
            Class<?> objectTypeToCheckForSqlType = objectType;
            if (objectType.isEnum())
            {
                objectTypeToCheckForSqlType = Enum.class;
            }
            else if (ZoneId.class.isAssignableFrom(objectType))
            {
                objectTypeToCheckForSqlType = ZoneId.class;
            }
            sqlType = sqlTypesForJavaClasses.containsKey(objectTypeToCheckForSqlType) ? sqlTypesForJavaClasses.get(objectTypeToCheckForSqlType) : Types.JAVA_OBJECT;
        }

        return sqlType;
    }

    public static Object convertObjectToSqlType(Object object)
    {

        if (object == null)
        {
            return null;
        }

        Class<?> objectType = object.getClass();
        int sqlType = getSqlType(object);
        Object val = null;
        // for dates convert to sql date types
        switch (sqlType)
        {
            case Types.TIME:
                val = convertToSqlTime(object);
                break;
            case Types.TIMESTAMP:
            case Types.TIMESTAMP_WITH_TIMEZONE:
                val = convertToSqlTimestamp(object);
                break;
            case Types.DATE:
                val = convertToSqlDate(object);
                break;
            case Types.VARCHAR:
                if (ZoneId.class.equals(objectType))
                {
                    val = ((ZoneId) object).toString();
                }
                else if (isConvertibleUsingSpringConversionService(object, String.class))
                {
                    val = convertUsingSpringConversionService(object, String.class);
                }
                else
                {
                    val = object.toString();
                }
                break;
            default:
                val = object;
                break;
        }

        return val;
    }

    public static <T> void setObject(Query query, int parameterIndex, T object)
    {
        Object val = convertObjectToSqlType(object);
        query.setParameter(parameterIndex, val);
    }
    
    public static <T> void setObject(Query query, String parameterName, T object) 
    {
        Object val = convertObjectToSqlType(object);
        query.setParameter(parameterName, val);
    }

    public static List<String> extractHeaders(Tuple tuple) 
    {
        return tuple.getElements().stream().map(TupleElement::getAlias).collect(Collectors.toList());
    }

    public static String escapeSql(String val)
    {
        if (val == null)
        {
            return null;
        }

        return StringUtils.replace(val, "'", "''");
    }
}

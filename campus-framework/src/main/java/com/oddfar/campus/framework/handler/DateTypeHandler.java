package com.oddfar.campus.framework.handler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTypeHandler extends BaseTypeHandler<Date> {

    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Date parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, FORMATTER.format(parameter));
    }

    @Override
    public Date getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String dateTimeString = rs.getString(columnName);
        return parseDate(dateTimeString);
    }

    @Override
    public Date getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String dateTimeString = rs.getString(columnIndex);
        return parseDate(dateTimeString);
    }

    @Override
    public Date getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String dateTimeString = cs.getString(columnIndex);
        return parseDate(dateTimeString);
    }

    private Date parseDate(String dateTimeString) {
        if (dateTimeString == null) {
            return null;
        }
        if (NumberUtil.isNumber(dateTimeString)) {
            return DateUtil.date(Long.valueOf(dateTimeString));
        }
        try {
            return FORMATTER.parse(dateTimeString);
        } catch (ParseException e) {
            throw new RuntimeException("Error parsing date string: " + dateTimeString, e);
        }
    }
}

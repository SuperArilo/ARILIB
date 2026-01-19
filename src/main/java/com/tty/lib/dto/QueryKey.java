package com.tty.lib.dto;

import com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.tty.lib.Log;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

// ai generated
public final class QueryKey<T> {

    private final Class<?> entityClass;
    private final String sqlSegmentSnapshot;
    private final SortedMap<String, Object> paramsSnapshot;
    private final String orderBySqlSnapshot;
    private final String readableCondition;
    private final int hashCode;

    private QueryKey(Class<?> entityClass,
                     String sqlSegmentSnapshot,
                     SortedMap<String, Object> paramsSnapshot,
                     String orderBySqlSnapshot) {
        this.entityClass = entityClass;
        this.sqlSegmentSnapshot = sqlSegmentSnapshot == null ? "" : sqlSegmentSnapshot;
        this.paramsSnapshot = paramsSnapshot == null ? new TreeMap<>() : paramsSnapshot;
        this.orderBySqlSnapshot = orderBySqlSnapshot == null ? "" : orderBySqlSnapshot;

        this.readableCondition = buildReadableCondition();
        this.hashCode = readableCondition.hashCode();
    }

    public static <T> QueryKey<T> of(LambdaQueryWrapper<T> wrapper) {
        Objects.requireNonNull(wrapper, "wrapper must not be null");

        Class<?> entityClass = wrapper.getEntityClass();
        if (entityClass == null && wrapper.getEntity() != null) {
            entityClass = wrapper.getEntity().getClass();
        }

        String rawSqlSegment;
        try {
            rawSqlSegment = wrapper.getSqlSegment();
        } catch (Exception e) {
            Log.error(e);
            rawSqlSegment = "";
        }
        String cleanedSqlSegment = cleanSqlSegment(rawSqlSegment);

        SortedMap<String, Object> paramsSnapshot;
        try {
            Map<String, Object> params = wrapper.getParamNameValuePairs();
            if (params == null || params.isEmpty()) {
                paramsSnapshot = new TreeMap<>();
            } else {
                paramsSnapshot = new TreeMap<>(params);
            }
        } catch (Exception e) {
            Log.error(e);
            paramsSnapshot = new TreeMap<>();
        }

        String orderBySqlSnapshot;
        try {
            orderBySqlSnapshot = extractOrderBySql(wrapper);
        } catch (Exception e) {
            Log.error(e);
            orderBySqlSnapshot = "";
        }

        return new QueryKey<>(entityClass, cleanedSqlSegment, paramsSnapshot, orderBySqlSnapshot);
    }

    private String buildReadableCondition() {
        StringBuilder sb = new StringBuilder();

        if (entityClass != null) {
            sb.append(entityClass.getName()).append(" | ");
        } else {
            sb.append("Entity=UNKNOWN").append(" | ");
        }

        if (StringUtils.isNotBlank(sqlSegmentSnapshot)) {
            sb.append(sqlSegmentSnapshot);
        } else {
            sb.append("<no-sql-segment>");
        }

        if (!paramsSnapshot.isEmpty()) {
            sb.append(" [");
            String paramStr = paramsSnapshot.entrySet().stream()
                    .map(entry -> {
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        String resolvedName = tryResolveParamName(key, value);
                        return resolvedName + "=" + formatValue(value);
                    })
                    .collect(Collectors.joining(", "));
            sb.append(paramStr).append("]");
        }

        if (StringUtils.isNotBlank(orderBySqlSnapshot)) {
            sb.append(" ORDER_BY:").append(orderBySqlSnapshot);
        }

        return sb.toString();
    }

    private static String cleanSqlSegment(String sqlSegment) {
        if (StringUtils.isBlank(sqlSegment)) return "";
        return sqlSegment.replaceAll("#\\{ew\\.paramNameValuePairs\\.[^}]+}", "?");
    }

    private String tryResolveParamName(String paramKey, Object paramValue) {
        if (entityClass == null || paramValue == null) {
            return paramKey;
        }

        if (!paramKey.toUpperCase().contains("MPGEN") && !paramKey.toUpperCase().contains("PARAM")) {
            return paramKey;
        }

        try {
            Object possibleEntityInstance = null;
            if (paramsSnapshot.containsKey("et")) {
                possibleEntityInstance = paramsSnapshot.get("et");
            } else if (paramsSnapshot.containsKey("entity")) {
                possibleEntityInstance = paramsSnapshot.get("entity");
            }
            if (entityClass.isInstance(possibleEntityInstance)) {
                List<String> matches = new ArrayList<>();
                for (Field field : getAllDeclaredFields(entityClass)) {
                    try {
                        field.setAccessible(true);
                        Object fieldValue = field.get(possibleEntityInstance);
                        if (Objects.equals(fieldValue, paramValue)) {
                            matches.add(field.getName());
                        }
                    } catch (Throwable ignored) {
                    }
                }
                if (matches.size() == 1) {
                    return matches.getFirst();
                }
            }
        } catch (Throwable t) {
            Log.error(t);
        }

        return paramKey;
    }

    private String formatValue(Object value) {
        if (value == null) return "null";
        if (value instanceof Number || value instanceof Boolean) return String.valueOf(value);
        return "'" + value + "'";
    }

    private static String extractOrderBySql(LambdaQueryWrapper<?> wrapper) {
        try {
            Field orderByFlagField = findFieldInHierarchy("orderBy");
            boolean orderBy = false;
            if (orderByFlagField != null) {
                orderByFlagField.setAccessible(true);
                orderBy = orderByFlagField.getBoolean(wrapper);
            }

            if (orderBy) {
                Field orderBySqlField = findFieldInHierarchy("orderBySql");
                if (orderBySqlField != null) {
                    orderBySqlField.setAccessible(true);
                    Object val = orderBySqlField.get(wrapper);
                    if (val instanceof String s) {
                        return StringUtils.isNotBlank(s) ? s : "";
                    }
                }
            }
        } catch (Throwable t) {
            Log.error(t);
        }
        return "";
    }

    private static Field findFieldInHierarchy(String fieldName) {
        Class<?> cls = AbstractLambdaWrapper.class;
        while (cls != null && cls != Object.class) {
            try {
                return cls.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                cls = cls.getSuperclass();
            }
        }
        return null;
    }

    private static List<Field> getAllDeclaredFields(Class<?> cls) {
        List<Field> fields = new ArrayList<>();
        Class<?> cur = cls;
        while (cur != null && cur != Object.class) {
            Field[] declared = cur.getDeclaredFields();
            fields.addAll(Arrays.asList(declared));
            cur = cur.getSuperclass();
        }
        return fields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QueryKey<?> that)) return false;
        return Objects.equals(this.readableCondition, that.readableCondition);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return readableCondition;
    }
}

package cz.upce.idp.authentication.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class DatabasePump {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabasePump.class);
    @NotNull
    private NamedParameterJdbcTemplate sourceJdbcTemplate;
    @NotNull
    private NamedParameterJdbcTemplate targetJdbcTemplate;
    @NotNull
    @Size(min = 6)
    private String selectQuery;
    @NotNull
    @Size(min = 6)
    private String insertQuery;
    @NotNull
    @Size(min = 1)
    private Map<String, String> mapping;

    public void setSourceDataSource(DataSource sourceDataSource) {
        sourceJdbcTemplate = new NamedParameterJdbcTemplate(sourceDataSource);
    }

    public void setTargetDataSource(DataSource targetDataSource) {
        targetJdbcTemplate = new NamedParameterJdbcTemplate(targetDataSource);
    }

    public String getSelectQuery() {
        return selectQuery;
    }

    public void setSelectQuery(String selectQuery) {
        this.selectQuery = selectQuery;
    }

    public String getInsertQuery() {
        return insertQuery;
    }

    public void setInsertQuery(String insertQuery) {
        this.insertQuery = insertQuery;
    }

    public Map<String, String> getMapping() {
        return mapping;
    }

    public void setMapping(Map<String, String> mapping) {
        this.mapping = mapping;
    }

    public void pump() {
        LOGGER.info("Starting database pump");

        List<Map<String, Object>> items = sourceJdbcTemplate.queryForList(selectQuery, Collections.EMPTY_MAP);
        LOGGER.info("Preparing pump for {} items", items.size());
        int completed = 0;
        for (Map<String, Object> item : items) {
            Map<String, Object> parameters = new HashMap<String, Object>();
            for (Map.Entry<String, String> map : mapping.entrySet()) {
                parameters.put(map.getValue(), item.get(map.getKey()));
            }
            try {
                int count = targetJdbcTemplate.update(insertQuery, parameters);
                completed += count;
                LOGGER.info("{}/{} rows added", completed, items.size());
            } catch (Throwable th) {
                LOGGER.error("Error adding", th);
            }
        }

        LOGGER.info("Pumping finished");
    }
}

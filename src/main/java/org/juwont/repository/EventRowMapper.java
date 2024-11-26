package org.juwont.repository;

import org.juwont.domain.Event;
import org.juwont.domain.EventType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        return Event.builder()
                .aggregateId(UUID.fromString(rs.getString("account_id")))
                .version(rs.getLong("account_version"))
                .timestamp(rs.getTimestamp("event_timestamp").toInstant())
                .data(rs.getString("event_data"))
                .type(EventType.valueOf(rs.getString("event_type")))
                .build();
    }
}

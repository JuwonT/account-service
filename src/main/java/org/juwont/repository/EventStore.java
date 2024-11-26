package org.juwont.repository;

import lombok.RequiredArgsConstructor;
import org.juwont.domain.Event;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class EventStore {

    private static final String SELECT_EVENT = "SELECT * FROM events WHERE account_id=:accountId ORDER BY account_version;";
    private static final String INSERT_EVENTS = """
            INSERT INTO events(account_id, account_version, event_timestamp, event_data, event_type) \
            VALUES (:accountId, :version, :timestamp, :data, :type);
            """;

    private final NamedParameterJdbcTemplate template;
    private final EventRowMapper mapper;

    @Transactional(readOnly = true)
    public List<Event> getEvents(final UUID accountID) {
        final MapSqlParameterSource params = new MapSqlParameterSource(Map.of(
                "accountId", accountID.toString()
        ));

        return template.queryForStream(SELECT_EVENT, params, new EventRowMapper()).toList();
    }

    @Transactional
    public void insertEvent(final Event event) {
        template.update(INSERT_EVENTS, toParamSource(event));
    }

    private SqlParameterSource toParamSource(final Event event) {
        return new MapSqlParameterSource(Map.of(
                "accountId", event.aggregateId().toString(),
                "version", event.version(),
                "timestamp", Timestamp.from(event.timestamp()),
                "data", event.data(),
                "type", event.type().name()
        ));
    }
}

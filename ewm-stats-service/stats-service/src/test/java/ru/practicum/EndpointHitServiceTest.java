package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;
import ru.practicum.service.EndpointHitService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EndpointHitServiceTest {

    private final EndpointHitService endpointHitService;
    private final EntityManager em;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    public void createHitTest() {
        // Create EndpointHit
        EndpointHit endpointHit =
                createEndpointHit("/test", "127.0.0.1", "2000-01-01 10:00:00");
        // Save EndpointHit to DB
        endpointHitService.createHit(endpointHit);
        // Get EndpointHit from DB
        List<EndpointHit> hits = getHitsFromDB();
        // Check results
        assertThat(hits.size(), equalTo(1));
        assertThat(hits.get(0).getId(), notNullValue());
        assertThat(hits.get(0).getApp(), equalTo("service1"));
        assertThat(hits.get(0).getUri(), equalTo("/test"));
        assertThat(hits.get(0).getIp(), equalTo("127.0.0.1"));
        assertThat(hits.get(0).getTimestamp(), equalTo(LocalDateTime.parse("2000-01-01 10:00:00", formatter)));
    }

    @Test
    public void getStatsTest() {
        // Create list of EndpointHit
        List<EndpointHit> hits = List.of(
                createEndpointHit("/1", "127.0.0.1", "2000-01-01 10:00:00"),
                createEndpointHit("/1", "127.0.0.1", "2000-01-01 11:00:00"),
                createEndpointHit("/1", "192.168.1.1", "2000-01-01 12:00:00"),
                createEndpointHit("/2", "127.0.0.1", "2000-01-01 13:00:00"),
                createEndpointHit("/2", "192.168.1.1", "2000-01-01 14:00:00"),
                createEndpointHit("/3", "192.168.1.1", "2000-01-01 15:00:00")
        );
        // Save list of EndpointHit to DB
        insertHits(hits);

        // Get statistic from DB (valid param, unique == false)
        List<ViewStats> viewStatsList1 = endpointHitService.getStats(
                LocalDateTime.parse("2000-01-01 10:00:00", formatter),
                LocalDateTime.parse("2000-01-01 14:00:00", formatter),
                List.of("/1", "/2"),
                false
        );
        // Check results
        assertThat(viewStatsList1.size(), equalTo(2));
        assertThat(viewStatsList1.get(0).getHits(), equalTo(3L));
        assertThat(viewStatsList1.get(1).getHits(), equalTo(2L));
        assertThat(viewStatsList1.get(0).getApp(), equalTo("service1"));
        assertThat(viewStatsList1.get(1).getApp(), equalTo("service1"));
        assertThat(viewStatsList1.get(0).getUri(), equalTo("/1"));
        assertThat(viewStatsList1.get(1).getUri(), equalTo("/2"));

        // Get statistic from DB (valid param, unique == true)
        List<ViewStats> viewStatsList2 = endpointHitService.getStats(
                LocalDateTime.parse("2000-01-01 10:00:00", formatter),
                LocalDateTime.parse("2000-01-01 14:00:00", formatter),
                List.of("/1", "/2"),
                true
        );
        // Check results
        assertThat(viewStatsList2.size(), equalTo(2));
        assertThat(viewStatsList2.get(0).getHits(), equalTo(2L));
        assertThat(viewStatsList2.get(1).getHits(), equalTo(2L));

        // Get statistic from DB (start after end)
        assertThrows(RuntimeException.class, () -> endpointHitService.getStats(
                LocalDateTime.parse("2100-01-01 10:00:00", formatter),
                LocalDateTime.parse("2000-01-01 10:00:00", formatter),
                List.of("/1", "/2"),
                false
        ));

        // Get statistic from DB (valid param, uris not exists)
        List<ViewStats> viewStatsList3 = endpointHitService.getStats(
                LocalDateTime.parse("2000-01-01 10:00:00", formatter),
                LocalDateTime.parse("2000-01-01 14:00:00", formatter),
                List.of("/4"),
                false
        );
        // Check results
        assertThat(viewStatsList3, is(empty()));

        // Get statistic from DB (valid param, uris is null, unique == false)
        List<ViewStats> viewStatsList4 = endpointHitService.getStats(
                LocalDateTime.parse("2000-01-01 10:00:00", formatter),
                LocalDateTime.parse("2000-01-01 14:00:00", formatter),
                null,
                false
        );
        // Check results
        assertThat(viewStatsList4.size(), equalTo(2));
        assertThat(viewStatsList4.get(0).getHits(), equalTo(3L));
        assertThat(viewStatsList4.get(1).getHits(), equalTo(2L));

        // Get statistic from DB (valid param, uris is null, unique == true)
        List<ViewStats> viewStatsList5 = endpointHitService.getStats(
                LocalDateTime.parse("2000-01-01 10:00:00", formatter),
                LocalDateTime.parse("2000-01-01 14:00:00", formatter),
                null,
                true
        );
        // Check results
        assertThat(viewStatsList5.size(), equalTo(2));
        assertThat(viewStatsList5.get(0).getHits(), equalTo(2L));
        assertThat(viewStatsList5.get(1).getHits(), equalTo(2L));
    }

    private EndpointHit createEndpointHit(String uri, String ip, String dateTime) {
        return EndpointHit.builder()
                .app("service1")
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.parse(dateTime, formatter))
                .build();
    }

    private List<EndpointHit> getHitsFromDB() {
        TypedQuery<EndpointHit> query = em.createQuery("SELECT eh FROM EndpointHit eh", EndpointHit.class);
        return query.getResultList();
    }

    private void insertHits(List<EndpointHit> list) {
        list.forEach(em::persist);
    }
}

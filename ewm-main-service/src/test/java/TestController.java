//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import ru.practicum.client.StatsClient;
//import ru.practicum.dto.EndpointHitDto;
//import ru.practicum.dto.ViewStatsDto;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//
//@RestController
//@RequestMapping("")
//@RequiredArgsConstructor
//public class TestController {
//
//    private final StatsClient statsClient;
//
//    @PostMapping("/postTest")
//    public String sendTestHit() {
//        return statsClient.postHit(EndpointHitDto.builder()
//                .app("service100")
//                .uri("/uri")
//                .ip("192.168.1.1")
//                .timestamp(LocalDateTime.parse("2000-01-01 15:10:05",
//                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
//                .build()).block();
//    }
//
//    @GetMapping("/getTest")
//    public List<ViewStatsDto> getStats() {
//        return statsClient.getStats(
//                LocalDateTime.parse("2000-01-01 15:10:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
//                LocalDateTime.parse("2000-01-01 15:10:10", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
//                List.of("/uri", "/admin"),
//                true)
//                .block();
//    }
//}

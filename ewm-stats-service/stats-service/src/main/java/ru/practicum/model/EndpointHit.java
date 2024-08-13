package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "endpoint_hits")
public class EndpointHit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_identifier", nullable = false)
    private String app;

    @Column(name = "endpoint_uri", nullable = false)
    private String uri;

    @Column(name = "client_ip", nullable = false)
    private String ip;

    @Column(name = "request_date", nullable = false)
    private LocalDateTime timestamp;
}

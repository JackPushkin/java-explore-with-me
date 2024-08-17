package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @Length(min = 20, max = 2000)
    private String annotation;

    @Column(nullable = false)
    @Length(min = 3, max = 120)
    private String title;

    @Column(nullable = false)
    @Length(min = 20, max = 7000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_category")
    private Category category;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "publish_date")
    private LocalDateTime publishedOn;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_initiator")
    private User initiator;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_location")
    private Location location;

    @Column(nullable = false)
    private boolean paid;

    @Column(name = "part_limit")
    private Integer participantLimit;

    @Column(name = "moderation", nullable = false)
    private boolean requestModeration;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventState state;

    @Transient
    private Long confirmedRequests = 0L;

    public Event(Integer id, Long confirmedRequests) {
        this.id = id;
        this.confirmedRequests = confirmedRequests;
    }
}

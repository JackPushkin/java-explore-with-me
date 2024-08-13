package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
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

    @Column(name = "view")
    private Integer views;

    @Transient
    private Long confirmedRequests = 0L;

    public Event(Integer id, Long confirmedRequests) {
        this.id = id;
        this.confirmedRequests = confirmedRequests;
    }
}

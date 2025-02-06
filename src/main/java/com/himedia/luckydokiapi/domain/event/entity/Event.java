package com.himedia.luckydokiapi.domain.event.entity;

import com.himedia.luckydokiapi.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString(exclude = "eventBridgeList")
@Table(name = "event")
public class Event extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(columnDefinition = "LONGTEXT")
    private String image;

    @Column(name = "start_at", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime endAt;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    @Builder.Default
    private List<EventBridge> eventBridgeList = new ArrayList<>();
}

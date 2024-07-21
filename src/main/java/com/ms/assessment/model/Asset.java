package com.ms.assessment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String symbol;
    private int quantity;
    @ManyToOne
    @JoinColumn(name = "userName", referencedColumnName = "userName", nullable = false)
    private Users user;
    private OffsetDateTime updatedTime;
}

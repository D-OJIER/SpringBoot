package com.task.water_billing.telemetry.entity;

import com.task.water_billing.property.entity.Apartment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "guest_stay")
@Getter @Setter
public class GuestStay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    private int guestCount;

    @ManyToOne
    @JoinColumn(name = "apartment_id")
    private Apartment apartment;
}
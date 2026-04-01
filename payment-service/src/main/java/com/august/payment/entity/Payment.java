package com.august.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;


@Entity
@Table(name = "name")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column()
    private Long orderId;

    @Column()
    private Long userId;

    @Column()
    private Long totalPrice;

    @Column()
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column()
    private String transactionId;

    @Column()
    private ZonedDateTime createdAt;

}

package com.omeganessy.payroll.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "CUSTOM_ORDER")
public class Order {
    @Id
    @GeneratedValue
    private Long id;
    @NonNull
    private String description;
    @NonNull
    private Status status;

}

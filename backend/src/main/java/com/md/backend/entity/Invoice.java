package com.md.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id", nullable = false)
    private Long invoiceId;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "issue_date",nullable = false)
    private LocalDate issueDate;

    @Column(name = "due_date",nullable = false)
    private LocalDate dueDate;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(nullable = false)
    private List<Item> items;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false)
    private double totalPrice;
}

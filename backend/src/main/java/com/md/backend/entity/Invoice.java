package com.md.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
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

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false)
    private double price;
}

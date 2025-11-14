package com.example.banque_service.dtos;

import com.example.banque_service.entities.TypeTransaction;
import lombok.Data;

import java.util.Date;

@Data
public class TransactionRequest {
    private Long compteId;
    private Double montant;
    private Date date;
    private TypeTransaction type;
}
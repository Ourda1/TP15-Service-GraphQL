package com.example.banque_service.controllers;

import com.example.banque_service.dtos.TransactionRequest;
import com.example.banque_service.entities.Compte;
import com.example.banque_service.entities.Transaction;
import com.example.banque_service.entities.TypeTransaction;
import com.example.banque_service.repositories.CompteRepository;
import com.example.banque_service.repositories.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
public class CompteControllerGraphQL {
    private CompteRepository compteRepository;
    private TransactionRepository transactionRepository;

    // Méthodes existantes pour les comptes...
    @QueryMapping
    public List<Compte> allComptes(){
        return compteRepository.findAll();
    }

    @QueryMapping
    public Compte compteById(@Argument Long id){
        return compteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format("Compte %s not found", id)));
    }

    @MutationMapping
    public Compte saveCompte(@Argument Compte compte){
        return compteRepository.save(compte);
    }

    @QueryMapping
    public Map<String, Object> totalSolde() {
        long count = compteRepository.count();
        double sum = compteRepository.sumSoldes();
        double average = count > 0 ? sum / count : 0;

        return Map.of(
                "count", count,
                "sum", sum,
                "average", average
        );
    }

    // Nouvelles méthodes pour les transactions

    @MutationMapping
    public Transaction addTransaction(@Argument("transaction") TransactionRequest transactionRequest) {
        if (transactionRequest == null) {
            throw new RuntimeException("Transaction request cannot be null");
        }

        Compte compte = compteRepository.findById(transactionRequest.getCompteId())
                .orElseThrow(() -> new RuntimeException("Compte not found"));

        Transaction transaction = new Transaction();
        transaction.setMontant(transactionRequest.getMontant());
        transaction.setDate(transactionRequest.getDate() != null ? transactionRequest.getDate() : new Date());
        transaction.setType(transactionRequest.getType());
        transaction.setCompte(compte);

        // Mettre à jour le solde du compte
        if (transactionRequest.getType() == TypeTransaction.DEPOT) {
            compte.setSolde(compte.getSolde() + transactionRequest.getMontant());
        } else if (transactionRequest.getType() == TypeTransaction.RETRAIT) {
            // Vérifier si le solde est suffisant pour le retrait
            if (compte.getSolde() < transactionRequest.getMontant()) {
                throw new RuntimeException("Solde insuffisant pour effectuer ce retrait");
            }
            compte.setSolde(compte.getSolde() - transactionRequest.getMontant());
        }

        compteRepository.save(compte);
        return transactionRepository.save(transaction);
    }

}
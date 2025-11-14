package com.example.banque_service.repositories;

import com.example.banque_service.entities.Compte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CompteRepository extends JpaRepository<Compte, Long> {

    /**
     * Calcule la somme de tous les soldes des comptes
     * @return la somme totale des soldes
     */
    @Query("SELECT SUM(c.solde) FROM Compte c")
    Double sumSoldes();
}

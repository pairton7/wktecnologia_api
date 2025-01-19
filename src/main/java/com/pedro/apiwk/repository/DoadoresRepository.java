package com.pedro.apiwk.repository;
import com.pedro.apiwk.model.Doadores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoadoresRepository extends JpaRepository<Doadores, Integer> {
}

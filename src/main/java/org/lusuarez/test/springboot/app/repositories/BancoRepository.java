package org.lusuarez.test.springboot.app.repositories;

import org.lusuarez.test.springboot.app.models.Banco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BancoRepository extends JpaRepository<Banco, Long> {

}

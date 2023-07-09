package org.lusuarez.test.springboot.app;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.lusuarez.test.springboot.app.models.Cuenta;
import org.lusuarez.test.springboot.app.repositories.CuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integracion_jpa")
@DataJpaTest
public class IntegracionJpaTest {

    @Autowired
    private CuentaRepository cuentaRepository;

    @Test
    void testFindById() {
        Optional<Cuenta> cuenta = cuentaRepository.findById(1L);

        assertTrue(cuenta.isPresent());
        assertEquals("Andrés", cuenta.orElseThrow().getPersona());
    }

    @Test
    void testFindByPersona() {
        Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Andrés");

        assertTrue(cuenta.isPresent());
        assertEquals("Andrés", cuenta.orElseThrow().getPersona());
        assertEquals("1000.00", cuenta.orElseThrow().getSaldo().toPlainString());
    }

    @Test
    void testFindByPersonaThrowException() {
        Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Luis");

        //assertThrows(NoSuchElementException.class, () -> {cuenta.orElseThrow();});
        assertThrows(NoSuchElementException.class, cuenta::orElseThrow);
        assertFalse(cuenta.isPresent());
    }

    @Test
    void testFindAll() {
        List<Cuenta> listCuentas = cuentaRepository.findAll();

        assertFalse(listCuentas.isEmpty());
        assertEquals(2, listCuentas.size());
    }

    @Test
    void testSave() {
        //Given
        Cuenta cuenta = new Cuenta(null, "Pepe", new BigDecimal("3000"));


        //When
        Cuenta cuentaSave = cuentaRepository.save(cuenta);
        //Cuenta cuenta = cuentaRepository.findByPersona("Pepe").orElseThrow();
        //Cuenta cuenta = cuentaRepository.findById(cuenta.getId()).orElseThrow();

        //Then
        assertEquals("Pepe", cuentaSave.getPersona());
        assertEquals("3000", cuentaSave.getSaldo().toPlainString());
        assertTrue(cuentaSave.getId() != null);
        assertNotEquals(null, cuentaSave.getId());
    }

    @Test
    void testUpdate() {
        //Given
        Cuenta cuenta = new Cuenta(null, "Pepe", new BigDecimal("3000"));

        //When
        Cuenta cuentaSave = cuentaRepository.save(cuenta);

        //Then
        assertEquals("Pepe", cuentaSave.getPersona());
        assertEquals("3000", cuentaSave.getSaldo().toPlainString());
        assertTrue(cuentaSave.getId() != null);
        assertNotEquals(null, cuentaSave.getId());

        cuentaSave.setSaldo(new BigDecimal("3800"));
        Cuenta cuentaUpdate = cuentaRepository.save(cuenta);

        assertEquals("Pepe", cuentaUpdate.getPersona());
        assertEquals("3800", cuentaUpdate.getSaldo().toPlainString());
    }

    @Test
    void testDelete() {
        Cuenta cuenta = cuentaRepository.findById(2L).orElseThrow();
        assertEquals("Jhon", cuenta.getPersona());

        cuentaRepository.delete(cuenta);

        assertThrows(NoSuchElementException.class, () -> {
            //cuentaRepository.findByPersona("Jhon").orElseThrow();
            cuentaRepository.findById(2L).orElseThrow();
        });
        assertEquals(1, cuentaRepository.findAll().size());
    }

}

package org.lusuarez.test.springboot.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.lusuarez.test.springboot.app.Datos.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lusuarez.test.springboot.app.exceptions.DineroInsuficienteException;
import org.lusuarez.test.springboot.app.models.Banco;
import org.lusuarez.test.springboot.app.models.Cuenta;
import org.lusuarez.test.springboot.app.repositories.BancoRepository;
import org.lusuarez.test.springboot.app.repositories.CuentaRepository;
import org.lusuarez.test.springboot.app.services.CuentaService;

import org.lusuarez.test.springboot.app.services.CuentaServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class SpringbootTestApplicationTests {

	@MockBean
	//@Mock
	CuentaRepository cuentaRepository;

	@MockBean
	//@Mock
	BancoRepository bancoRepository;

	@Autowired
	//@InjectMocks
	CuentaService cuentaService;

	@BeforeEach
	void setUp() {
		//cuentaRepository = mock(CuentaRepository.class);
		//bancoRepository = mock(BancoRepository.class);
		//cuentaService = new CuentaServiceImpl(cuentaRepository, bancoRepository);
	}

	@Test
	void contextLoads() {
		when(cuentaRepository.findById(1L)).thenReturn(crearCuenta001());
		when(cuentaRepository.findById(2L)).thenReturn(crearCuenta002());
		when(bancoRepository.findById(1L)).thenReturn(crearBanco());

		BigDecimal saldoOrigen = cuentaService.revisarSaldo(1L);
		BigDecimal saldoDestino = cuentaService.revisarSaldo(2L);

		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());

		cuentaService.transferir(1L, 2L, new BigDecimal("100"), 1L);

		saldoOrigen = cuentaService.revisarSaldo(1L);
		saldoDestino = cuentaService.revisarSaldo(2L);

		int total  = cuentaService.revisarTotalTransferencia(1L);
		assertEquals(1, total);

		assertEquals("900", saldoOrigen.toPlainString());
		assertEquals("2100", saldoDestino.toPlainString());

		verify(cuentaRepository, times(3)).findById(1L);
		verify(cuentaRepository, times(3)).findById(2L);
		verify(cuentaRepository, times(2)).save(any(Cuenta.class));

		verify(bancoRepository, times(2)).findById(1L);
		verify(bancoRepository).save(any(Banco.class));

		verify(cuentaRepository, times(6)).findById(anyLong());
		verify(cuentaRepository, never()).findAll();
	}

	@Test
	void contextLoads2() {
		when(cuentaRepository.findById(1L)).thenReturn(crearCuenta001());
		when(cuentaRepository.findById(2L)).thenReturn(crearCuenta002());
		when(bancoRepository.findById(1L)).thenReturn(crearBanco());

		BigDecimal saldoOrigen = cuentaService.revisarSaldo(1L);
		BigDecimal saldoDestino = cuentaService.revisarSaldo(2L);

		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());

		assertThrows(DineroInsuficienteException.class, () -> {
			cuentaService.transferir(1L, 2L, new BigDecimal("1200"), 1L);
		});

		saldoOrigen = cuentaService.revisarSaldo(1L);
		saldoDestino = cuentaService.revisarSaldo(2L);

		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());

		int total  = cuentaService.revisarTotalTransferencia(1L);
		assertEquals(0, total);

		verify(cuentaRepository, times(3)).findById(1L);
		verify(cuentaRepository, times(2)).findById(2L);
		verify(cuentaRepository, never()).save(any(Cuenta.class));

		verify(bancoRepository, times(1)).findById(1L);
		verify(bancoRepository, never()).save(any(Banco.class));

		verify(cuentaRepository, times(5)).findById(anyLong());
		verify(cuentaRepository, never()).findAll();
	}

	@Test
	void contextLoads3() {
		when(cuentaRepository.findById(1L)).thenReturn(crearCuenta001());

		Cuenta cuenta1 = cuentaService.findById(1L);
		Cuenta cuenta2 = cuentaService.findById(1L);

		assertSame(cuenta1, cuenta2);
		assertTrue(cuenta1 == cuenta2);
		assertEquals("Andrés", cuenta1.getPersona());
		assertEquals("Andrés", cuenta2.getPersona());

		verify(cuentaRepository, times(2)).findById(1L);
	}


	@Test
	void testFindAll() {
		//Given
		List<Cuenta> datos = Arrays.asList(crearCuenta001().orElseThrow(),
				crearCuenta002().orElseThrow());
		when(cuentaRepository.findAll()).thenReturn(datos);

		//When
		List<Cuenta> cuentas = cuentaService.findAll();

		//Then
		assertFalse(cuentas.isEmpty());
		assertEquals(2, cuentas.size());
		assertTrue(cuentas.contains(crearCuenta002().orElseThrow()));

		verify(cuentaRepository).findAll();

	}

	@Test
	void testSave() {
		//given
		Cuenta cuentaPepe = new Cuenta(null, "Pepe", new BigDecimal("3000"));
		when(cuentaRepository.save(any())).then(invocation ->{
			Cuenta c = invocation.getArgument(0);
			c.setId(3L);
			return c;
		});

		//when
		Cuenta cuenta = cuentaService.save(cuentaPepe);

		//then
		assertEquals("Pepe", cuenta.getPersona());
		assertEquals(3, cuenta.getId());
		assertEquals("3000", cuenta.getSaldo().toString());

		verify(cuentaRepository).save(any());

	}
}

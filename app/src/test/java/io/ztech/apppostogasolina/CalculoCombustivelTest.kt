package io.ztech.apppostogasolina

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Teste Unitário Local (roda na JVM do computador).
 * Valida a lógica matemática dos IFs de consumo.
 */
class CalculoCombustivelTest {

    // --- TESTES DE MOTOS ---

    @Test
    fun testeMoto_BaixaCilindrada_Cidade() {
        // Cenário: Moto pequena na cidade.
        // Regra esperada: 30.0 km/l
        val resultado = ListaCombustivelActivity.calcularConsumo(
            "Moto",
            "150cc-500cc",
            "Gasolina",
            "Cidade"
        )

        // assertEquals(esperado, resultado, margem_de_erro)
        assertEquals(30.0, resultado, 0.0)
    }

    @Test
    fun testeMoto_AltaCilindrada_Premium_GanhaPotencia() {
        // Cenário: Moto potente com Gasolina Premium.
        // Regra esperada: Base (19.0) * 1.05 = 19.95 km/l
        val resultado = ListaCombustivelActivity.calcularConsumo(
            "Moto",
            "600cc-750cc",
            "Gasolina Premium",
            "Cidade"
        )

        // Usamos delta de 0.001 para comparações de ponto flutuante (double)
        assertEquals(19.95, resultado, 0.001)
    }

    @Test
    fun testeMoto_BaixaCilindrada_Premium_NaoGanhaPotencia() {
        // Cenário: Moto pequena com Gasolina Premium (Não deve ter ganho).
        // Regra esperada: Base Estrada (26.0) mantida = 26.0 km/l
        val resultado = ListaCombustivelActivity.calcularConsumo(
            "Moto",
            "150cc-500cc",
            "Gasolina Premium",
            "Estrada"
        )

        assertEquals(26.0, resultado, 0.0)
    }

    // --- TESTES DE CARROS ---

    @Test
    fun testeCarro_1_0_Gasolina_Cidade() {
        // Cenário: Carro 1.0 na cidade com Gasolina.
        // Regra esperada: 13.0 km/l
        val resultado = ListaCombustivelActivity.calcularConsumo(
            "Carro",
            "1.0",
            "Gasolina",
            "Cidade"
        )

        assertEquals(13.0, resultado, 0.0)
    }

    @Test
    fun testeCarro_2_0_Gasolina_Estrada() {
        // Cenário: Carro potente na estrada com Gasolina.
        // Regra esperada: 11.0 km/l
        val resultado = ListaCombustivelActivity.calcularConsumo(
            "Carro",
            "2.0+",
            "Gasolina",
            "Estrada"
        )

        assertEquals(11.0, resultado, 0.0)
    }

    @Test
    fun testeCarro_1_6_Etanol_Cidade() {
        // Cenário: Carro médio na cidade com Etanol.
        // Regra esperada: 7.0 km/l
        val resultado = ListaCombustivelActivity.calcularConsumo(
            "Carro",
            "1.6",
            "Etanol",
            "Cidade"
        )

        assertEquals(7.0, resultado, 0.0)
    }
}
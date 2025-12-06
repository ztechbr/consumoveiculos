package io.ztech.apppostogasolina

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FluxoUsuarioTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun fluxoCompleto_CalcularMelhorOpcao() {
        // 1. Digita os Preços na MainActivity
        onView(withId(R.id.edtPreco1)).perform(typeText("5.00"), closeSoftKeyboard()) // Gasolina
        onView(withId(R.id.edtPreco2)).perform(typeText("3.50"), closeSoftKeyboard()) // Etanol

        // 2. Verifica se o botão 2 está desabilitado inicialmente
        onView(withId(R.id.btnBuscar2)).check(matches(not(isEnabled())))

        // --- FLUXO DO BOTÃO 1 (CARRO 1.0 GASOLINA CIDADE) ---
        onView(withId(R.id.btnBuscar1)).perform(click())

        // Estamos na ListaCombustivelActivity agora.
        // Seleciona "Carro"
        onView(withId(R.id.spTipoVeiculo)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Carro"))).perform(click())

        // Seleciona Motor "1.0"
        onView(withId(R.id.spMotorizacao)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("1.0"))).perform(click())

        // Seleciona "Gasolina"
        onView(withId(R.id.spCombustivel)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Gasolina"))).perform(click())

        // Seleciona "Cidade"
        onView(withId(R.id.spUso)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Cidade"))).perform(click())

        // Confirma
        onView(withId(R.id.btnConfirmar)).perform(click())

        // --- DE VOLTA A MAIN ACTIVITY ---
        // Verifica se preencheu o Consumo 1 com 13.0 (Regra do Carro 1.0 Gasolina Cidade)
        onView(withId(R.id.edtConsumo1)).check(matches(withText("13.0")))

        // Verifica se o Botão 2 agora está habilitado
        onView(withId(R.id.btnBuscar2)).check(matches(isEnabled()))

        // --- FLUXO DO BOTÃO 2 (CARRO 1.0 ETANOL CIDADE) ---
        onView(withId(R.id.btnBuscar2)).perform(click())

        // Seleciona "Carro" (Já deve vir pré-selecionado, mas garantimos)
        // Seleciona "Etanol"
        onView(withId(R.id.spCombustivel)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Etanol"))).perform(click())

        // Confirma
        onView(withId(R.id.btnConfirmar)).perform(click())

        // Verifica se preencheu o Consumo 2 com 9.5 (Regra do Carro 1.0 Etanol Cidade)
        onView(withId(R.id.edtConsumo2)).check(matches(withText("9.5")))

        // --- CALCULAR RESULTADO ---
        onView(withId(R.id.btnCalcular)).perform(click())

        // Cálculo:
        // Opção 1 (Gasolina): R$ 5.00 / 13.0km/l = R$ 0.38/km
        // Opção 2 (Etanol):   R$ 3.50 / 9.5km/l  = R$ 0.36/km (Mais barato!)

        // Verifica se o resultado diz que o Combustível 2 é melhor
        onView(withId(R.id.txtResultado)).check(matches(withText(containsString("A opção 2"))))
    }
}
package io.ztech.apppostogasolina

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class ListaCombustivelActivity : AppCompatActivity() {

    private lateinit var spTipoVeiculo: Spinner
    private lateinit var spMotorizacao: Spinner
    private lateinit var spCombustivel: Spinner
    private lateinit var spUso: Spinner
    private lateinit var btnConfirmar: Button

    // Variáveis temporárias para guardar o que veio da tela anterior (Memória)
    private var motorParaRestaurar: String? = null
    private var combustivelParaRestaurar: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_combustivel)

        spTipoVeiculo = findViewById(R.id.spTipoVeiculo)
        spMotorizacao = findViewById(R.id.spMotorizacao)
        spCombustivel = findViewById(R.id.spCombustivel)
        spUso = findViewById(R.id.spUso)
        btnConfirmar = findViewById(R.id.btnConfirmar)

        // 1. Captura os dados enviados pela MainActivity (se houver) para pré-seleção
        val tipoVindo = intent.getStringExtra("EXTRA_TIPO")
        motorParaRestaurar = intent.getStringExtra("EXTRA_MOTOR")
        combustivelParaRestaurar = intent.getStringExtra("EXTRA_COMBUSTIVEL")
        val usoVindo = intent.getStringExtra("EXTRA_USO")

        configurarSpinners()

        // 2. Se vieram dados, aplica a pré-seleção inicial (Tipo e Uso)
        if (tipoVindo != null) {
            selecionarItemSpinner(spTipoVeiculo, tipoVindo)
        }
        if (usoVindo != null) {
            selecionarItemSpinner(spUso, usoVindo)
        }

        btnConfirmar.setOnClickListener {
            enviarDadosDeVolta()
        }
    }

    private fun configurarSpinners() {
        val tipos = arrayOf("Carro", "Moto")
        val adapterTipo = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tipos)
        spTipoVeiculo.adapter = adapterTipo

        val usos = arrayOf("Cidade", "Estrada")
        val adapterUso = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, usos)
        spUso.adapter = adapterUso

        spTipoVeiculo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Ao mudar o tipo (Carro/Moto), recarregamos as listas dependentes (Motor/Combustível)
                atualizarListasDependentes(tipos[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun atualizarListasDependentes(tipo: String) {
        val opcoesMotor: Array<String>
        val opcoesCombustivel: Array<String>

        if (tipo == "Carro") {
            opcoesMotor = arrayOf("1.0", "1.3", "1.6", "2.0+")
            opcoesCombustivel = arrayOf("Gasolina", "Etanol")
        } else {
            opcoesMotor = arrayOf("150cc-500cc", "600cc-750cc", "950cc+")
            opcoesCombustivel = arrayOf("Gasolina", "Gasolina Premium")
        }

        val adapterMotor = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opcoesMotor)
        spMotorizacao.adapter = adapterMotor

        val adapterCombustivel = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opcoesCombustivel)
        spCombustivel.adapter = adapterCombustivel

        // 3. Lógica de Restauração (Memória):
        // Se temos um motor/combustível pendente para restaurar (vindo da Intent), selecionamos agora
        // e depois limpamos a variável para não interferir em escolhas futuras manuais.
        if (motorParaRestaurar != null) {
            selecionarItemSpinner(spMotorizacao, motorParaRestaurar!!)
            motorParaRestaurar = null
        }
        if (combustivelParaRestaurar != null) {
            selecionarItemSpinner(spCombustivel, combustivelParaRestaurar!!)
            combustivelParaRestaurar = null
        }
    }

    private fun enviarDadosDeVolta() {
        val tipo = spTipoVeiculo.selectedItem.toString()
        val motor = spMotorizacao.selectedItem.toString()
        val combustivel = spCombustivel.selectedItem.toString()
        val uso = spUso.selectedItem.toString()

        // Chama a função estática (do companion object) para calcular
        val consumoEstimado = calcularConsumo(tipo, motor, combustivel, uso)
        val resumoTexto = "$tipo $motor - $combustivel ($uso)"

        val resultIntent = Intent()
        resultIntent.putExtra("CONSUMO_ESCOLHIDO", consumoEstimado)
        resultIntent.putExtra("RESUMO_ESCOLHA", resumoTexto)

        // Devolvemos também os dados crus para a MainActivity poder salvar e reutilizar depois
        resultIntent.putExtra("EXTRA_TIPO", tipo)
        resultIntent.putExtra("EXTRA_MOTOR", motor)
        resultIntent.putExtra("EXTRA_COMBUSTIVEL", combustivel)
        resultIntent.putExtra("EXTRA_USO", uso)

        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    // Função auxiliar para selecionar texto no Spinner evitando Warnings
    private fun selecionarItemSpinner(spinner: Spinner, valor: String) {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString() == valor) {
                spinner.setSelection(i)
                break
            }
        }
    }

    // --- COMPANION OBJECT: Lógica movida para cá para facilitar Testes Unitários ---
    companion object {
        fun calcularConsumo(tipo: String, motor: String, comb: String, uso: String): Double {
            if (tipo == "Moto") {
                var consumoBase = when (motor) {
                    "150cc-500cc" -> if (uso == "Cidade") 30.0 else 26.0
                    "600cc-750cc" -> if (uso == "Cidade") 19.0 else 22.0
                    "950cc+"      -> if (uso == "Cidade") 14.0 else 17.0
                    else -> 25.0
                }
                if (comb == "Gasolina Premium") {
                    consumoBase = when (motor) {
                        "150cc-500cc" -> consumoBase
                        "600cc-750cc", "950cc+" -> consumoBase * 1.05
                        else -> consumoBase
                    }
                }
                return consumoBase
            }

            if (tipo == "Carro") {
                if (comb == "Gasolina") {
                    return when (motor) {
                        "1.0"  -> if (uso == "Cidade") 13.0 else 16.0
                        "1.3"  -> if (uso == "Cidade") 11.5 else 14.5
                        "1.6"  -> if (uso == "Cidade") 10.0 else 13.0
                        "2.0+" -> if (uso == "Cidade") 8.5  else 11.0
                        else -> 10.0
                    }
                } else { // Etanol
                    return when (motor) {
                        "1.0"  -> if (uso == "Cidade") 9.5  else 11.5
                        "1.3"  -> if (uso == "Cidade") 8.0  else 10.0
                        "1.6"  -> if (uso == "Cidade") 7.0  else 9.0
                        "2.0+" -> if (uso == "Cidade") 5.5  else 7.5
                        else -> 7.0
                    }
                }
            }
            return 0.0
        }
    }
}
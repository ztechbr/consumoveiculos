package io.ztech.apppostogasolina

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // Componentes da Interface
    private lateinit var edtConsumo1: EditText
    private lateinit var edtConsumo2: EditText
    private lateinit var edtPreco1: EditText
    private lateinit var edtPreco2: EditText
    private lateinit var txtResultado: TextView
    private lateinit var txtResumo1: TextView
    private lateinit var txtResumo2: TextView
    private lateinit var btnBuscar2: Button

    // Variável para saber qual botão foi clicado (1 ou 2)
    private var campoParaPreencher: Int = 0

    // --- VARIÁVEIS DE MEMÓRIA ---
    private var ultimoTipo: String? = null
    private var ultimoMotor: String? = null
    private var ultimoUso: String? = null
    private var ultimoCombustivel: String? = null

    // Configuração para receber o resultado da tela de lista
    private val getConsumo = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data

            val consumoRetornado = data?.getDoubleExtra("CONSUMO_ESCOLHIDO", 0.0) ?: 0.0

            val resumoTexto = data?.getStringExtra("RESUMO_ESCOLHA") ?: getString(R.string.text_personalizado)

            ultimoTipo = data?.getStringExtra("EXTRA_TIPO")
            ultimoMotor = data?.getStringExtra("EXTRA_MOTOR")
            ultimoUso = data?.getStringExtra("EXTRA_USO")
            ultimoCombustivel = data?.getStringExtra("EXTRA_COMBUSTIVEL")

            if (campoParaPreencher == 1) {
                edtConsumo1.setText(consumoRetornado.toString())
                txtResumo1.text = resumoTexto
                btnBuscar2.isEnabled = true
            } else if (campoParaPreencher == 2) {
                edtConsumo2.setText(consumoRetornado.toString())
                txtResumo2.text = resumoTexto
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Vinculando IDs
        edtConsumo1 = findViewById(R.id.edtConsumo1)
        edtConsumo2 = findViewById(R.id.edtConsumo2)
        edtPreco1 = findViewById(R.id.edtPreco1)
        edtPreco2 = findViewById(R.id.edtPreco2)
        txtResultado = findViewById(R.id.txtResultado)

        txtResumo1 = findViewById(R.id.txtResumo1)
        txtResumo2 = findViewById(R.id.txtResumo2)

        val btnBuscar1 = findViewById<Button>(R.id.btnBuscar1)
        btnBuscar2 = findViewById<Button>(R.id.btnBuscar2)
        val btnCalcular = findViewById<Button>(R.id.btnCalcular)

        val btnLimpar = findViewById<Button>(R.id.btnLimpar)

        btnBuscar2.isEnabled = false

        btnBuscar1.setOnClickListener {
            campoParaPreencher = 1
            abrirListaCombustiveis()
        }

        btnBuscar2.setOnClickListener {
            campoParaPreencher = 2
            abrirListaCombustiveis()
        }

        btnCalcular.setOnClickListener {
            calcularMelhorOpcao()
        }

        btnLimpar.setOnClickListener {
            edtConsumo1.setText("")
            edtConsumo2.setText("")
            edtPreco1.setText("")
            edtPreco2.setText("")

            txtResumo1.setText(R.string.text_nenhuma_selecao)
            txtResumo2.setText(R.string.text_nenhuma_selecao)
            txtResultado.setText(R.string.text_resultado_padrao)

            txtResultado.setTextColor(Color.BLACK)

            btnBuscar2.isEnabled = false

            ultimoTipo = null
            ultimoMotor = null
            ultimoUso = null
            ultimoCombustivel = null

            Toast.makeText(this, R.string.msg_campos_limpos, Toast.LENGTH_SHORT).show()
        }
    }

    private fun abrirListaCombustiveis() {
        val intent = Intent(this, ListaCombustivelActivity::class.java)

        if (ultimoTipo != null) {
            intent.putExtra("EXTRA_TIPO", ultimoTipo)
            intent.putExtra("EXTRA_MOTOR", ultimoMotor)
            intent.putExtra("EXTRA_USO", ultimoUso)
            intent.putExtra("EXTRA_COMBUSTIVEL", ultimoCombustivel)
        }

        getConsumo.launch(intent)
    }

    private fun calcularMelhorOpcao() {
        val sConsumo1 = edtConsumo1.text.toString()
        val sConsumo2 = edtConsumo2.text.toString()
        val sPreco1 = edtPreco1.text.toString()
        val sPreco2 = edtPreco2.text.toString()

        if (sConsumo1.isEmpty() || sConsumo2.isEmpty() || sPreco1.isEmpty() || sPreco2.isEmpty()) {
            Toast.makeText(this, R.string.msg_erro_preencha, Toast.LENGTH_SHORT).show()
            return
        }

        val consumo1 = sConsumo1.toDouble()
        val consumo2 = sConsumo2.toDouble()
        val preco1 = sPreco1.toDouble()
        val preco2 = sPreco2.toDouble()

        if (consumo1 == 0.0 || consumo2 == 0.0) {
            Toast.makeText(this, R.string.msg_erro_consumo_zero, Toast.LENGTH_SHORT).show()
            return
        }

        val custoKm1 = preco1 / consumo1
        val custoKm2 = preco2 / consumo2

//        val resumo1 = txtResumo1.text.toString().substringBefore(" - ")
//        val resumo2 = txtResumo2.text.toString().substringBefore(" - ")

        val resumo1 = txtResumo1.text.toString().replace(" ", "")
        val resumo2 = txtResumo2.text.toString().replace(" ", "")

        if (custoKm1 < custoKm2) {
            val economia = ((1 - (custoKm1 / custoKm2)) * 100).toInt()
            // Concatenando a string manualmente para formar a frase completa
            txtResultado.text = "A opção 1 $resumo1 é $economia% mais econômico que o $resumo2."
            txtResultado.setTextColor(getColor(android.R.color.holo_green_dark))

        } else if (custoKm2 < custoKm1) {
            val economia = ((1 - (custoKm2 / custoKm1)) * 100).toInt()
            // Concatenando a string manualmente para formar a frase completa
            txtResultado.text = "A opção 2 $resumo2 é $economia% mais econômico que o $resumo1."
            txtResultado.setTextColor(getColor(android.R.color.holo_orange_dark))

        } else {
            // Para o caso de empate, podemos continuar usando o recurso de string, pois não há argumentos.
            txtResultado.text = getString(R.string.result_comb_empate)
            txtResultado.setTextColor(Color.BLUE)
        }

    }
}
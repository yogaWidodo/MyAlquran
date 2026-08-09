package com.expert.myalquran.presentation.vida

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.expert.myalquran.BuildConfig
import com.expert.myalquran.core.utils.Constants.BASE_URL_VIDA
import com.expert.myalquran.core.utils.DataStatus
import com.expert.myalquran.databinding.ActivityVidaBinding
import com.expert.myalquran.domain.vida.model.LivenessDecisionRequest
import com.expert.myalquran.domain.vida.model.LivenessDecisionResponse
import org.koin.android.ext.android.inject

/**
 * Layar latihan alur VIDA: CAPTURE -> SUBMIT -> DECISION -> CALLBACK.
 *
 * Tidak ada kamera dan tidak ada SDK asli. Tahap capture disimulasikan oleh
 * [DummyLivenessCapture]; sisanya adalah alur sungguhan lewat Retrofit ke stub
 * backend di tools/liveness-stub-server.py.
 *
 * Jalur error ditangani TERPISAH dari happy path - kartu sendiri dengan tombol
 * coba lagi, bukan Toast yang langsung hilang seperti di layar lain repo ini.
 */
class VidaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVidaBinding
    private val viewModel by inject<VidaViewModel>()

    /** Payload capture terakhir, dipakai untuk menampilkan skor di kartu hasil. */
    private var lastCapture: LivenessDecisionRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityVidaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyInsets()

        showEndpointAndCredentialStatus()

        binding.btnSubmit.setOnClickListener { startFlow(forceServerError = false) }
        binding.btnSubmitError.setOnClickListener { startFlow(forceServerError = true) }
        binding.btnRetry.setOnClickListener { viewModel.retryLast() }

        observeDecision()
    }

    // --- Tahap 1: CAPTURE -------------------------------------------------

    private fun startFlow(forceServerError: Boolean) {
        val request = DummyLivenessCapture.capture(forceServerError)
        lastCapture = request
        // --- Tahap 2: SUBMIT ---
        viewModel.submit(request)
    }

    // --- Tahap 3: DECISION ------------------------------------------------

    private fun observeDecision() {
        viewModel.decision.observe(this) { state ->
            when (state.status) {
                DataStatus.Status.LOADING -> renderLoading()
                DataStatus.Status.SUCCESS -> {
                    val body = state.data
                    if (body == null) {
                        // Sukses tapi body kosong tetap dianggap error, bukan
                        // happy path dengan layar melompong.
                        renderError(getString(com.expert.myalquran.R.string.vida_error_empty_body))
                    } else {
                        renderDecision(body)
                        onDecisionCallback(body)
                    }
                }

                DataStatus.Status.ERROR -> renderError(
                    state.message ?: getString(com.expert.myalquran.R.string.vida_error_unknown)
                )
            }
        }
    }

    // --- Tahap 4: CALLBACK ------------------------------------------------

    /**
     * Titik tempat aplikasi induk melanjutkan atau membatalkan alurnya.
     * Di project kantor, di sinilah transaksi diteruskan, dibatalkan, atau
     * dilempar ke antrean review manual.
     */
    private fun onDecisionCallback(decision: LivenessDecisionResponse) {
        when (decision.action?.uppercase()) {
            "ALLOW" -> Log.i(TAG, "callback: lanjutkan transaksi")
            "BLOCK" -> Log.i(TAG, "callback: batalkan transaksi")
            "REVIEW" -> Log.i(TAG, "callback: teruskan ke review manual")
            else -> Log.w(TAG, "callback: action tidak dikenal='${decision.action}'")
        }
    }

    // --- Render -----------------------------------------------------------

    private fun renderLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.layoutResult.visibility = View.GONE
        binding.layoutError.visibility = View.GONE
        setButtonsEnabled(false)
    }

    private fun renderDecision(decision: LivenessDecisionResponse) {
        binding.progressBar.visibility = View.GONE
        binding.layoutError.visibility = View.GONE
        binding.layoutResult.visibility = View.VISIBLE
        setButtonsEnabled(true)

        val action = decision.action?.uppercase().orEmpty()
        binding.tvAction.text = action.ifEmpty { "?" }
        binding.tvAction.setTextColor(colorFor(action))
        binding.tvReason.text = decision.reason.orEmpty()
        binding.tvMessage.text = decision.message.orEmpty()

        lastCapture?.let {
            binding.tvPayload.text = getString(
                com.expert.myalquran.R.string.vida_payload_summary,
                it.livenessScore,
                it.manipulationScore,
                it.userId
            )
        }
    }

    private fun renderError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.layoutResult.visibility = View.GONE
        binding.layoutError.visibility = View.VISIBLE
        binding.tvErrorMessage.text = message
        setButtonsEnabled(true)
    }

    private fun setButtonsEnabled(enabled: Boolean) {
        binding.btnSubmit.isEnabled = enabled
        binding.btnSubmitError.isEnabled = enabled
        binding.btnRetry.isEnabled = enabled
    }

    private fun colorFor(action: String): Int = when (action) {
        "ALLOW" -> Color.parseColor("#2E7D32")
        "BLOCK" -> Color.parseColor("#C62828")
        "REVIEW" -> Color.parseColor("#EF6C00")
        else -> Color.parseColor("#616161")
    }

    /**
     * Menampilkan credential hasil Fase 1 tanpa pernah mencetak nilainya -
     * hanya ada/tidak dan panjangnya, disiplin yang sama seperti di CI.
     */
    private fun showEndpointAndCredentialStatus() {
        binding.tvEndpoint.text = getString(
            com.expert.myalquran.R.string.vida_endpoint,
            "${BASE_URL_VIDA}liveness/decision"
        )
        binding.tvCredential.text = listOf(
            "API_KEY_VIDA" to BuildConfig.API_KEY_VIDA,
            "LICENSE_KEY_VIDA" to BuildConfig.LICENSE_KEY_VIDA,
        ).joinToString("\n") { (name, value) ->
            val status = if (value.isNotEmpty()) "OK (len=${value.length})" else "MISS"
            "BuildConfig.$name : $status"
        } + "\nflavor: ${BuildConfig.FLAVOR}"
    }

    private fun applyInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.vidaRoot) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }

    companion object {
        private const val TAG = "VidaActivity"
    }
}

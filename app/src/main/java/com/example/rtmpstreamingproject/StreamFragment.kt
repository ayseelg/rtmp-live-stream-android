package com.example.rtmpstreamingproject

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.rtmplibrary.data.datasource.StreamDataSource
import com.example.rtmplibrary.data.repository.StreamRepositoryImpl
import com.example.rtmplibrary.domain.model.StreamState
import com.example.rtmplibrary.domain.usecase.InitCameraUseCase
import com.example.rtmplibrary.domain.usecase.ObserveStreamStateUseCase
import com.example.rtmplibrary.domain.usecase.StartPreviewUseCase
import com.example.rtmplibrary.domain.usecase.StartStreamUseCase
import com.example.rtmplibrary.domain.usecase.StopPreviewUseCase
import com.example.rtmplibrary.domain.usecase.StopStreamUseCase
import com.example.rtmplibrary.domain.usecase.SwitchCameraUseCase
import com.example.rtmplibrary.presentation.viewmodel.StreamViewModel
import com.pedro.library.view.OpenGlView
import kotlinx.coroutines.flow.collectLatest

class StreamFragment : Fragment() {

    private lateinit var openGlView: OpenGlView
    private lateinit var btnStartStop: Button
    private lateinit var btnEndStream: Button
    private lateinit var btnSwitchCamera: Button
    private lateinit var btnClose: Button
    private lateinit var etRtmpUrl: EditText
    private lateinit var etStreamKey: EditText
    private lateinit var liveStatusCard: CardView
    private lateinit var viewerCountCard: CardView
    private lateinit var tvStatus: TextView
    private lateinit var controlsContainer: View

    private val viewModel: StreamViewModel by lazy {
        val dataSource = StreamDataSource()
        val repository = StreamRepositoryImpl(dataSource)
        StreamViewModel(
            ObserveStreamStateUseCase(repository),
            StartStreamUseCase(repository),
            StopStreamUseCase(repository),
            InitCameraUseCase(repository),
            StartPreviewUseCase(repository),
            StopPreviewUseCase(repository),
            SwitchCameraUseCase(repository)
        )
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            startStreaming()
        } else {
            Toast.makeText(requireContext(), "Kamera ve mikrofon izni gerekli", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_stream, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        openGlView = view.findViewById(R.id.openGlView)
        btnStartStop = view.findViewById(R.id.btnStartStop)
        btnEndStream = view.findViewById(R.id.btnEndStream)
        btnSwitchCamera = view.findViewById(R.id.btnSwitchCamera)
        btnClose = view.findViewById(R.id.btnClose)
        etRtmpUrl = view.findViewById(R.id.etRtmpUrl)
        etStreamKey = view.findViewById(R.id.etStreamKey)
        liveStatusCard = view.findViewById(R.id.liveStatusCard)
        viewerCountCard = view.findViewById(R.id.viewerCountCard)
        tvStatus = view.findViewById(R.id.tvStatus)
        controlsContainer = view.findViewById(R.id.controlsContainer)

        etRtmpUrl.setText("rtmp://10.0.2.2:1935/stream")
        etStreamKey.setText("test")

        // RtmpCamera2 artik data katmaninda; openGlView buradan iletiliyor
        viewModel.initCamera(openGlView)

        openGlView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                val hasCam = ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
                if (hasCam && !viewModel.isStreaming) {
                    viewModel.startPreview()
                }
            }
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
            override fun surfaceDestroyed(holder: SurfaceHolder) {
                viewModel.stopStream()
            }
        })

        btnStartStop.setOnClickListener {
            val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
            val allGranted = permissions.all {
                ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
            }
            if (allGranted) startStreaming() else permissionLauncher.launch(permissions)
        }

        btnEndStream.setOnClickListener {
            viewModel.stopStream()
        }

        btnSwitchCamera.setOnClickListener {
            viewModel.switchCamera()
        }

        btnClose.setOnClickListener {
            requireActivity().finish()
        }

        @Suppress("DEPRECATION")
        lifecycleScope.launchWhenStarted {
            viewModel.streamState.collectLatest { state ->
                when (state) {
                    is StreamState.Idle, is StreamState.Stopped -> {
                        btnStartStop.isEnabled = true
                        controlsContainer.visibility = View.VISIBLE
                        btnEndStream.visibility = View.GONE
                        liveStatusCard.visibility = View.GONE
                        viewerCountCard.visibility = View.GONE
                        tvStatus.visibility = View.GONE
                    }
                    is StreamState.Error -> {
                        btnStartStop.isEnabled = true
                        controlsContainer.visibility = View.VISIBLE
                        btnEndStream.visibility = View.GONE
                        liveStatusCard.visibility = View.GONE
                        viewerCountCard.visibility = View.GONE
                        tvStatus.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    }
                    is StreamState.Connecting -> {
                        btnStartStop.isEnabled = false
                        showStatus("Baglanıyor...")
                    }
                    is StreamState.Streaming -> {
                        btnStartStop.isEnabled = false
                        controlsContainer.visibility = View.GONE
                        btnEndStream.visibility = View.VISIBLE
                        liveStatusCard.visibility = View.VISIBLE
                        viewerCountCard.visibility = View.VISIBLE
                        tvStatus.visibility = View.GONE
                    }
                    null -> {}
                }
            }
        }
    }

    private fun showStatus(message: String) {
        tvStatus.text = message
        tvStatus.visibility = View.VISIBLE
    }

    private fun startStreaming() {
        if (viewModel.isStreaming) return
        val baseUrl = etRtmpUrl.text.toString().trim().trimEnd('/')
        val streamKey = etStreamKey.text.toString().trim()
        if (baseUrl.isEmpty()) {
            Toast.makeText(requireContext(), "Sunucu URL giriniz", Toast.LENGTH_SHORT).show()
            return
        }
        if (streamKey.isEmpty()) {
            Toast.makeText(requireContext(), "Yayin anahtari giriniz", Toast.LENGTH_SHORT).show()
            return
        }
        val url = "$baseUrl/$streamKey"
        // Tum kamera islemleri data katmaninda; sadece URL iletiliyor
        viewModel.startStream(url)
    }

    override fun onResume() {
        super.onResume()
        // Preview is started inside SurfaceHolder.Callback.surfaceCreated
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopStream()
    }
}
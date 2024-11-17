package com.example.lottonumbergenerator

import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.lottonumbergenerator.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Size
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonGenerate.setOnClickListener {
            binding.lottoBallContainer.removeAllViews()

            playConfetti()

            val numbers = generateLottoNumbers()
            val bonusNumber = generateBonusNumber(numbers)

            numbers.forEach { number ->
                val ball = createLottoBall(number)
                binding.lottoBallContainer.addView(ball)
            }

            val bonusBall = createBonusBall(bonusNumber)
            binding.lottoBallContainer.addView(bonusBall)
        }
    }

    private fun playConfetti() {
        binding.buttonGenerate.visibility = View.GONE

        binding.konfettiView.apply {
            visibility = View.VISIBLE
            start(
                Party(
                    speed = 10f,
                    maxSpeed = 50f,
                    damping = 0.9f,
                    spread = 360,
                    colors = listOf(Color.RED, Color.YELLOW, Color.BLUE, Color.GREEN, Color.MAGENTA),
                    size = listOf(Size(12)),
                    timeToLive = 3000L, // 파티클 유지 시간
                    fadeOutEnabled = true,
                    position = Position.Relative(0.5, 0.5),
                    emitter = Emitter(duration = 1000, TimeUnit.MILLISECONDS).perSecond(500)
                )
            )

            // Konfetti가 종료된 후 버튼 다시 표시
            observeConfettiEnd()
        }
    }

    private fun observeConfettiEnd() {
        lifecycleScope.launch {
            while (binding.konfettiView.isActive()) {
                delay(500)
            }

            binding.konfettiView.visibility = View.GONE

            // 버튼 페이드인 애니메이션으로 다시 표시
            fadeInButton(binding.buttonGenerate)
        }
    }

    private fun fadeInButton(button: View) {
        button.apply {
            visibility = View.VISIBLE
            alpha = 0f
            ObjectAnimator.ofFloat(this, "alpha", 1f).apply {
                duration = 500
                start()
            }
        }
    }


    // 로또 번호 생성 (6개)
    private fun generateLottoNumbers(): List<Int> {
        return (1..45).shuffled().take(6).sorted()
    }

    // 보너스 번호 생성 (기존 번호 제외)
    private fun generateBonusNumber(excludeNumbers: List<Int>): Int {
        val remainingNumbers = (1..45).filterNot { it in excludeNumbers }
        return remainingNumbers.random()
    }

    // 일반 번호 공 생성
    private fun createLottoBall(number: Int): TextView {
        val textView = TextView(this).apply {
            text = number.toString()
            textSize = 14f
            gravity = Gravity.CENTER
            setTextColor(Color.BLACK)
            setBackgroundResource(R.drawable.lotto_ball)
        }
        return textView
    }

    // 보너스 번호 공 생성
    private fun createBonusBall(number: Int): TextView {
        val textView = TextView(this).apply {
            text = number.toString()
            textSize = 14f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            setBackgroundResource(R.drawable.bonus_ball)
        }
        return textView
    }
}

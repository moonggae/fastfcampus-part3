package fastcampus.aop.part3.chapter03

import android.app.TimePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fastcampus.aop.part3.chapter03.databinding.ActivityMainBinding
import java.util.*
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //step0 뷰를 초기화해주기
        initOnOffButton()
        initChangeAlarmButton()

        //step1 데이터 가져오기
        //step2 뷰에 데이터를 그려주기
    }

    private fun initOnOffButton() {
        binding.onOffButton.setOnClickListener {
            // 데이터를 확인을 한다.

            // 온오프에 따라 작업을 처맇나다.

            // 오프 -> 알람 제거
            // 온 -> 알람을 등록

            // 데이터를 저장한다.
        }
    }

    private fun initChangeAlarmButton() {
        binding.changeAlarmTimeButton.setOnClickListener {
            // 현재시간을 일단 가져온다.
            // TimePickDialog 띄워줘서 시간을 설정을 하도록 하고, 그 시간을 가져와서

            val calendar = Calendar.getInstance()

            TimePickerDialog(
                this,
                R.layout.activity_main,
                { timePicker, hour, minute ->

                    val model = saveAlarmModel(hour, minute, false)

                    // 데이터를 저장한다.
                    // 뷰를 업데이트한다.
                    // 기존에 있던 알람을 삭제한다.
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            ).show()
        }
    }

    private fun saveAlarmModel(hour: Int, minute: Int, onOff: Boolean): AlarmDisplayModel {
        val model = AlarmDisplayModel(
            hour = hour,
            minute = minute,
            onOff = onOff
        )

        val sharedPreferences = getSharedPreferences(TIME_PREFERENCE_NAME, Context.MODE_PRIVATE)

        with(sharedPreferences.edit()) {
            putString(TIME_PREFERENCE_ALARM_KEY, model.makeDataForDB())
            putBoolean(TIME_PREFERENCE_ONOFF_KEY, model.onOff)
            commit() // commit or apply 차이는 Thread 점유 여부
        }

        return model
    }

    companion object {
        const val TIME_PREFERENCE_NAME = "time"
        const val TIME_PREFERENCE_ALARM_KEY = "alarm"
        const val TIME_PREFERENCE_ONOFF_KEY = "onOff"
    }

}
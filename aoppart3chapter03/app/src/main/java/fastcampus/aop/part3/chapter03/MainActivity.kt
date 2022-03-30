package fastcampus.aop.part3.chapter03

import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
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

        initOnOffButton()
        initChangeAlarmButton()

        val model = fetchDataFromSharedPreferences()
        renderView(model)
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
                    renderView(model)

                    val pendingIntent = PendingIntent.getBroadcast(
                        this,
                        ALARM_REQUEST_CODE,
                        Intent(this, AlarmReceiver::class.java),
                        PendingIntent.FLAG_NO_CREATE
                    )
                    pendingIntent?.cancel()
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

    private fun fetchDataFromSharedPreferences(): AlarmDisplayModel {
        val sharedPreferences = getSharedPreferences(TIME_PREFERENCE_NAME, Context.MODE_PRIVATE)

        val timeDBValue = sharedPreferences.getString(TIME_PREFERENCE_ALARM_KEY, "9:30") ?: "9:30"
        val onOffDBValue = sharedPreferences.getBoolean(TIME_PREFERENCE_ONOFF_KEY, false)
        val alarmData = timeDBValue.split(":")

        val alarmModel = AlarmDisplayModel(
            hour = alarmData[0].toInt(),
            minute = alarmData[1].toInt(),
            onOff = onOffDBValue
        )

        // 보정 보정 예외처리
        // PendingIntent.FLAG_NO_CREATE : 있으면 가져오고 없으면 Null
//        val pendingIntent = PendingIntent.getBroadcast(
//            this,
//            ALARM_REQUEST_CODE,
//            Intent(this, AlarmReceiver::class.java),
//            PendingIntent.FLAG_NO_CREATE
//        )
//        if ((pendingIntent == null) and alarmModel.onOff) {
//            // 알람은 꺼져있는데, 데이터는 켜져있는 경우
//            alarmModel.onOff = false
//
//        } else if ((pendingIntent != null) and alarmModel.onOff.not()) {
//            // 알람은 켜져있는데, 데이터는 꺼져있는 경우
//            // 알람을 취소함
//            pendingIntent.cancel()
//        }

        return alarmModel
    }


    private fun renderView(model: AlarmDisplayModel) {
        binding.amplTextView.apply {
            text = model.ampmText
        }

        binding.timeTextView.apply {
            text = model.timeText
        }

        binding.onOffButton.apply {
            text = model.onOffText
            tag = model
        }
    }


    companion object {
        private const val TIME_PREFERENCE_NAME = "time"
        private const val TIME_PREFERENCE_ALARM_KEY = "alarm"
        private const val TIME_PREFERENCE_ONOFF_KEY = "onOff"
        private const val ALARM_REQUEST_CODE = 1000
    }


}
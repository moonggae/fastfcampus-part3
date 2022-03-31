package fastcampus.aop.part3.chapter03

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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
    }


    private fun initOnOffButton() {
        binding.onOffButton.setOnClickListener {
            val model = it.tag as? AlarmDisplayModel ?: return@setOnClickListener
            val newModel = saveAlarmModel(model.hour, model.minute, model.onOff.not())
            renderView(newModel)

            if (newModel.onOff) {
                // 켜진 경우 -> 알람을 등록
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, newModel.hour)
                    set(Calendar.MINUTE, newModel.minute)

                    if (before(Calendar.getInstance())) {
                        add(Calendar.DATE, 1)
                    }
                }

                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(this, AlarmReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(
                    this,
                    ALARM_REQUEST_CODE,
                    intent,
                    PendingIntent.FLAG_MUTABLE
                )

                // doze모드에서 알람 사용하기
                // 잠자기 모드(doze) : https://developer.android.com/training/monitoring-device-state/doze-standby?hl=ko
                // setAndAllowWhileIdle : https://developer.android.com/reference/android/app/AlarmManager#setAndAllowWhileIdle(int,%20long,%20android.app.PendingIntent)
                // alarmManager.setAndAllowWhileIdle()
                // alarmManager.setExactAndAllowWhileIdle()


                // setInexactRepeating : 시간이 정확하지 않음음

                // ELAPSED_REALTIME_WAKEUP : 휴대폰 부팅된후 시간
                // RTC_WAKEUP : 절대시간
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )

                Toast.makeText(
                    this,
                    "알람을 등록했습니다. ${newModel.timeText}${newModel.ampmText}",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                // 꺼진 경우 -> 알람을 제거
                cancelAlarm()
            }
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
                    cancelAlarm()
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
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            ALARM_REQUEST_CODE,
            Intent(this, AlarmReceiver::class.java),
            PendingIntent.FLAG_MUTABLE
        )
        if ((pendingIntent == null) and alarmModel.onOff) {
            // 알람은 꺼져있는데, 데이터는 켜져있는 경우
            alarmModel.onOff = false

        } else if ((pendingIntent != null) and alarmModel.onOff.not()) {
            // 알람은 켜져있는데, 데이터는 꺼져있는 경우
            // 알람을 취소함
            pendingIntent.cancel()
        }

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

    private fun cancelAlarm() {
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            ALARM_REQUEST_CODE,
            Intent(this, AlarmReceiver::class.java),
            PendingIntent.FLAG_MUTABLE
        )
        pendingIntent?.cancel()
        Toast.makeText(this, "알람을 삭제했습니다.", Toast.LENGTH_SHORT).show()
    }


    companion object {
        private const val TIME_PREFERENCE_NAME = "time"
        private const val TIME_PREFERENCE_ALARM_KEY = "alarm"
        private const val TIME_PREFERENCE_ONOFF_KEY = "onOff"
        private const val ALARM_REQUEST_CODE = 1000
    }


}
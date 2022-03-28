package fastcampus.aop.part3.chapter01

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // 토큰은 변경 가능함
    // https://firebase.google.com/docs/cloud-messaging/android/first-message?hl=ko&authuser=0
    // 새 토큰이 생성될 때 onNewToken 콜백이 호출됨
    override fun onNewToken(token: String) {
        super.onNewToken(token)

    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
    }

}
# Chapter01 - Notification 기능 구현하기

##  메시지 전송 테스트

- Firebase Android Gradle SDK 셋업
- Firebase 프로젝트 셋업
- `OnCompleteListener`를 이용해 token 값 받아오기
```kotlin
FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
    if(task.isSuccessful){
        val token = task.result
        binding.firebaseTokenTextView.text = token
    }
}
```

- [메시지 전송 테스트 페이지](https://console.firebase.google.com/project/aop-part3-chapter01-25e62/notification/compose)에서 테스트하기
![테스트 형식](./resources/try_notification.png)


## FirebaseMessagingService
- `Manifest`에 서비스 등록
```xml
<service
    android:name=".MyFirebaseMessagingService"
    android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
    </intent-filter>
</service>
```
- `FirebaseMessagingService` 서비스 등록을 통해 메시지와 토큰을 처리한다.
- `onNewToken` 오버라이드 함수를 통해 토큰이 변경 됐을 때 이벤트를 처리한다. ([토큰이 변경되는 경우](https://firebase.google.com/docs/cloud-messaging/android/first-message?hl=ko&authuser=0#access_the_registration_token))
- `onMessageReceived` 오버라이드 함수를 통해 메시지를 받았을 때 이벤트를 처리한다.
- `onMessageReceived`함수 내에 중단점을 걸고 [메시지 전송 테스트 페이지](https://firebase.google.com/docs/reference/fcm/rest/v1/projects.messages/send)에서 테스트 메시지 전송하기
![테스트 형식](./resources/try_send_message.png)


## [Channel 설정](https://developer.android.com/training/notify-user/channels?hl=ko)
- Android 8.0(API 수준 26) 부터 알람 채널 할당
- Channel ID, Channel Name, Channel Description, [Channel Importance](https://developer.android.com/training/notify-user/channels?hl=ko#importance) 설정 필요
```kotlin
private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = CHANNEL_DESCRIPTION

        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)
    }
}
```

## Notification 생성
- Build 패턴으로 생성 
```kotlin
NotificationCompat.Builder(this, CHANNEL_ID)
    .setSmallIcon(R.drawable.ic_baseline_circle_notifications_24)
    .setContentTitle(title)
    .setContentText(message)
    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    .setContentIntent(pendingIntent)
    .setAutoCancel(true)
    .build()
```
- 필요에 따라 레이아웃 설정 변경
    - Normal, Expandable, Custom 등
    - Custom Layout 사용시 [RemoteView](https://developer.android.com/reference/android/widget/RemoteViews) 사용
    - enum 클래스로 분리하여 사용
- [PendingIntent](https://developer.android.com/reference/android/app/PendingIntent)를 사용하여 Notification 클릭시 이동하는 액티비티 지정 가능


## Intent Flags
- 기존의 Activity를 쌓는 방식은 Activity를 쌓는 스택 방식
- Intent Flag를 통해 제어 가능

<img src="./resources/activity_flag1.png" width="40%" title="standard" alt="standard" />


- FLAG_ACTIVITY_SINGLE_TOP

<img src="./resources/activity_flag2.png" width="40%" title="SINGLE_TOP" alt="SINGLE_TOP" />

- 호출한 Activity의 `override fun onNewIntent(intent: Intent?)` 함수 호출


# Chapter02 - 오늘의 명언

## Firebase Remote Config
- firebase 콘솔에서 remote config를 수정하는 것만으도로 이미 지정된 코드를 통해 앱을 재 출시할 필요 없이 변경할 수 있다.
- use cases
    - 배포된 어플의 일정 유저에게만 새로운 기능 출시하기
    - 지역, 언어별 문구나 이미지 등 원격 수정
    - 제한된 그룹에게 새로운 기능 테스트
- [Throttling](https://firebase.google.com/docs/remote-config/get-started?hl=en&authuser=0&platform=android#throttling)
    - 개발환경에서는 짧은 시간 간격으로 페치를 진행 할 수 있지만, 실제 배포 환경에서는 자주 페치를 수행하면 Throttling이 걸릴 수 있다.
    - `minimumFetchIntervalInSeconds` 속성을통해 최소 페치 인터벌을 12시간으로 제한해야한다.
- `fetch`, `activate`로 구성하여 사용
```kotlin
remoteConfig.fetchAndActivate().addOnCompleteListener {
    if (it.isSuccessful) {
        val quotes = parseQuotesJson(remoteConfig.getString("quotes"))
        val isNameRevealed = remoteConfig.getBoolean("is_name_revealed")
    }
}
```
![remote config value](./resources/remote_config.png)

## ViewPager2
- `ViewHolder Pattern`으로 구현
- 아이템 개수 무한으로 늘리기
    - 아이템 개수를 int max 값으로 설정 
    ```kotlin
    override fun getItemCount() = Int.MAX_VALUE
    ```
    - `onBindViewHolder`함수에서 bind하는 아이템을 조정
    ```kotlin
    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        val actualPosition = position % quotes.size // position이 itemList 범위를 벗어나도 다시 0부터 시작한다.
        holder.bind(quotes[actualPosition], isNameRevealed)
    }
    ```
    - adapter 바인딩 직후 현재 Item을 ItemCount의 중간에 있는 position에 해당하는 Item으로 설정하여 양 옆으로 모두 이동이 가능하도록 설정한다.
    ```kotlin
    binding.viewPager.adapter = adapter
    binding.viewPager.setCurrentItem(adapter.itemCount / 2 - 1 , false)
    ```
- 아이템 자연스럽게 전환하기
    - `setPageTransformer`함수를 통해 페이지 전환시 이벤트를 설정한다
    - Item의 Position이 0에 가까워 질수록 alpha 값을 1로 수렴하게 한다
    ```kotlin
    binding.viewPager.setPageTransformer { page, position ->
        when{
            position.absoluteValue >= 1F -> {
                page.alpha = 0F
            }

            position == 0F -> {
                page.alpha = 1F
            }

            else -> {
                page.alpha = 1F - 2 * position.absoluteValue
            }
        }
    }
    ```

# Chapter03 - 알람 앱

## kotlin format chatacter
- 숫자 자리수를 지정해줄 때 유용하다
    - %d : 정수값 지정
    - %f : 소수값 지정 (.2f 의미는 소수점 기준으로 하위 2자리까지 출력)
    -  %s : 문자열값 지정
```kotlin 
val str = "%02d, %.2f".format(4, 10) // 04, 10.00
```

## [Shared Preferences](https://developer.android.com/training/data-storage/shared-preferences)
- key-value 쌍을 저장할 때 사용한다.
- name, key를 지정해놓고 사용해야 하는데 `companion object`에 const로 할당해서 사용하는게 좋다.
- 여러 데이터를 저장할 때에는 Json 형식으로 사용하면 편하다.
- Pregerences를 가져 올때 `MODE_PRIVATE` 모드로 가져오는데 다른앱에서 사용하지 못하게 한다. 퍼블릭으로 사용하는 방법은 deprecated
- 에디터 저장시에 `commit()`, `apply()` 2가지 방법이 있다.
    - `commit() : boolean` : 쓰레드를 블록시키며 실행 결과값을 boolean으로 리턴한다
    - `apply() : Unit` : 쓰레드를 블록시키지 않는다.

## Background 작업
- Immediate tasks (즉시 실행해야하는 작업)
    - Thread
    - Handler
    - Kotlin coroutines
- Deferred tasks (지연된 작업)
    - WorkManager
- Exact tasks (정시에 실행해야 하는 작업)
    - AlarmManager

## [AlarmManager](https://developer.android.com/training/scheduling/alarms)
- 원하는 시간에 이벤트를 발생시킬 수 있음
    - RTC_WAKEUP : 절대시간 (absolute time)
    - ELAPSED_REALTIME_WAKEUP : 휴대폰이 부팅 된 이후 지난 시간
- 알람을 설정 할 때 `setRepeating()` 대신 `setInexactRepeating()`을 사용하면 자원을 줄일 수 있다. 하지만 시간 정확도는 떨어진다.
- doze모드에서 사용이 필요하면 `setAndAllowWhileIdle(), setExactAndAllowWhileIdle()`을 사용하면 된다.


# Chapter04 - 도서 리뷰 앱
## Retrofit2
- Restful API 호출하여 사용하는 라이브러리
- gson과 함께 사용하면 모델을 편하게 사용할 수 있음
- 모델생성시 `@SerializedName` 어노테이션을 사용해서 json object키와 필드 이름을 다르게 지정할 수 있음
- response json 최상단에 모델값이 없더라도 Dto 클래스를 사용해서 node를 맞추어 사용하면 따로 파싱할 필요 없음
```kotlin
data class SearchBookDto (
    @SerializedName("title") val title : String,
    @SerializedName("item") val books : List<Book>,
)
```

## Room
- 내장 DB를 사용하는 라이브러리
- 어노테이션 이용
    - Entity : 테이블
    - PrimaryKey : 기본키
    - ColumnInfo : 컬럼 정보
```kotlin
@Entity
data class History(
    @PrimaryKey val uid : Int?,
    @ColumnInfo(name = "keyword") val keyword : String?
)
```
- 인터페이스를 이용해 Dao를 만들고 쿼리를 작성함
```kotlin
@Dao
interface HistoryDao {
    @Query("SELECT * FROM history")
    fun getAll() : List<History>

    @Insert
    fun insertHistory(history: History)

    @Query("DELETE FROM history WHERE keyword = :keyword")
    fun delete(keyword : String)
}
```
- 테이블이 추가되거나 변경 될 때는 db버전을 올리고, 마이그레이션 코드를 직접 작성해서 진행함
```kotlin
fun getAppDatabase(context : Context) : AppDatabase{
    val migration_1_2 = object : Migration(1,2){
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE `REVIEW` (`id` INTEGER, `review` TEXT," + "PRIMARY KEY(`id`))")
        }
    }
    return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "BookSearchDB"
    )
        .addMigrations(migration_1_2)
        .build()
}

```


## RecyclerView
- Adapter안에 ViewHolder를 inner class로 작성해 이벤트 리스너를 파라미터롤 받아 사용할 수 있음
```kotlin
class HistoryAdapter(val historyDeleteClickedListener : (String) -> Unit) : ListAdapter<History, HistoryAdapter.HistoryItemViewHolder>(diffUtil){
    inner class HistoryItemViewHolder(private val binding : ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(historyModel : History) {
            binding.historyKeywordDeleteButton.setOnClickListener {
                historyDeleteClickedListener(historyModel.keyword.orEmpty())
            }
...
```
- `diffUtil`을 이용해 notifyDataSetChanged() 사용 하지않기
    - `notifyDataSetChanged()`를 사용하면 리스트를 모두 비우고 처음부터 다시 렌더링함
    - `diffUtil`를 사용하면 이전 데이터와 현재 데이터의 상태 차이를 계산하고 최소한의 데이터만 갱신한다.

## Glide
- 서버에 있는 이미지 URL을 가지고 있을 때 이미지를 편리하게 적용할 수 있다.
```kotlin
Glide.with(binding.coverImageView.context)
            .load(bookModel?.coverSmallUrl.orEmpty())
            .into(binding.coverImageView)
```

## EditText
- 아래 옵션을 통해 1줄만 작성하게함
```kotlin
android:inputType="text"
android:lines="1"
```
- 엔터 이벤트 적용하기
```kotlin
binding.searchEditText.setOnKeyListener { v, keyCode, event ->
    if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == MotionEvent.ACTION_DOWN) {
        search(binding.searchEditText.text.toString())
        return@setOnKeyListener true
    }
    return@setOnKeyListener false
}
```

# Chapter05 - 틴더 앱
## 로그인 상태 관리
- 어플 시작시 `MainActivity` 호출
- `MainActivity`에서 로그인 정보가 없으면 `LoginActivity`호출, 로그인 정보가 있으면 `LikeActivity`호출
- `LoginActivity`에서 로그인 성공하면 `finish()`

## Firebase - Authentication
- email, 전화번호, 익명, sns 계정 등 다양한 로그인 기능을 간편하게 이용 할 수 있게 지원함
- Instance
```kotlin
val auth : FirebaseAuth = FirebaseAuth.getInstance()
// or
val auth : FirebaseAuth = Firebase.auth
```
- Email 회원가입
```kotlin
auth.createUserWithEmailAndPassword(email, password)
    .addOnCompleteListener(this) { task ->
        if (task.isSuccessful) {
            ...
        } 
    }
```
- Email 로그인
```kotlin
auth.signInWithEmailAndPassword(email, password)
    .addOnCompleteListener(this) { task ->
        if (task.isSuccessful) {
            ...
        } 
    }
```

## Firebase - Realtime Database
- NoSQL DB
- key와 value로 이용
- DatabaseReference
```kotlin
private val userDB : DatabaseReference = Firebase.database.reference.child(USERS)
```
- Insert Value
```kotlin
userDB.child("keyName").setValue("value")
```
- Get Value
- `ValueEventListener - onDataChange()` : 하위 요소를 포함한 데이터가 변경 될 때 마다 호출됨
- `addListenerForSingleValueEvent` : 로컬 캐시 값을 이용하고 싶을 때 사용
```kotlin
val otherUserDB = userDB.child(getCurrentUserID()).child(LIKED_BY).child(LIKE).child(otherUserId)
otherUserDB.addListenerForSingleValueEvent(object : ValueEventListener {
    override fun onDataChange(snapshot: DataSnapshot) {
        if (snapshot.value == true) {
            ...
        }
    }

    override fun onCancelled(error: DatabaseError) {}
})
```
- `get()` : 데이터 한 번 읽기
```kotlin
mDatabase.child("users").child(userId).get().addOnSuccessListener {
    Log.i("firebase", "Got value ${it.value}")
}.addOnFailureListener{
    Log.e("firebase", "Error getting data", it)
}
```

## CardStackView
- 카드를 넘기는 이벤트
- `ViewHolder Pattern`으로 구현
```kotlin
private fun initCardStackView() {
    binding.cardStackView.layoutManager = CardStackLayoutManager(context = this, listner = this)
    binding.cardStackView.adapter = CardItemAdapter()
}
```

![card stack view](./resources/cardstack.gif)


# Chapter06 - 중고나라 앱
## BottomNavigationView
- 하단에 메뉴 버튼들을 위치시켜 Fragment 이동을 원할하게 만들어줌
![bottomNavigationView](./resources/bottomnavigationview.png)
- Fragment 이동
```kotlin
private fun initBottomNavigationView() {
    binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
        when(menuItem.itemId){
            R.id.home -> replaceFragment(homeFragment)
            R.id.chatList-> replaceFragment(charListFragment)
            R.id.myPage ->replaceFragment(myPageFragment)
        }
        true
    }
}

private fun replaceFragment(fragment : Fragment){
    supportFragmentManager.beginTransaction()
        .apply {
            replace(binding.fragmentContainer.id, fragment)
            commit()
        }
}
```

## Fragment
- `Activity`와 다르게 재활용이 가능한 View
- UI를 개별로 분할할 수 있도록 하여 `Activity` UI에 모듈성과 재사용성을 도입
- 독립적으로 존재할 수 없음
- life cycle

<img src="./resources/fragment-view-lifecycle.png" width="40%" title="Fragment life cycle" alt="Fragment life cycle" />

## Floating Button
![floating button](./resources/floating_button.png)


# Chapter07 - 에어비앤비

## [Naver Map API](https://navermaps.github.io/android-map-sdk/guide-ko/1.html)

## FrameLayout
- 여러개의 뷰(View) 위젯들을 중첩하고, 그 중 하나를 전면에 표시할 때 사용하는 레이아웃
- 액자 속 사진을 마음대로 빼고 넣고 하듯이, 경우에 따라 보여주고 싶은 화면을 자유자재로 스위칭 할 수 있도록 하는 것이 FrameLayout 사용 목적

## CoordinatorLayout
- View간의 상호작용을 처리하기 위한 View
- CoordinatorLayout이 Child View의 Behavior를 수신하여 다른 Child View에 Behavior를 전달
- Child View는 미리 정의된 Behavior를 사용하거나 새롭게 만든 Behavior를 사용하여 수신된 Behavior로 특정 작업을 수행

## BottomSheetBehavior
- `CoordinatorLayout`을 이용하여 구현
```xml
<!-- activity_main.xml -->
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
    ...

    <include
        android:id="@+id/bottomSheet"
        layout="@layout/bottom_sheet" />

    ...
```
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout 
    ...
    app:behavior_peekHeight="100dp"
    app:layout_behavior="com.google.android.material.bottomsheet.BottomSheetBehavior">

    ...

</androidx.constraintlayout.widget.ConstraintLayout>
```

## Glide
- `transform()`메소드로 커스텀
```kotlin
Glide.with(binding.thumbnailImageView)
    .load(houseModel.imgUrl)
    .transform(CenterCrop(), RoundedCorners(dpToPx(binding.thumbnailImageView.context, 12))) // centor 기준으로 꽉차게 이미지 확장, corder raidus 주기
    .into(binding.thumbnailImageView)
```

## Share (공유)
- intent 이용
```kotlin
val intent = Intent()
    .apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "[지금 이 가격에 예약하세요!!!] ${it.title} ${it.price} 사진보기 : ${it.imgUrl}")
        type = "text/plain"
    }
startActivity(Intent.createChooser(intent, null))
```



## theme 이용해서 status bar color를 window color와 통일 시키기
```xml
<!-- themes.xml -->
<resources xmlns:tools="http://schemas.android.com/tools">
    <style name="Theme.Aoppart3chapter07" parent="Theme.MaterialComponents.DayNight.NoActionBar">
        ...
        <item name="android:statusBarColor" tools:targetApi="l">?android:windowBackground</item>
        <item name="android:windowLightStatusBar">true</item>
        ...
    </style>
</resources>
```
```xml
<!-- themes.xml - night -->
<resources xmlns:tools="http://schemas.android.com/tools">
    <style name="Theme.Aoppart3chapter07" parent="Theme.MaterialComponents.DayNight.NoActionBar">
        ...
        <item name="android:statusBarColor" tools:targetApi="l">?android:windowBackground</item>
        ...
    </style>
</resources>
```
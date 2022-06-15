# The Bible In Ypu
<img src="https://lh3.googleusercontent.com/exaqHr4GnFwYbtBxEuBtNLG7aSWGK59p3CmlBsjzczWRpqFu-smbt3QCLmYB-hBwZKI" alt="image" width="512"/></br>
[_**Google Play**_](https://play.google.com/store/apps/details?id=com.duke.orca.android.kotlin.biblelockscreen)

# Preview
https://user-images.githubusercontent.com/80195353/173956917-c61016ac-1d9c-4dd5-9c0a-a300733a00b2.mp4

# Features
## 메인
- 플레이 옵션을 설정할 수 있습니다.
- 다른 화면으로 이동할 수 있는 화면입니다.

## 플레이
- 실질적으로 N-Back Training을 플레이하는 화면입니다.
- 플레이 방법은 현재 표시되는 숫자와 N 라운드 전에 표시되었던 숫자가 같으면 O 버튼을, 다르면 X 버튼을 누르는 것입니다.
- 상단에 현재 라운드가 표시되며, 가운데에는 숫자가 표시됩니다.

## 랭킹
- 3-Back 이상, 20 라운드 이상, 그리고 스피드 모드에서 모든 문제를 맞히면 랭킹 등록 조건이 충족됩니다.
- N과 라운드가 클수록, 경과시간이 짧을수록 높게 랭크됩니다.

## 기록
- 플레이 기록을 확인할 수 있으며, 삭제 기능을 지원합니다.

## 온보딩
- N-Back Training의 플레이 방법을 소개합니다.
- 앱을 처음 실행하면 즉시 표시됩니다.
- 메인의 '플레이 방법' 버튼을 클릭하여 진입할 수 있습니다.

# Tech Stack
- AAC (Android Architecture Components)
- Clean Architecture(Multi Modules)
- Kotlin Coroutine, Flow
- Jetpack Compose
- Hilt
- Room
- Firebase(Crashlytics, Firestore)

# Release Note
| version | log |
| --- | --- |
| 1 (1.0) | 첫 번째 버전 출시 |
| 2 (1.1) | 인앱 결제 추가 |
| 5 (1.2) | Firebase Crashlytics 추가 |

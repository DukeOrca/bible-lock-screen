package com.duke.orca.android.kotlin.biblelockscreen.billing.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// "구매" 만을 관리, 인앱 상품 로드 + 결제 시퀸스만 담당,
// 모듈 자체는 싱글턴 관리를 하고,, 쿼리만 다시 실행하는 것으로..
// 아님,, 콜백을 전달받아야함,, 리스터 교체는 지저분하니까. 걍 새로 커넥트 실행하는 걸로 ㄱ.
// 여기서 다시 인스턴스 얻을 것.
class BillingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }



//    private fun launchBillingFlow(skuDetails: SkuDetails) {
//        val billingFlowParams = BillingFlowParams.newBuilder()
//            .setSkuDetails(skuDetails)
//            .build()
//
//        val responseCode = billingClient.launchBillingFlow(this, billingFlowParams).responseCode
//    }
}
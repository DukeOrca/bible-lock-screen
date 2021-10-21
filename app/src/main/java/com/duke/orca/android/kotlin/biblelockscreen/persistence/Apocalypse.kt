package com.duke.orca.android.kotlin.biblelockscreen.persistence

import android.content.Context
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleVerse
import com.duke.orca.android.kotlin.biblelockscreen.persistence.database.BibleDatabase

object Apocalypse {
    private val words = arrayOf(
        "다시 밤이 없겠고 등불과 햇빛이 쓸 데 없으니 이는 주 하나님이 그들에게 비치심이라 그들이 세세토록 왕 노릇 하리로다",
        "또 그가 내게 말하기를 이 말은 신실하고 참된지라 주 곧 선지자들의 영의 하나님이 그의 종들에게 반드시 속히 되어질 일을 보이시려고 그의 천사를 보내셨도다",
        "보라 내가 속히 오리니 이 두루마리의 예언의 말씀을 지키는 자는 복이 있으리라 하더라",
        "이것들을 보고 들은 자는 나 요한이니 내가 듣고 볼 때에 이 일을 내게 보이던 천사의 발 앞에 경배하려고 엎드렸더니",
        "그가 내게 말하기를 나는 너와 네 형제 선지자들과 또 이 두루마리의 말을 지키는 자들과 함께 된 종이니 그리하지 말고 하나님께 경배하라 하더라",
        "또 내게 말하되 이 두루마리의 예언의 말씀을 인봉하지 말라 때가 가까우니라",
        "불의를 행하는 자는 그대로 불의를 행하고 더러운 자는 그대로 더럽고 의로운 자는 그대로 의를 행하고 거룩한 자는 그대로 거룩하게 하라",
        "보라 내가 속히 오리니 내가 줄 상이 내게 있어 각 사람에게 그가 행한 대로 갚아 주리라",
        "나는 알파와 오메가요 처음과 마지막이요 시작과 마침이라",
        "자기 두루마기를 빠는 자들은 복이 있으니 이는 그들이 생명나무에 나아가며 문들을 통하여 성에 들어갈 권세를 받으려 함이로다",
        "개들과 점술가들과 음행하는 자들과 살인자들과 우상 숭배자들과 및 거짓말을 좋아하며 지어내는 자는 다 성 밖에 있으리라",
        "나 예수는 교회들을 위하여 내 사자를 보내어 이것들을 너희에게 증언하게 하였노라 나는 다윗의 뿌리요 자손이니 곧 광명한 새벽 별이라 하시더라",
        "성령과 신부가 말씀하시기를 오라 하시는도다 듣는 자도 오라 할 것이요 목마른 자도 올 것이요 또 원하는 자는 값없이 생명수를 받으라 하시더라",
        "내가 이 두루마리의 예언의 말씀을 듣는 모든 사람에게 증언하노니 만일 누구든지 이것들 외에 더하면 하나님이 이 두루마리에 기록된 재앙들을 그에게 더하실 것이요",
        "만일 누구든지 이 두루마리의 예언의 말씀에서 제하여 버리면 하나님이 이 두루마리에 기록된 생명나무와 및 거룩한 성에 참여함을 제하여 버리시리라",
        "이것들을 증언하신 이가 이르시되 내가 진실로 속히 오리라 하시거늘 아멘 주 예수여 오시옵소서",
        "주 예수의 은혜가 모든 자들에게 있을지어다 아멘"
    )

    suspend fun insert(applicationContext: Context) {
        val bibleVerseDao = BibleDatabase.getInstance(applicationContext).bibleVerseDao()
        val bibleVerses = arrayListOf<BibleVerse>()

        words.forEachIndexed { index, word ->
            bibleVerses.add(
                BibleVerse(
                    id = 31085 + index,
                    book = 66,
                    chapter = 22,
                    verse = 5 + index,
                    word = word,
                    favorites = false
                )
            )
        }

        bibleVerseDao.insert(bibleVerses)
    }
}
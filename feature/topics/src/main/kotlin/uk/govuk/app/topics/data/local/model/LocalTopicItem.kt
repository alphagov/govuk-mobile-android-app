package uk.govuk.app.topics.data.local.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class LocalTopicItem : RealmObject {
    @PrimaryKey
    var ref: String = ""

    var title: String = ""
}
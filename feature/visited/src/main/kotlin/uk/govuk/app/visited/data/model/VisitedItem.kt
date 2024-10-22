package uk.govuk.app.visited.data.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

internal class VisitedItem: RealmObject {
    @PrimaryKey var _id: ObjectId = ObjectId()
    var title: String = ""
    var url: String = ""
    var lastVisited: String = "" // TODO: needs to be a date at this point
}

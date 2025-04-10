package uk.govuk.app.local.data.model

import io.realm.kotlin.types.RealmObject

class StoredLocalAuthority: RealmObject {
    var name: String = ""
    var url: String = ""
    var slug: String = ""
    var parentName: String? = ""
    var parentUrl: String? = ""
    var parentSlug: String? = ""
}

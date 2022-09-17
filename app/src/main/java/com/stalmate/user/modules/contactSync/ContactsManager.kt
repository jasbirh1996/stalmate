package com.stalmate.user.modules.contactSync

import android.content.ContentProviderOperation
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds
import com.stalmate.user.utilities.Constants


object ContactsManager {
    private const val MIMETYPE = "vnd.android.cursor.item/com.example.ajay.contacts_4"
    fun addContact(context: Context, contact: MyContact) {
        val resolver: ContentResolver = context.getContentResolver()
        val ops = ArrayList<ContentProviderOperation>()
        ops.add(
            ContentProviderOperation
                .newInsert(
                    addCallerIsSyncAdapterParameter(
                        ContactsContract.RawContacts.CONTENT_URI, true
                    )
                )
                .withValue(
                    ContactsContract.RawContacts.ACCOUNT_NAME,
                    Constants.ACCOUNT_NAME
                )
                .withValue(
                    ContactsContract.RawContacts.ACCOUNT_TYPE,
                    Constants.ACCOUNT_TYPE
                )
                .withValue(
                    ContactsContract.RawContacts.AGGREGATION_MODE,
                    ContactsContract.RawContacts.AGGREGATION_MODE_DEFAULT
                )
                .build()
        )
        ops.add(
            ContentProviderOperation
                .newInsert(
                    addCallerIsSyncAdapterParameter(
                        ContactsContract.Data.CONTENT_URI, true
                    )
                )
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(
                    ContactsContract.Data.MIMETYPE,
                    CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                )
                .withValue(
                    CommonDataKinds.StructuredName.DISPLAY_NAME,
                    contact.name
                )
                .build()
        )
        ops.add(
            ContentProviderOperation
                .newInsert(
                    addCallerIsSyncAdapterParameter(
                        ContactsContract.Data.CONTENT_URI, true
                    )
                )
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, MIMETYPE)
                .withValue(ContactsContract.Data.DATA1, 12345)
                .withValue(ContactsContract.Data.DATA2, "user")
                .withValue(ContactsContract.Data.DATA3, "MyData")
                .build()
        )
        try {
            resolver.applyBatch(ContactsContract.AUTHORITY, ops)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addCallerIsSyncAdapterParameter(
        uri: Uri,
        isSyncOperation: Boolean
    ): Uri {
        return if (isSyncOperation) {
            uri.buildUpon()
                .appendQueryParameter(
                    ContactsContract.CALLER_IS_SYNCADAPTER,
                    "true"
                ).build()
        } else uri
    }
}

data class MyContact(
    val id: String,
    val name: String,
    val numbers: ArrayList<String>,
    val rawContactIdMap: HashMap<String, String>
)

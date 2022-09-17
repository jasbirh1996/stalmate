package com.stalmate.user.modules.contactSync

import android.accounts.Account
import android.content.*
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import com.stalmate.user.utilities.Constants
import java.util.regex.Pattern


class SyncAdapter(context: Context, autoInitialize: Boolean): AbstractThreadedSyncAdapter(context, autoInitialize) {

    private val TAG: String = javaClass.simpleName
    private var mContactsList: ArrayList<MyContact> = ArrayList()
    private var serverNumberList: ArrayList<String> = ArrayList()

    init {
        Log.i(TAG, "SyncAdapter Created")
        mContactsList = getContactData()
    }

    override fun onPerformSync(
        account: Account?,
        extras: Bundle?,
        authority: String?,
        provider: ContentProviderClient?,
        syncResult: SyncResult?
    ) {
        Log.i(TAG, "SyncAdapter called")

        try {

            /*     for(i in 0 until jsonArray.length()) {
                     val jsonObject = JSONObject(jsonArray.get(i).toString())
                     serverNumberList.add(jsonObject.get("number").toString())
                 }*/

            for(contact in mContactsList) {
                for(number in contact.numbers) {
                    if(isNumberAlreadyRegistered(number)) {
                        // If number is registered and invalid on server, delete it
                        if(!serverNumberList.contains(getFormattedNumber(number))) {
                          //  ContactsManager.deleteNumber(context, number)
                        }
                    } else {
                        // If number is not registered and valid on server, register it
                        if(serverNumberList.contains(getFormattedNumber(number))) {
                         //   ContactsManager.registerNumber(context, number, contact.name, contact.rawContactIdMap)
                        }
                    }
                }
            }

            // send broadcast response for manual refresh request
            var stringnumbers=ArrayList<String>()
            var intentt=Intent()
            intentt.action=Constants.ACTION_SYNC_COMPLETED

            for (i in 0 until mContactsList.size){
                for (j in 0 until mContactsList[i].numbers.size){
                    var retrivedContact=mContactsList[i].numbers[j].toString().replace(" ", "");
                    stringnumbers.add(getPhoneNumberWithoutCountryCode(retrivedContact))
                }
            }
            val str: String = java.lang.String.join(",", stringnumbers)




            intentt.putExtra("contacts",str)
            context.sendBroadcast(intentt)

            // Log the read contacts
            for(contact in mContactsList) {
                Log.e(TAG, "Contact details -> " +
                        "Id: ${contact.id},Name: ${contact.name},Numbers: ${contact.numbers}")

                // this will log raw contacts with data
                logRawContacts(contact.id)
            }
        } catch (exception: Exception) {
            Log.d(TAG, "Network Failure: ${exception.cause}")
        }
        //endregion
    }

    fun getPhoneNumberWithoutCountryCode(phoneNumberWithCountryCode: String): String { //+91 7698989898
        val compile: Pattern = Pattern.compile(
            "\\+(?:998|996|995|994|993|992|977|976|975|974|973|972|971|970|968|967|966|965|964|963|962|961|960|886|880|856|855|853|852|850|692|691|690|689|688|687|686|685|683|682|681|680|679|678|677|676|675|674|673|672|670|599|598|597|595|593|592|591|590|509|508|507|506|505|504|503|502|501|500|423|421|420|389|387|386|385|383|382|381|380|379|378|377|376|375|374|373|372|371|370|359|358|357|356|355|354|353|352|351|350|299|298|297|291|290|269|268|267|266|265|264|263|262|261|260|258|257|256|255|254|253|252|251|250|249|248|246|245|244|243|242|241|240|239|238|237|236|235|234|233|232|231|230|229|228|227|226|225|224|223|222|221|220|218|216|213|212|211|98|95|94|93|92|91|90|86|84|82|81|66|65|64|63|62|61|60|58|57|56|55|54|53|52|51|49|48|47|46|45|44\\D?1624|44\\D?1534|44\\D?1481|44|43|41|40|39|36|34|33|32|31|30|27|20|7|1\\D?939|1\\D?876|1\\D?869|1\\D?868|1\\D?849|1\\D?829|1\\D?809|1\\D?787|1\\D?784|1\\D?767|1\\D?758|1\\D?721|1\\D?684|1\\D?671|1\\D?670|1\\D?664|1\\D?649|1\\D?473|1\\D?441|1\\D?345|1\\D?340|1\\D?284|1\\D?268|1\\D?264|1\\D?246|1\\D?242|1)\\D?"
        )
        //Log.e(tag, "number::_>" +  number);//OutPut::7698989898

        return phoneNumberWithCountryCode.replace(compile.pattern().toRegex(), "")
    }


    /**
     * Method to get database number string formatted to match server string
     */
    private fun getFormattedNumber(numberString: String): String {
        return numberString.replace("(","")
            .replace(")","")
            .replace("-","")
            .replace(" ","")
    }

    /**
     * Method to log rawcontacts
     */
    private fun logRawContacts(contactId: String) {
        for((index,rawContactId) in getRawContactIds(contactId).withIndex()) {
            Log.e(TAG, "RawContactId#$index -> $rawContactId")

            val dataCursor = context.contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                arrayOf(ContactsContract.Data.DATA1,ContactsContract.Data.DATA2,ContactsContract.Data.DATA3),
                "${ContactsContract.Data.RAW_CONTACT_ID} = ?",
                arrayOf(rawContactId), null)

            if(dataCursor != null && dataCursor.moveToFirst()) {
                do{
                    val data = dataCursor.getString(dataCursor.getColumnIndexOrThrow(ContactsContract.Data.DATA1))
                    val data2 = dataCursor.getString(dataCursor.getColumnIndexOrThrow(ContactsContract.Data.DATA2))
                    val data3 = dataCursor.getString(dataCursor.getColumnIndexOrThrow(ContactsContract.Data.DATA3))
                    Log.e(TAG, "Data1 -> $data")
                    Log.e(TAG, "Data2 -> $data2")
                    Log.e(TAG, "Data3 -> $data3")
                }while (dataCursor.moveToNext())
                dataCursor.close()
            }
        }
    }

    /**
     * Method to check if number is already registered
     */
    private fun isNumberAlreadyRegistered(number: String): Boolean {
        var isRegistered = false

        //region Get RawContactId's
        val rawContactIdCursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID),
            "${ContactsContract.CommonDataKinds.Phone.NUMBER} = ?",
            arrayOf(number), null
        )

        val rawContactIdList = ArrayList<String>()
        if(rawContactIdCursor != null && rawContactIdCursor.moveToFirst()) {
            do {
                rawContactIdList.add(rawContactIdCursor.getString(rawContactIdCursor
                    .getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID)))
            }while (rawContactIdCursor.moveToNext())
            rawContactIdCursor.close()
        }
        //endregion

        //region Check Account type
        for(rawContactId in rawContactIdList) {
            val accTypeCursor = context.contentResolver.query(
                ContactsContract.RawContacts.CONTENT_URI,
                arrayOf(ContactsContract.RawContacts.ACCOUNT_TYPE),
                "${ContactsContract.RawContacts._ID} = ?",
                arrayOf(rawContactId), null
            )

            val accTypeList = ArrayList<String>()
            if(accTypeCursor != null && accTypeCursor.moveToFirst()) {
                do {
                    accTypeList.add(accTypeCursor.getString(accTypeCursor
                        .getColumnIndexOrThrow(ContactsContract.RawContacts.ACCOUNT_TYPE)))
                } while (accTypeCursor.moveToNext())
                accTypeCursor.close()
            }

            if(accTypeList.contains(Constants.ACCOUNT_TYPE)) {
                isRegistered = true
                break
            }
        }
        //endregion

        return isRegistered
    }

    /**
     * Method to get RawContactId list for the ContactId
     */
    private fun getRawContactIds(contactId: String): ArrayList<String> {
        val rawContactIds = ArrayList<String>()
        val rawContactsCursor = context.contentResolver.query(
            ContactsContract.RawContacts.CONTENT_URI,
            arrayOf(ContactsContract.RawContacts._ID),
            "${ContactsContract.RawContacts.CONTACT_ID} = ?",
            arrayOf(contactId), null)

        if(rawContactsCursor != null && rawContactsCursor.moveToFirst()) {
            do {
                rawContactIds.add(rawContactsCursor.getString(rawContactsCursor
                    .getColumnIndexOrThrow(ContactsContract.RawContacts._ID)))
            } while (rawContactsCursor.moveToNext())

            rawContactsCursor.close()
        }

        return rawContactIds
    }

    /**
     * Method to get all contact data
     */
    private fun getContactData(): ArrayList<MyContact> {
        val contacts = ArrayList<MyContact>()

        // Get all contact id's
        val idCursor = context.contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
            arrayOf(ContactsContract.Contacts._ID),null,null,null,null)

        // create contactid list from idCursor
        if(idCursor != null && idCursor.moveToFirst()) {
            do {
                // add the id
                val contactId = idCursor.getString(idCursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))

                //region Numbers
                // query for all numbers for that id
                val numberCursor = context.contentResolver.query(ContactsContract.Data.CONTENT_URI,
                    arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                    "${ContactsContract.Data.CONTACT_ID} = ? AND " +
                            "${ContactsContract.Data.MIMETYPE} = ?",
                    arrayOf(contactId, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE),null)

                // create the numbers list from numberCursor
                val numbers = ArrayList<String>()
                if(numberCursor != null && numberCursor.moveToFirst()) {
                    do{
                        numbers.add(numberCursor.getString(
                            numberCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        ))
                    } while (numberCursor.moveToNext())

                    numberCursor.close()
                }
                //endregion

                //region Names
                val nameCursor = context.contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                    arrayOf(ContactsContract.Contacts.DISPLAY_NAME),
                    "${ContactsContract.Contacts._ID} = ?",
                    arrayOf(contactId), null)

                var name = ""
                if(nameCursor != null && nameCursor.moveToFirst()) {
                    do {
                        name = nameCursor.getString(nameCursor
                            .getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                    } while (nameCursor.moveToNext())

                    nameCursor.close()
                }
                //endregion

                //region RawContactIdMap
                val rawContactIdMap = HashMap<String,String>()
                for(number in numbers) {
                    val rawContactIdCursor = context.contentResolver.query(ContactsContract.Data.CONTENT_URI,
                        arrayOf(ContactsContract.Data.RAW_CONTACT_ID),
                        "${ContactsContract.CommonDataKinds.Phone.NUMBER} = ?",
                        arrayOf(number), null)

                    if(rawContactIdCursor != null && rawContactIdCursor.moveToFirst()) {
                        val rawContactId = rawContactIdCursor.getString(rawContactIdCursor.getColumnIndexOrThrow(
                            ContactsContract.Data.RAW_CONTACT_ID
                        ))
                        rawContactIdMap[number] = rawContactId
                        rawContactIdCursor.close()
                    }
                }
                //endregion
                Log.d("asdasdas",name)
                contacts.add(MyContact(contactId, name, numbers, rawContactIdMap))
            }while (idCursor.moveToNext())

            idCursor.close()

        }

        return contacts
    }
}
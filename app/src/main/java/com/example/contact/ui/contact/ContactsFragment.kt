package com.example.contact.ui.contact

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.contact.R
import com.example.contact.model.Contacts
import com.uits.baseproject.utils.PermissionUtil
import kotlinx.android.synthetic.main.fragment_contact.*
import java.io.IOException
import java.io.InputStream


class ContactsFragment : Fragment() {
    lateinit var mImgBtnCall: ImageButton
    lateinit var mImgBtnMessage: ImageButton
    private var permissions = arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG)
    lateinit var mContactAdapter: ContactAdapter
    var mListContacts: MutableList<Contacts> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkRunTimePermission()
    }

    private fun retrieveContactPhoto(context: Context, number: String?): Bitmap? {
        val contentResolver = context.contentResolver
        var contactId: String? = null
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(number)
        )
        val projection =
            arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID)
        val cursor = contentResolver.query(
            uri,
            projection,
            null,
            null,
            null
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                contactId =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID))
            }
            cursor.close()
        }
        var photo = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.ic_baseline_person_24
        )
        try {
            if (contactId != null) {
                val inputStream: InputStream? =
                    ContactsContract.Contacts.openContactPhotoInputStream(
                        context.contentResolver,
                        ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId.toLong())
                    )
                if (inputStream != null) {
                    photo = BitmapFactory.decodeStream(inputStream)
                }
                inputStream?.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return photo
    }

    private fun initAdapter() {
        mContactAdapter = ContactAdapter(mListContacts)
        mRecyclerView.adapter = mContactAdapter

        mProgressBarLoading.visibility = View.GONE
        mRecyclerView.visibility = View.VISIBLE

    }

    fun getContacts(context: Context): MutableList<Contacts> {
        val list: MutableList<Contacts> = arrayListOf()

        val cr = context.contentResolver
        val cur = cr.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null
        )
        if (cur?.count ?: 0 > 0) {
            while (cur != null && cur.moveToNext()) {
                val id = cur.getString(
                    cur.getColumnIndex(ContactsContract.Contacts._ID)
                )
                val name = cur.getString(
                    cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                    )
                )
                if (cur.getInt(
                        cur.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER
                        )
                    ) > 0
                ) {
                    cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )?.run {
                        while (moveToNext()) {
                            val phoneNo = getString(
                                getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER
                                )
                            )
                            list.add(Contacts(id, name, phoneNo, retrieveContactPhoto(requireActivity(),phoneNo)))
                        }
                        close()
                    }
                }
            }
        }
        cur?.close()
        return list
    }

    private fun checkRunTimePermission() {
        if (Build.VERSION.SDK_INT < 23) {
            getContactsAsyncTask()
        } else {
            requestPermissions(permissions, 55)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 55) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getContactsAsyncTask()
            }
        }
    }

    private fun getData(list: MutableList<Contacts>?) {
        list?.let { mListContacts = it}

        mListContacts.sortBy { it.name }
        initAdapter()
    }

    private fun getContactsAsyncTask() {
        @SuppressLint("StaticFieldLeak")
        val mAsyncTask = object : AsyncTask<Context, Void, MutableList<Contacts>?>() {
            override fun doInBackground(vararg params: Context?): MutableList<Contacts>? {
                var list = params[0]?.let { getContacts(it) }
                return list
            }

            override fun onPostExecute(result: MutableList<Contacts>?) {
                super.onPostExecute(result)
                getData(result)
            }
        }.execute(activity)
    }

}

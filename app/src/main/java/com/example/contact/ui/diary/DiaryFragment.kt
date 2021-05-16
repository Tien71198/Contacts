package com.example.contact.ui.diary

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.CallLog
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.contact.R
import com.example.contact.model.Logs
import kotlinx.android.synthetic.main.fragment_diary.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DiaryFragment : Fragment() {
    private var mListDiary: MutableList<Logs> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_diary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initAdapter()
    }

    private fun initData() {
        if (isPermissionGranted()){
            mListDiary = getCallLogs()
        }else{
            requestPermissions(arrayOf(Manifest.permission.READ_CALL_LOG), 56)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (isPermissionGranted()){
            mListDiary = getCallLogs()
        }else{
            requestPermissions(arrayOf(Manifest.permission.READ_CALL_LOG), 56)
        }
    }

    private fun initAdapter() {
        mRecyclerViewDiary.adapter = DiaryAdapter(mListDiary)
    }

    private fun getCallLogs(): MutableList<Logs> {
        val list: MutableList<Logs> = arrayListOf()

        val projection = arrayOf(
            CallLog.Calls._ID,
            CallLog.Calls.NUMBER,
            CallLog.Calls.DURATION,
            CallLog.Calls.DATE,
        )

        requireActivity().applicationContext.contentResolver.query(
            Uri.parse("content://call_log/calls"),
            projection,
            null,
            null,
            CallLog.Calls._ID + " DESC"
        )?.let { cursor ->
            if (cursor.count <= 0) {
                return@let
            }
            cursor.moveToFirst()
            do {
                cursor.apply {
                    val id = getString(getColumnIndex(CallLog.Calls._ID))
                    val number = getString(getColumnIndex(CallLog.Calls.NUMBER))
                    val duration = getString(getColumnIndex(CallLog.Calls.DURATION))
                    val dateStart = getString(getColumnIndex(CallLog.Calls.DATE))

                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = dateStart.toLong()

                    val stringDate = SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale.US).let {
                        it.format(Date(dateStart.toLong()))
                    }
                    val stringDuration = duration.let {
                        "[${getFormattedTime(it.toLong())}]"
                    }
                    list.add(Logs(id, number, stringDuration, stringDate))
                }
            } while (cursor.moveToNext())
        }
        return list
    }

    private fun getFormattedTime(secs: Long): String {
        return if (secs < 60) "00:$secs"
        else {
            val mins = secs / 60
            val remainderSecs = secs - mins * 60
            if (mins < 60) {
                ((if (mins < 10) "0" else "") + mins + ":"
                        + (if (remainderSecs < 10) "0" else "") + "00:$remainderSecs")
            } else {
                val hours = mins / 60
                val remainderMins = mins - hours * 60
                ((if (hours < 10) "0" else "") + hours + ":"
                        + (if (remainderMins < 10) "0" else "") + remainderMins + ":"
                        + (if (remainderSecs < 10) "0" else "") + remainderSecs)
            }
        }
    }

    fun isPermissionGranted() = ContextCompat.checkSelfPermission(
        requireActivity(),
        Manifest.permission.READ_CALL_LOG
    ) == PackageManager.PERMISSION_GRANTED
}

package com.mtr.fieldopscust.DialogRequestStatus

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.mtr.fieldopscust.ChatScreen.ChatActivity
import com.mtr.fieldopscust.R
import com.mtr.fieldopscust.Utils.ApplicationHelper.formatDate
import com.mtr.fieldopscust.Utils.ApplicationHelper.formatDateString
import com.mtr.fieldopscust.Utils.ApplicationHelper.formatTime
import com.mtr.fieldopscust.Utils.Constants.Companion.INTENT_MESSAGE_SEND_USER_FULL_NAME

class AdapterRequestStatusDialog(var context: Context?, var list: List<ModelRequestStatusDialog>) :
    RecyclerView.Adapter<ViewHolderRequestStatusDialog>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderRequestStatusDialog {
        return ViewHolderRequestStatusDialog(
            LayoutInflater.from(context).inflate(R.layout.menu_request_status, parent, false)
        )
    }


    override fun onBindViewHolder(holder: ViewHolderRequestStatusDialog, position: Int) {

        if(list[position].visibility){
            if (list[position].date.isEmpty()){
                holder.date.text = list[position].date
            } else {
                holder.date.text = formatDate(list[position].date)
            }
            if (list[position].date.isEmpty()){
                holder.time.text = list[position].time
            } else {
                holder.time.text = formatTime(list[position].time)
            }
            holder.statusWhiteText.text = list[position].status
            holder.message1.text = list[position].text1
            holder.message2.text = list[position].text2
            holder.viewLineGrey.visibility = View.GONE
            holder.viewLineGreen.visibility = View.VISIBLE

            holder.btnChat.setOnClickListener{
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("profile_url", list[position].profileUrl)
                intent.putExtra(INTENT_MESSAGE_SEND_USER_FULL_NAME, list[position].text1)
                context?.startActivity(intent)
            }

            if (list[position].visibilityChatButton) {
                holder.btnChat.visibility = View.VISIBLE
            } else {
                holder.btnChat.visibility = View.GONE
            }

            if (list[position].visibilityGreenDrawable) {
                holder.imgGreenCircleDrawable.visibility = View.VISIBLE
                var typeface = ResourcesCompat.getFont(context!!, R.font.inter)
                holder.message1.typeface = typeface
                typeface = ResourcesCompat.getFont(context!!, R.font.inter_bold)
                holder.message2.typeface = typeface
            } else {
                holder.imgGreenCircleDrawable.visibility = View.GONE
            }

            if (list[position].visibleEyeorTick) {
                holder.imgViewEyeorTick.setImageResource(R.drawable.vector_eye_white)
            } else {
                holder.imgViewEyeorTick.setImageResource(R.drawable.vector_white_tick)
            }
        }else{
            if (list[position].date.isEmpty()){
                holder.date.text = list[position].date
            } else {
                holder.date.text = formatDate(list[position].date)
            }
            if (list[position].date.isEmpty()){
                holder.time.text = list[position].time
            } else {
                holder.time.text = formatTime(list[position].time)
            }
            holder.statusGreyText.text = list[position].status
            holder.statusGreyText.visibility = View.VISIBLE
            holder.statusWhiteText.visibility = View.GONE
            holder.layoutStatus.backgroundTintList = ContextCompat.getColorStateList(context!!, R.color.grey1)
            holder.message1.text = list[position].text1
            holder.message2.visibility = View.GONE
            holder.viewLineGrey.visibility = View.VISIBLE
            holder.viewLineGreen.visibility = View.VISIBLE

            if (list[position].visibilityChatButton) {
                holder.btnChat.visibility = View.VISIBLE
            } else {
                holder.btnChat.visibility = View.GONE
            }

            if (list[position].visibilityGreyDrawable) {
                holder.imgGreyCircleDrawable.visibility = View.VISIBLE
                var typeface = ResourcesCompat.getFont(context!!, R.font.inter)
                holder.message1.typeface = typeface
                typeface = ResourcesCompat.getFont(context!!, R.font.inter_bold)
                holder.message2.typeface = typeface
            } else {
                holder.imgGreyCircleDrawable.visibility = View.GONE
            }

            if (list[position].visibleEyeorTick) {
                holder.imgViewEyeorTick.setImageResource(R.drawable.vector_eye_grey)
                holder.imgViewEyeorTick.visibility = View.VISIBLE
            } else {
                holder.imgViewEyeorTickGrey.setImageResource(R.drawable.vector_grey_tick)
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}

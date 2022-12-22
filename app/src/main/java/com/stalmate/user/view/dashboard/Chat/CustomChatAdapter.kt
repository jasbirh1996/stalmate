import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.nayaeducation.user.model.Message
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R

import com.stalmate.user.databinding.ItemTextInNewBinding
import com.stalmate.user.databinding.ItemTextOutNewBinding
import com.stalmate.user.utilities.Constants
import com.stalmate.user.utilities.TimesAgo2


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList
import java.util.HashMap

class CustomChatAdapter     // you can pass other parameters in constructor
    (private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    var list: ArrayList<Message> = ArrayList<Message>()

    private inner class MessageInViewHolder(var binding: ItemTextInNewBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(position: Int) {
            val messageModel: Message = list[position]

      /*      binding.dateText.text =
                TimesAgo2.covertTimeToText(messageModel.created_at.toString(), false)
*/

            if (messageModel.is_file == 1) {
                if (messageModel.file_type.equals("jpg") || messageModel.file_type.equals("jpeg") || messageModel.file_type.equals(
                        "webp"
                    )
                ) {
                    binding.ivImage.visibility = View.VISIBLE
                    binding.messageText.visibility = View.GONE
            /*        Glide.with(context).load(messageModel.text).into(binding.ivImage)
                    binding.ivImage.setOnClickListener {
                        context.startActivity(
                            IntentHelper.getDataViewer(
                                context
                            ).putExtra(Constants.type, "image")
                                .putExtra(Constants.url, messageModel.text)
                        )
                    }*/
                } else if (messageModel.file_type.equals("mp4")) {
                    binding.ivImage.visibility = View.VISIBLE
                    binding.messageText.visibility = View.GONE
           /*         Glide.with(context).load(R.drawable.video_icon).into(binding.ivImage)
                    binding.ivImage.setOnClickListener {
                        context.startActivity(
                            IntentHelper.getVideoPlayerScreen(context)
                        )
                    }*/

                } else if (messageModel.file_type.equals("xls") || messageModel.file_type.equals("xlsx")) {


                } else if (messageModel.file_type.equals("mp3")) {

                  //  Glide.with(context).load(R.drawable.sound_new).into(binding.ivImage)
             /*       binding.ivImage.setOnClickListener {
                        var audioPlayDialog =
                            AudioplayerDialog(context, object : AudioplayerDialog.Callbackk {
                                override fun isAudioPlaying(isPlaying: Boolean?) {

                                }
                            })
                        audioPlayDialog.showDialog(messageModel.text)
                    }*/

                } else if (messageModel.file_type.equals("pdf")) {
                    binding.ivImage.visibility = View.VISIBLE
                    binding.messageText.visibility = View.GONE
              /*      Glide.with(context).load(R.drawable.pdf_new).into(binding.ivImage)
                    binding.ivImage.setOnClickListener {
                        context.startActivity(
                            IntentHelper.getDataViewer(
                                context
                            ).putExtra(Constants.type, "pdf")
                                .putExtra(Constants.url, messageModel.text)
                        )

                    }*/

                }

            } else {
                binding.ivImage.visibility = View.GONE
                binding.messageText.setText(messageModel.text)
                binding.messageText.visibility = View.VISIBLE
            }
        }

    }


    private inner class MessageOutViewHolder(var binding: ItemTextOutNewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val messageModel: Message = list[position]

    /*        binding.dateText.text =
                TimesAgo2.covertTimeToText(messageModel.created_at.toString(), false)
*/

            if (messageModel.is_file == 1) {
                binding.messageText.visibility = View.GONE

                if (messageModel.file_type.equals("jpg") || messageModel.file_type.equals("jpeg") || messageModel.file_type.equals(
                        "webp"
                    )
                ) {

                    binding.layoutAudio.visibility=View.GONE
                    binding.layoutImage.visibility=View.VISIBLE
                    binding.layoutVideo.visibility=View.GONE

                //    Glide.with(context).load(messageModel.text).into(binding.ivImage)
            /*        binding.ivImage.setOnClickListener {
                        context.startActivity(
                            IntentHelper.getDataViewer(
                                context
                            ).putExtra(Constants.type, "image")
                                .putExtra(Constants.url, messageModel.text)
                        )
                    }*/
                } else if (messageModel.file_type.equals("mp4")) {

                    binding.layoutAudio.visibility=View.GONE
                    binding.layoutImage.visibility=View.GONE
                    binding.layoutVideo.visibility=View.VISIBLE

                  //  Glide.with(context).load(R.drawable.video_icon).into(binding.ivImage)
               /*     binding.ivImage.setOnClickListener {
                        context.startActivity(
                            IntentHelper.getVideoPlayerScreen(context)
                        )
                    }*/

                } else if (messageModel.file_type.equals("xls") || messageModel.file_type.equals("xlsx")) {


                } else if (messageModel.file_type.equals("mp3")) {
                 binding.layoutAudio.visibility=View.VISIBLE
                    binding.layoutImage.visibility=View.GONE
                    binding.layoutVideo.visibility=View.GONE
                  //  Glide.with(context).load(R.drawable.sound_new).into(binding.ivAudio)
           /*         binding.ivAudio.setOnClickListener {
                        var audioPlayDialog =
                            AudioplayerDialog(context, object : AudioplayerDialog.Callbackk {
                                override fun isAudioPlaying(isPlaying: Boolean?) {

                                }
                            })
                        audioPlayDialog.showDialog(messageModel.text)
                    }*/

                } else if (messageModel.file_type.equals("pdf")) {
                    binding.ivImage.visibility = View.VISIBLE
                  //  Glide.with(context).load(R.drawable.pdf_new).into(binding.ivImage)
                /*    binding.ivImage.setOnClickListener {
                        context.startActivity(
                            IntentHelper.getDataViewer(
                                context
                            ).putExtra(Constants.type, "pdf")
                                .putExtra(Constants.url, messageModel.text)
                        )

                    }*/

                }

            } else {
                binding.messageText.setText(messageModel.text)
                binding.messageText.visibility = View.VISIBLE
                binding.layoutAudio.visibility=View.GONE
                binding.layoutImage.visibility=View.GONE
                binding.layoutVideo.visibility=View.GONE

            }



            if (messageModel.status=="seen"){
               binding.blueTick.visibility=View.VISIBLE
            }else   if (messageModel.status=="sent"){
                binding.singleTick.visibility=View.VISIBLE
            }
            else   if (messageModel.status=="delivered"){
                binding.doubleTick.visibility=View.VISIBLE
            }
            else   if (messageModel.status=="notSent"){
                list[list.size-1].status="notSent"
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == MESSAGE_TYPE_IN) {

            var binding = DataBindingUtil.bind<ItemTextInNewBinding>(
                LayoutInflater.from(context).inflate(R.layout.item_text_in_new, parent, false)
            )!!

            MessageInViewHolder(binding)
        } else {
            var binding = DataBindingUtil.bind<ItemTextOutNewBinding>(
                LayoutInflater.from(context).inflate(R.layout.item_text_out_new, parent, false)
            )!!

            MessageOutViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (list[position].position.equals("left")) {
            (holder as MessageInViewHolder).bind(position)
        } else {
            (holder as MessageOutViewHolder).bind(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position].position.equals("left")) {
            1
        } else {
            2
        }
    }

    fun addMessage(mesasage: Message) {
        list.add(mesasage)
        notifyItemInserted(list.size)
    }

    fun updateMessageStatus(messageStatus: String) {

        if (messageStatus=="seen"){
            list[list.size-1].status="seen"
        }else   if (messageStatus=="sent"){
            list[list.size-1].status="sent"
        }
        else   if (messageStatus=="delivered"){
            list[list.size-1].status="delivered"
        }
        else   if (messageStatus=="notSent"){
            list[list.size-1].status="notSent"
        }
        notifyItemChanged(list.size-1)
    }





    fun addAllMessage(mesasage: List<Message>?) {
        val size = list.size
        list.addAll(mesasage!!)
        val sizeNew = list.size
        notifyItemRangeChanged(size, sizeNew)
    }



    companion object {
        const val MESSAGE_TYPE_IN = 1
        const val MESSAGE_TYPE_OUT = 2
    }


    fun addBucketMessageToList(mesasage: ArrayList<Message>?,fillingCapability: Int) {
        Log.d("asdasdasdSizeBefore", (list.size).toString())
        //Removing Upper Items To Add Below New Items
        if (fillingCapability==0){
            if (list.size>9){
                var removeCount=0
                var itemToBeRemoved=mesasage!!.size
                for (i in list.size-1 downTo 1) {
                    if (removeCount!=itemToBeRemoved){
                        removeCount++
                        removeAt(i)
                    }
                }
            }

        }
        list.addAll(0,mesasage!!)
        notifyDataSetChanged()


    }

    fun removeAt(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, list.size)
    }












}
package com.stalmate.user.view.dashboard.Chat

import CustomChatAdapter
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.nayaeducation.user.model.Message
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentSocketChatBinding
import com.stalmate.user.utilities.PrefManager
import com.stalmate.user.utilities.ValidationHelper
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException


class FragmentSocketChat(var receiver_id: String) : BaseFragment() {
    var isSendButtonVisible = false
    var sender_id = ""
    lateinit var binding: FragmentSocketChatBinding
    lateinit var mSocket: io.socket.client.Socket
    lateinit var chatAdapter: CustomChatAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v = inflater.inflate(R.layout.fragment_socket_chat, null, false)
        binding = DataBindingUtil.bind<FragmentSocketChatBinding>(v)!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("akljsdasd", "alds;asd")
        sender_id = PrefManager.getInstance(requireContext())!!.userDetail.results?._id.toString()
        chatAdapter = CustomChatAdapter(requireContext())
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerview.adapter = chatAdapter
        getRoomId()
        binding.ivVideoCall.setOnClickListener {
            startActivity(IntentHelper.getCallScreen(requireContext()))
        }
        Glide.with(requireActivity()).load(R.drawable.user_placeholder).circleCrop()
            .into(binding.ivUserImage)

        binding.msgEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    if (!ValidationHelper.isNull(s.toString())) {
                        var jsonObject = JSONObject()
                        jsonObject.put("typing", true)
                        jsonObject.put("Room_id", roomId)
                        mSocket.emit("typing", true, jsonObject)

                        if (!isSendButtonVisible) {
                            isSendButtonVisible = true
                            val bm = BitmapFactory.decodeResource(
                                resources, R.drawable.send_icon
                            )
                            changeImageWithAnimation(
                                requireContext(),
                                binding.ivButtonVoiceRecorder, bm
                            )
                        }


                    } else {
                        isSendButtonVisible = false
                        val bm = BitmapFactory.decodeResource(
                            resources, R.drawable.microphone_button
                        )
                        changeImageWithAnimation(
                            requireContext(),
                            binding.ivButtonVoiceRecorder, bm
                        )
                    }


                }

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        getAllMessages()

    }

    var handler: Handler = Handler(Looper.getMainLooper() /*UI thread*/)
    lateinit var workRunnable: Runnable
    fun listenTyping() {
        requireActivity().runOnUiThread {
            binding.tvOtherUserStatus.text = "Typing..."
        }
        if (this::workRunnable.isInitialized) {
            handler.removeCallbacks(workRunnable)
        }
        workRunnable = Runnable {
            binding.tvOtherUserStatus.text = "Online"
        }
        handler.postDelayed(workRunnable, 1000 /*delay*/)
    }


    fun listeningSocket(roomId: String) {
        SocketHandler.setSocket()
        SocketHandler.establishConnection()
        mSocket = SocketHandler.getSocket()
        mSocket.connect()
        mSocket.emit("join", roomId);
        mSocket.on("message") { args ->//listening
            if (args[0] != null) {
                val data = args[0] as JSONObject
                var nickname = data.getString("senderNickname");
                var message = data.getString("message");
                var sender_id = data.getString("sender_id");
                var receiver_id = data.getString("receiver_id");
                var Room_id = data.getString("Room_id");
                Log.d("aljdasda", "apskdasd")
                var messageObjsect = Message(
                    System.currentTimeMillis(),
                    receiver_id,
                    sender_id,
                    "",
                    message,
                    "",
                    nickname,
                    "",
                    0,
                    "left",
                    ""
                )
                chatAdapter.addMessage(messageObjsect)
                //adding to database
                AppDatabase.getAppDatabase(requireContext())!!.messageDao()!!
                    .insert(messageObjsect);

                mSocket.emit(
                    "markSeen", roomId, ""
                )

                //  binding.tvReceivingMessage.text = nickname
            }
        }
        //    senderNickname+" : " +messageContent+" : " +sender_id+" : " +receiver_i
        binding.buttonMain.setOnClickListener {//sending message
            if (isSendButtonVisible) {
                mSocket.emit(
                    "messagedetection",
                    PrefManager.getInstance(requireContext())!!.userDetail.results?.first_name,
                    binding.msgEdittext.text.toString(),
                    sender_id, receiver_id, roomId
                )
                var messageObject = Message(
                    System.currentTimeMillis(),
                    receiver_id,
                    sender_id,
                    "",
                    binding.msgEdittext.text.toString(),
                    "",
                    PrefManager.getInstance(requireContext())!!.userDetail.results?.first_name.toString(),
                    "",
                    0,
                    "right",
                    "sent"
                )
                chatAdapter.addMessage(messageObject)
                //adding to database
                AppDatabase.getAppDatabase(requireContext())!!.messageDao()!!
                    .insert(messageObject);
                binding.msgEdittext.setText("")
                binding.recyclerview.smoothScrollToPosition(chatAdapter.list.size - 1)
            }
        }


        mSocket.on("display") { args ->//listening
            if (args[0] != null) {

                listenTyping()
            }
        }


        mSocket.on("markSeen") { args ->//listening
            Log.d("lkajsdasd", "aksjdlasd")
            if (args[0] != null) {
                chatAdapter.updateMessageStatus("seen")
            }
        }

        mSocket.on("user_offline") { args ->//listening

            if (args[0] != null) {
                var isOffline = args[0] as Boolean
                if (isOffline) {
                    requireActivity().runOnUiThread {
                        binding.tvOtherUserStatus.text = "Offline"
                    }
                } else {
                    requireActivity().runOnUiThread {
                        binding.tvOtherUserStatus.text = "Online"
                    }
                }

            }
        }

    }

    var roomId = ""
    private fun getRoomId() {
        var hashmap = HashMap<String, String>()
        hashmap.put("receiver_id", receiver_id)
        networkViewModel.createroomId(hashmap)
        networkViewModel.createRoomIdLiveData.observe(viewLifecycleOwner) {
            if (it!!.status) {
                listeningSocket(it.Room_id)
                roomId = it.Room_id
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mSocket.disconnect()
    }

    override fun onStart() {
        if (this::mSocket.isInitialized) {
            Log.d("lakjdasd", "paisdpoasd")
            mSocket.emit("join", roomId);
        }
        super.onStart()
    }

    fun sendImage(path: String) {
        val sendData = JSONObject()
        try {
            sendData.put("imageData", encodeImage(path))
            mSocket.emit("image", sendData)
        } catch (e: JSONException) {
        }
    }

    private fun encodeImage(path: String): String? {
        val imagefile = File(path)
        var fis: FileInputStream? = null
        try {
            fis = FileInputStream(imagefile)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        val bm: Bitmap = BitmapFactory.decodeStream(fis)
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        //Base64.de
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    fun getAllMessages() {
        var data = AppDatabase.getAppDatabase(requireContext())!!.messageDao()!!.all
        chatAdapter.addAllMessage(data)
    }

    fun changeImageWithAnimation(c: Context?, v: ImageView, new_image: Bitmap?) {
        val anim_out: Animation = AnimationUtils.loadAnimation(c, android.R.anim.fade_out)
        val anim_in: Animation = AnimationUtils.loadAnimation(c, android.R.anim.fade_in)
        anim_out.setAnimationListener(object : Animation.AnimationListener {
            override
            fun onAnimationStart(animation: Animation?) {
            }

            override
            fun onAnimationRepeat(animation: Animation?) {
            }

            override
            fun onAnimationEnd(animation: Animation?) {
                v.setImageBitmap(new_image)
                anim_in.setAnimationListener(object : Animation.AnimationListener {
                    override
                    fun onAnimationStart(animation: Animation?) {
                    }

                    override
                    fun onAnimationRepeat(animation: Animation?) {
                    }

                    override
                    fun onAnimationEnd(animation: Animation?) {
                    }
                })
                v.startAnimation(anim_in)
            }
        })
        v.startAnimation(anim_out)
    }

}

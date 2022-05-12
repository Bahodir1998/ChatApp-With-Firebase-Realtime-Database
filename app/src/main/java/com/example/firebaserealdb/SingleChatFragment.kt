package com.example.firebaserealdb

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.firebaserealdb.Notifications.*
import com.example.firebaserealdb.adapters.MessageAdapter
import com.example.firebaserealdb.adapters.PrefsHelper
import com.example.firebaserealdb.constants.AppConstants
import com.example.firebaserealdb.databinding.FragmentSingleChatBinding
import com.example.firebaserealdb.models.Message
import com.example.firebaserealdb.models.OnlineUser
import com.example.firebaserealdb.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SingleChatFragment : Fragment() {

    lateinit var binding: FragmentSingleChatBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var messageAdapter: MessageAdapter
    lateinit var user: User
    lateinit var myInfo: User
    lateinit var apiService: APIService
    var notify = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSingleChatBinding.inflate(inflater)

        val intent = Intent()
        if (arguments?.getSerializable("user") != null) {
            user = arguments?.getSerializable("user") as User
        } else if (intent.hasExtra("hisId")) {
            val dbRef = FirebaseDatabase.getInstance().getReference("users")
                .child(intent.getStringExtra("hisId")!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        user = snapshot.getValue(User::class.java)!!
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })


        }
        apiService = Client().getClient("https://fcm.googleapis.com/").create(APIService::class.java)
        Picasso.get().load(user.photoUrl).into(binding.imgView)
        binding.nameTv.setText(user.displayName)

        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseDatabase.getReference("users").child(currentUser?.uid!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    myInfo = snapshot.getValue(User::class.java)!!
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        reference = firebaseDatabase.getReference("messages")

        firebaseDatabase.getReference("online/${user.uid}")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("ResourceAsColor")
                override fun onDataChange(snapshot: DataSnapshot) {

                    val value = snapshot.getValue(OnlineUser::class.java)
                    if (value != null && value.isOnline == 1) {
                        binding.online.setText("Online")
                    } else {
                        binding.online.setText("Offline")
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        binding.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.editText.text.isNullOrBlank()) {
                    binding.addOrSend.setImageResource(R.drawable.ic_baseline_add_24)
                } else {
                    binding.addOrSend.setImageResource(R.drawable.ic_baseline_send_24)
                }
            }

        })

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.addOrSend.setOnClickListener {
            val m = binding.editText.text.toString()
            if (!m.isNullOrBlank()) {
                val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
                val date = simpleDateFormat.format(Date())
                val message = Message(m, date, 1)
                val key = reference.push().key
                reference.child("${currentUser?.uid}/${user.uid}/$key").setValue(message)
                message.isMine = 0
                reference.child("${user.uid}/${currentUser?.uid}/$key").setValue(message)
                binding.editText.setText("")
                firebaseDatabase.getReference("lastmessage")
                    .child("${currentUser?.uid}/${user.uid}").setValue(message)
                firebaseDatabase.getReference("lastmessage")
                    .child("${user.uid}/${currentUser?.uid}")
                    .setValue(message)

                sentNotification(user.uid, myInfo.displayName, m)
            }
        }

        reference.child("${currentUser?.uid}/${user.uid}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = ArrayList<Message>()
                    val children = snapshot.children
                    for (child in children) {
                        val value = child.getValue(Message::class.java)
                        if (value != null) {
                            list.add(value)
                        }
                    }

                    messageAdapter = MessageAdapter(list)
                    binding.messageRv.adapter = messageAdapter
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        return binding.root
    }

    private fun sentNotification(receiver: String?, username: String?, message: String) {
        var token: Token = Token(user.token!!)
        var data: Data =
            Data(myInfo.uid!!, myInfo.photoUrl!!, username + ": " + message, "New Message", user.uid!!)

        var sender = Sender(data, token.token)
        val key = AppConstants.SERVER_KEY
        apiService.sendNotification(sender, "application/json",key).enqueue(object : Callback<MyResponce>{
            override fun onResponse(
                call: Call<MyResponce>,
                response: retrofit2.Response<MyResponce>
            ) {
                Log.d("RES", "onResponse: ${response.body()}")
                Log.d("RES", "CODE: ${response.code()}")
                Log.d("RES", "MESSAGE: ${response.message()}")
                if (response.code() == 200){
                    if (response.body()?.success == 1){
                        Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<MyResponce>, t: Throwable) {
                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                Log.d("RES", "onFailure: cause= ${t.cause}")
                Log.d("RES", "onFailure: message= ${t.message}")
            }

        })
    }

    private fun getToken(message: String) {

        val token = user.token
        Log.d("AAA", "LOCALE TOKEN = $token")
        val to = JSONObject()
        val data = JSONObject()

        data.put("hisId", myInfo.uid)
        data.put("hisUid", user.uid)
        data.put("hisImage", myInfo.photoUrl)
        data.put("title", myInfo.displayName)
        data.put("message", message)

        to.put("to", token)
        to.put("data", data)
        Log.d("SSS", "getToken: $to")
        sendNotification(to)
    }

    private fun sendNotification(to: JSONObject) {
        Log.d("BBB", "JSONObject: $to")
        val request: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            AppConstants.NOTIFICATION_URL,
            to,
            Response.Listener {
                Log.d("BBB", "onResponce: $it")
            },
            Response.ErrorListener {
                Log.d("BBB", "onError: $it")
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val map: MutableMap<String, String> = HashMap()

                map["Authorization"] = "key=" + AppConstants.SERVER_KEY
                map["Content-type"] = "application/json; charset=utf-8"
                return map
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }
        val requestQueue = Volley.newRequestQueue(requireContext())
        request.retryPolicy = DefaultRetryPolicy(
            30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQueue.add(request)
    }
}
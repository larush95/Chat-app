package com.example.chatapp.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.R
import com.example.chatapp.adapters.MessageAdapter
import com.example.chatapp.components.DeleteMessageDialogFragment
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.model.Message
import com.example.chatapp.observers.ButtonObserver
import com.example.chatapp.observers.ScrollToBottomObserver
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity(), DeleteMessageDialogFragment.IDeleteMessageFragment {
    private lateinit var binding: ActivityMainBinding
    private lateinit var manager: LinearLayoutManager
    private lateinit var auth: FirebaseAuth


    private lateinit var db: FirebaseDatabase
    private lateinit var adapter: MessageAdapter

    private var id: String? = null
    private var deleteMessageDialogFragment = DeleteMessageDialogFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        if (auth.currentUser == null) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return
        }

        db =
            FirebaseDatabase.getInstance("https://chatapp-c104d-default-rtdb.europe-west1.firebasedatabase.app/")
        val messagesRef = db.reference.child(MESSAGES_CHILD)

        val options = FirebaseRecyclerOptions.Builder<Message>()
            .setQuery(messagesRef, Message::class.java)
            .build()
        adapter = MessageAdapter(options, getUserName(), this)
        binding.progressBar.visibility = ProgressBar.INVISIBLE
        manager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = manager
        binding.recyclerView.adapter = adapter

        deleteMessageDialogFragment.listener = this

        adapter.registerAdapterDataObserver(
            ScrollToBottomObserver(binding.recyclerView, adapter, manager)
        )

        binding.chatField.addTextChangedListener(ButtonObserver(binding.sendButton))

        binding.sendButton.setOnClickListener {
            val message =
                Message(
                    binding.chatField.text.toString(),
                    getUserName(),
                    getPhotoUrl(),
                    getDateTimeString()
                )

            db.reference.child(MESSAGES_CHILD).push().setValue(message)
            binding.chatField.setText("")
        }
    }

    private fun getDateTimeString(): String? {
        val dateTime = LocalDateTime.now()
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE. dd/MM HH:mm:ss")

        return dateTime.format(formatter)
    }


    public override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return
        }
    }

    public override fun onPause() {
        adapter.stopListening()
        super.onPause()
    }

    public override fun onResume() {
        adapter.startListening()
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_out_menu -> {
                signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getUserName(): String? {
        val user = auth.currentUser
        return if (user != null) {
            user.displayName
        } else ANONYMOUS
    }


    private fun getPhotoUrl(): String? {
        val user = auth.currentUser
        return user?.photoUrl?.toString()
    }

    private fun signOut() {
        AuthUI.getInstance().signOut(this)
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }

    fun showDialogFragment(id: String?) {
        this.id = id
        deleteMessageDialogFragment.show(supportFragmentManager, "dialog");
    }

    override fun confirmClicked() {
        val ref = db.reference
        val query: Query = ref.child(MESSAGES_CHILD).orderByChild("uid").equalTo(id!!)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    snapshot.ref.removeValue()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException())
            }
        })

    }
    companion object {
        private const val TAG = "MainActivity"
        const val MESSAGES_CHILD = "messages"
        const val ANONYMOUS = "anonymous"
    }
}

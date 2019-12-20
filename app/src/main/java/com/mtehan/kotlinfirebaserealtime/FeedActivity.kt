package com.mtehan.kotlinfirebaserealtime

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.core.OrderBy
import com.mtehan.kotlinfirebaserealtime.Adapter.FeedRecyclerAdapter
import kotlinx.android.synthetic.main.activity_feed.*
import java.sql.Timestamp

class FeedActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    var userEmailFromDb: ArrayList<String> = ArrayList()
    var userCommentFromDb: ArrayList<String> = ArrayList()
    var userImageFromDb: ArrayList<String> = ArrayList()

    var adapter : FeedRecyclerAdapter?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        //data resp.
        getDataFireStore()

        //reyclerview set

        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter=FeedRecyclerAdapter(userEmailFromDb,userCommentFromDb,userImageFromDb)
        recyclerView.adapter=adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_post) {
            //Upload Activity
            val intent = Intent(applicationContext, UploadActivity::class.java)
            startActivity(intent)

        } else if (item.itemId == R.id.logout) {
            mAuth = FirebaseAuth.getInstance()
            mAuth.signOut()
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun getDataFireStore() {
        db.collection("Posts").orderBy("date", Query.Direction.DESCENDING
        ).addSnapshotListener { querySnapshot, exception ->
            if (exception != null) {
                Toast.makeText(
                    applicationContext,
                    exception.localizedMessage.toString(),
                    Toast.LENGTH_LONG
                ).show()

            } else {
                if (querySnapshot != null) {
                    if (!querySnapshot.isEmpty) {

                        userEmailFromDb.clear()
                        userCommentFromDb.clear()
                        userImageFromDb.clear()

                        val documents = querySnapshot.documents
                        for (document in documents) {
                            val comment = document.get("comment") as String
                            val userEmail = document.get("userEmail") as String
                            val downloadUrl = document.get("downloadUrl") as String

                            userEmailFromDb.add(userEmail)
                            userCommentFromDb.add(comment)
                            userImageFromDb.add(downloadUrl)

                            adapter!!.notifyDataSetChanged()
                        }
                    }
                }
            }
        }

    }
}

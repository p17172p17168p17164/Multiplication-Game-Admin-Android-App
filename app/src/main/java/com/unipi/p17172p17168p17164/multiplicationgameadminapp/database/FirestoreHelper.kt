package com.unipi.p17172p17168p17164.multiplicationgameadminapp.database

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.unipi.p17172p17168p17164.multiplicationgameadminapp.models.User
import com.unipi.p17172p17168p17164.multiplicationgameadminapp.ui.activities.UserDetailsActivity
import com.unipi.p17172p17168p17164.multiplicationgameadminapp.ui.activities.UsersListActivity
import com.unipi.p17172p17168p17164.multiplicationgameadminapp.utils.Constants

class FirestoreHelper {

    // Access a Cloud Firestore instance.
    private val dbFirestore = FirebaseFirestore.getInstance()

    /**
     * A function to get the user id of current logged user.
     */
    fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    /**
     * A function to get the user details from FireStore Database.
     */
    fun getUserDetails(activity: Activity, userId: String) {

        // Here we pass the collection name from which we wants the data.
        dbFirestore.collection(Constants.COLLECTION_USERS)
            // The document id to get the Fields of user.
            .document(userId)
            .get()
            .addOnSuccessListener { document ->

                /// Here we get the product details in the form of document.
                Log.d(activity.javaClass.simpleName, document.toString())

                // Convert the snapshot to the object of Product data model class.
                val user = document.toObject(User::class.java)!!

                when (activity) {
                    is UserDetailsActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.successUserDetailsFromFirestore(user)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is UserDetailsActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details.",
                    e
                )
            }
    }

    /**
     * A function to get the tables list from cloud firestore.
     *
     * @param activity The fragment is passed as parameter as the function is called from fragment and need to the success result.
     */
    fun getUsersList(activity: Activity) {
        // The collection name for PRODUCTS
        dbFirestore.collection(Constants.COLLECTION_USERS)
            .orderBy(Constants.FIELD_FULL_NAME, Query.Direction.ASCENDING)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of boards in the form of documents.
                Log.d("Users List", document.documents.toString())

                // Here we have created a new instance for Products ArrayList.
                val usersList: ArrayList<User> = ArrayList()

                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in document.documents) {

                    val user = i.toObject(User::class.java)
                    user!!.userId = i.id

                    usersList.add(user)
                }
                when (activity) {
                    is UsersListActivity -> {
                        activity.successUsersListFromFirestore(usersList)
                    }
                    else -> {}
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is UsersListActivity -> {
                        activity.hideProgressDialog()
                    }
                    else -> {}
                }
                Log.e("Get Users List", "Error while getting users list.", e)
            }
    }

    fun updateUserProfileData(activity: Activity, userId: String, userHashMap: HashMap<String, Any>) {
        // Collection Name
        dbFirestore.collection(Constants.COLLECTION_USERS)
            // Document ID against which the data to be updated. Here the document id is the current logged in user id.
            .document(userId)
            // A HashMap of fields which are to be updated.
            .update(userHashMap)
            .addOnSuccessListener {

                // Notify the success result.
                when (activity) {
                    is UserDetailsActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is UserDetailsActivity -> {
                        // Hide the progress dialog if there is any error. And print the error in log.
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the user details.",
                    e
                )
            }
    }

}
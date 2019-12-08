package com.example.notely.ui.user

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class UserViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _text = MutableLiveData<String>().apply {
        value = "This is user Fragment"
    }
    private val _user = MutableLiveData<FirebaseUser?>()
    val text: LiveData<String>
        get() = _text
    val user: LiveData<FirebaseUser?>
        get() = _user

    init {
        _user.value = auth.currentUser
    }

    fun setUpClient(webClientID: String, context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientID)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    lateinit var googleSignInClient: GoogleSignInClient
    val RC_SIGN_IN = 9001

    fun signIn(frag: Fragment) {
        val signInIntent = googleSignInClient.signInIntent
        // Fragment should override onActivityResult() to call userViewModel.firebaseAuthWithGoogle()
        frag.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun signOut() {
        auth.signOut()
        _user.value = null
    }

    fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                when ( task.isSuccessful ) {
                    true -> _user.value = auth.currentUser
                    else -> _user.value = null
                }
            }
    }
}
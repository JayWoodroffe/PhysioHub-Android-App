package com.example.mypractice

//for firebase
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mypractice.data.DoctorDataAccess
import com.example.mypractice.data.DoctorDataHolder
import com.example.mypractice.databinding.ActivityMainBinding
import com.example.mypractice.model.DoctorModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class Login : AppCompatActivity() {
    private val RC_SIGN_IN = 123 // Request code for Google Sign-In
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialising firebase
        firebaseAuth = FirebaseAuth.getInstance()

        //hidings status and navigation bar
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )




            //initialising firebase sdk
        FirebaseApp.initializeApp(this)

        //configure google sign in
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnGoogleSignIn.setOnClickListener{
            signInWithGoogle()
        }

    }
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed
                Log.w("LoginActivity", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    val user = FirebaseAuth.getInstance().currentUser
                    Log.w("LoginActivity", "signInWithCredential:success", task.exception)

                    //if the user has logged in with google, need to check if the doctor is registered in firestore
                    if(user != null)
                    {
                        val userEmail = user.email
                        try {
                            if (userEmail != null) {
                                checkFireStore(userEmail,
                                    onSuccess = {
                                        //user is a registered doctor
                                        startActivity(Intent(this, Home::class.java))
                                        finish()
                                    },
                                    onFailure = {
                                        //user is not a registered doctor
                                        val intent = Intent(this, RegisterContact::class.java)
                                        intent.putExtra("Email", userEmail)
                                        Log.e("LoginActivity", "not registered")
                                        startActivity(intent)
                                    })
                            }
                        }catch (e: Exception) {
                            // Handle any unexpected exceptions
                            Toast.makeText(
                                this@Login,
                                "An unexpected error occurred, please try again later",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("LoginActivity", "Unexpected error during login", e)
                        }
                    }
                    //startActivity(Intent(this, Home::class.java))
                } else {
                    // Sign in failed
                    Log.w("LoginActivity", "signInWithCredential:failure", task.exception)
                }
            }
    }

    fun checkFireStore(email:String, onSuccess:() -> Unit, onFailure:(String) -> Unit)
    {
        DoctorDataAccess.getDoctorByEmail(
            email,
            onSuccess = { doctor: DoctorModel ->
                // Store the doctor object
                Log.d("logIn", "here")
                DoctorDataHolder.setLoggedInDoctor(doctor)
                onSuccess()
            },
            onFailure = { error ->
                onFailure(error)
            }
        )
    }




}

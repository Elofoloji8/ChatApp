package com.example.chatapp.models

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.auth.AuthState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class Database : ViewModel(){

    // Firebase kimlik doğrulama örneği oluşturma
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    // Kimlik doğrulama durumunu tutar
    private val _authState = MutableLiveData<AuthState>()
    val authState : LiveData<AuthState> = _authState

    private var db = FirebaseFirestore.getInstance()


    // Sınıf başlatıldığında çalışacak olan init bloğu
    init {
        checkAuthStatus()
    }

    // Kullanıcının mevcut kimlik doğrulama durumunu kontrol eder
    fun checkAuthStatus() {
        if(auth.currentUser == null){
            _authState.value = AuthState.Unauthenticated
        }else{
            _authState.value = AuthState.Authenticated
        }
    }

    fun signin(email : String, password : String){
        // Eğer kullanıcı email veya şifre alanlarını boş bırakmışsa hata mesajı döner ve fonksiyon sonlanır
        if (email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }

        // Giriş işlemi başladığında loading durumunu ayarlar
        _authState.value = AuthState.Loading

        // Firebase Authentication ile email ve şifre kullanarak giriş yapar.
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                // Giriş işlemi başarılıysa, kimlik doğrulama durumu kimlik doğrulandı olarak ayarlanır
                if(it.isSuccessful){
                    _authState.value = AuthState.Authenticated
                }else{
                    // Giriş işlemi başarısızsa, hata mesajı ile birlikte error mesajı verilir
                    _authState.value = AuthState.Error(it.exception?.message?:"Something went wrong")
                }
            }
    }


    fun registerPerson(email : String, password : String, role : String, area: String, context: Context){

        val personMap = hashMapOf(
            "email" to email,
            "password" to password,
            "role_name" to role,
            "role_area" to area
        )

        db.collection("role_registration").add(personMap)
            .addOnSuccessListener {
                Toast.makeText(context,"basarili kayit",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Kayıt sırasında hata: ${e.message}", Toast.LENGTH_SHORT).show()
                println(e)
            }

        /*
        // Eğer kullanıcı email veya şifre alanlarını boş bırakmışsa hata mesajı döner ve fonksiyon sonlanır
        if (email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }

        // Kayıt işlemi başladığında loading durumunu ayarlar
        _authState.value = AuthState.Loading

        // Firebase Authentication ile email ve şifre kullanarak yeni bir kullanıcı oluşturulur
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                // Kayıt işlemi başarılıysa, kimlik doğrulama durumu kimlik doğrulandı olarak ayarlanır
                if(it.isSuccessful){
                    _authState.value = AuthState.Authenticated
                }else{
                    // Kayıt işlemi başarısızsa, hata mesajı ile birlikte error mesajı verilir
                    _authState.value = AuthState.Error(it.exception?.message?:"Something went wrong")
                }
            }
            */

    }


    // Kullanıcının oturumunu kapatır ve kimlik doğrulama durumunu kimlik doğrulanmadı olarak ayarlar
    fun signout(){
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }




    // Kullanıcının kimlik doğrulama durumunu temsil eder
    // Sealed class, sınıflar arasında kısıtlı bir hiyerarşi sağlar ve tüm alt sınıfları aynı dosyada tanımlamanızı gerektirir.
    sealed class AuthState{
        object Authenticated : AuthState()
        object Unauthenticated : AuthState()
        object Loading : AuthState()
        data class Error(val message : String) : AuthState()
    }

}
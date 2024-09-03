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

    fun signIn(email : String, password : String){
        // Eğer kullanıcı email veya şifre alanlarını boş bırakmışsa hata mesajı döner ve fonksiyon sonlanır
        if (email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }

        // Giriş işlemi başladığında loading durumunu ayarlar
        _authState.value = AuthState.Loading
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

        if (email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }

        var roleId: Long? = null
        var areaId: Long? = null

        db.collection("roles")
            .whereEqualTo("role_name", role)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    roleId = document.getLong("role_id")
                }
            }

        db.collection("areas")
            .whereEqualTo("area_name", area)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    areaId = document.getLong("area_id")
                }
            }


        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid
                    val userMap = hashMapOf(
                        "email" to email,
                        "password" to password,
                        "role_id" to roleId,
                        "area_id" to areaId
                    )
                    userId?.let {
                        db.collection("user_registration").document(it).set(userMap)
                            .addOnSuccessListener {
                                // Kullanıcı bilgileri başarıyla kaydedildi
                            }
                            .addOnFailureListener { e ->
                                // Hata durumunda işlem yapın
                            }
                    }
                } else {
                    // Kayıt başarısız, hata mesajını işleyin
                }
            }
    }



    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
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
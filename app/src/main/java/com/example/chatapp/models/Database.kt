package com.example.chatapp.models

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.auth.AuthState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

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

    fun saveUserProfile(userName: String, aboutMe: String, imageUri: String?) {
        // Profil resmini Firebase Storage'a yükle ve URL'sini al
        val userId = auth.currentUser?.uid
        val storageRef: StorageReference = FirebaseStorage.getInstance().reference
        Log.e("Saturn",imageUri.toString())
        val profileImagesRef = imageUri?.let {
            storageRef.child("profile_images/${userId}.jpg")
        }

        profileImagesRef?.putFile(Uri.parse(imageUri))?.addOnSuccessListener { taskSnapshot ->
            // Resim yüklendikten sonra URL'sini al
            profileImagesRef.downloadUrl.addOnSuccessListener { downloadUri ->
                val imageUrl = downloadUri.toString()
                Log.e("Saturn",imageUrl)
                saveProfile(userId.toString(), userName, aboutMe, imageUrl)
            }
        }?.addOnFailureListener { e ->
            Log.e("Database", "Resim URL'si alınırken hata oluştu", e)
            // Resim yükleme hatası, resim URL'siz olarak Firestore'a kaydet
            saveProfile(userId.toString(), userName, aboutMe, null)
        } ?: run {
            // Resim yoksa direkt olarak Firestore'a kaydet
            saveProfile(userId.toString(), userName, aboutMe, null)
        }
    }

    fun saveProfile(userId: String, userName: String, aboutMe: String, imageUrl: String?) {
        val userProfile = hashMapOf(
            "name" to userName,
            "about_me" to aboutMe,
            "profile_image" to imageUrl
        )

        // Kullanıcı profili koleksiyonuna veri ekleme
        if (imageUrl != null){
            db.collection("user_profile").document(userId)
                .set(userProfile)
                .addOnSuccessListener {
                    // Veriler başarılı bir şekilde kaydedildi
                }
                .addOnFailureListener { e ->
                    // Veri kaydetme hatası
                }
        }

    }


    fun getUserProfile(userId: String, onResult: (String, String, Uri?) -> Unit) {
        db.collection("user_profile").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val userName = document.getString("name") ?: ""
                    val aboutMe = document.getString("about_me") ?: ""
                    val imageUrl = document.getString("profile_image")
                    val imageUri = imageUrl?.let { Uri.parse(it) }
                    onResult(userName, aboutMe, imageUri)
                } else {
                    // Document not found, handle accordingly
                    onResult("", "", null)
                }
            }
            .addOnFailureListener { e ->
                // Handle the error
                onResult("", "", null)
            }
    }





    // Kullanıcının oturumunu kapatır ve kimlik doğrulama durumunu kimlik doğrulanmadı olarak ayarlar
    fun signOut(){
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
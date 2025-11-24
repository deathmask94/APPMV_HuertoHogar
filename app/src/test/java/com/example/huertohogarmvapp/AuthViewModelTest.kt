package com.example.huertohogarmvapp



import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentReference
import io.mockk.*
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import com.example.huertohogarmvapp.viewmodel.AuthViewModel
import com.example.huertohogarmvapp.model.UserModel

class AuthViewModelTest {

    // 1. Mocks de las instancias estáticas de Firebase
    private val mockAuth = mockk<FirebaseAuth>()
    private val mockFirestore = mockk<FirebaseFirestore>()

    // 2. ViewModel a probar
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        // Mocking de las llamadas estáticas de Firebase
        // Esto redirige Firebase.auth y Firebase.firestore a nuestros mocks
        mockkStatic(FirebaseAuth::class)
        every { FirebaseAuth.getInstance() } returns mockAuth

        mockkStatic(FirebaseFirestore::class)
        every { FirebaseFirestore.getInstance() } returns mockFirestore

        // Inicializamos el ViewModel DESPUÉS de configurar los mocks estáticos
        viewModel = AuthViewModel()
    }

    // Método genérico para simular el éxito de cualquier Task de Firebase
    private inline fun <T> mockTask(value: T?): Task<T> {
        val slot = slot<OnCompleteListener<T>>() // Declaramos el slot fuera del block

        return mockk<Task<T>> {
            every { isSuccessful } returns true
            every { result } returns value
            every { exception } returns null // Aseguramos que no hay excepción en caso de éxito

            // Capturamos el slot y, al mismo tiempo, ejecutamos el listener capturado.
            every { addOnCompleteListener(capture(slot)) } answers {
                // Ejecutamos el listener capturado inmediatamente
                slot.captured.onComplete(self as Task<T>)
                self as Task<T> // Devolvemos el mock para encadenamiento
            }
        }
    }

    // Método genérico para simular el fallo de cualquier Task de Firebase
    private inline fun <T> mockTaskFailure(exception: Exception? = Exception("Test Failure")): Task<T> {
        val slot = slot<OnCompleteListener<T>>() // Declaramos el slot fuera del block

        return mockk<Task<T>> {
            every { isSuccessful } returns false
            every { result } returns null // El resultado es nulo en caso de fallo
            every { this@mockk.exception } returns exception // Referencia explícita a la propiedad

            // Capturamos el slot y ejecutamos el listener inmediatamente con el fallo
            every { addOnCompleteListener(capture(slot)) } answers {
                slot.captured.onComplete(self as Task<T>)
                self as Task<T>
            }
        }
    }

// PRUEBAS CONTINUACIÓN...

// ... Código de setup y métodos mockTask ...

    // ⭐ PRUEBA 1: Login Exitoso ⭐
    @Test
    fun `login with valid credentials should call onResult true and null error`() {
        // Arrange: Simular que signInWithEmailAndPassword devuelve un resultado exitoso
        val mockAuthResult = mockk<AuthResult>()
        val mockLoginTask = mockTask(mockAuthResult)

        // Cuando se llame a signInWithEmailAndPassword en el mockAuth, devuelve nuestra Task simulada
        every { mockAuth.signInWithEmailAndPassword("test@mail.com", "password123") } returns mockLoginTask

        var actualSuccess: Boolean? = null
        var actualError: String? = "Initial Error"

        // Act
        viewModel.login("test@mail.com", "password123") { success, error ->
            actualSuccess = success
            actualError = error
        }

        // Assert
        assertTrue(actualSuccess!!)
        assertNull(actualError)
    }

    // ⭐ PRUEBA 5: Manejo de Errores de Login (Error de Credenciales) ⭐
    @Test
    fun `login with invalid credentials should call onResult false and return error message`() {
        // Arrange: Simular que signInWithEmailAndPassword devuelve un fallo
        val expectedException = Exception("INVALID_CREDENTIALS")
        val mockErrorTask = mockTaskFailure<AuthResult>(expectedException)

        // Cuando se llame a signInWithEmailAndPassword, devuelve nuestra Task de fallo simulada
        every { mockAuth.signInWithEmailAndPassword("bad@mail.com", "wrongpass") } returns mockErrorTask

        var actualSuccess: Boolean? = null
        var actualError: String? = null

        // Act
        viewModel.login("bad@mail.com", "wrongpass") { success, error ->
            actualSuccess = success
            actualError = error
        }

        // Assert
        assertFalse(actualSuccess!!)
        assertEquals("INVALID_CREDENTIALS", actualError)
    }

    // ⭐ PRUEBA 2: Registro Exitoso y Guardado en Firestore ⭐
    @Test
    fun `signup should successfully create user and save model to firestore`() {
        // 1. Simular la creación de usuario exitosa
        val mockUser = mockk<FirebaseUser>()
        val testUID = "test-uid-123"
        every { mockUser.uid } returns testUID

        val mockAuthResult = mockk<AuthResult>()
        every { mockAuthResult.user } returns mockUser
        val mockSignupTask = mockTask(mockAuthResult)

        every { mockAuth.createUserWithEmailAndPassword("new@user.com", "securepass") } returns mockSignupTask

        // 2. Simular el guardado exitoso en Firestore
        val mockCollection = mockk<CollectionReference>()
        val mockDocument = mockk<DocumentReference>()
        val mockDbTask = mockTask<Void>(null) // set() devuelve Task<Void>

        every { mockFirestore.collection("users") } returns mockCollection
        every { mockCollection.document(testUID) } returns mockDocument
        // Cuando se llame a set(), devuelve nuestra Task simulada de éxito
        every { mockDocument.set(any(UserModel::class)) } returns mockDbTask

        var actualSuccess: Boolean? = null
        var actualError: String? = "Initial Error"

        // Act
        viewModel.signup("new@user.com", "Test User", "securepass") { success, error ->
            actualSuccess = success
            actualError = error
        }

        // Assert
        assertTrue(actualSuccess!!)
        assertNull(actualError)

        // Opcional: Verificar que el método set() de Firestore fue llamado exactamente una vez
        verify(exactly = 1) { mockDocument.set(any<UserModel>()) }
    }

    @Test
    fun `signup should fail if auth succeeds but firestore saving fails`() {
        // Arrange 1: Simular la creación de usuario exitosa (Auth OK)
        val mockUser = mockk<FirebaseUser>()
        val testUID = "test-uid-123"
        val expectedDbError = "Algo salio mal :/ , no se ha podido crear el usuario."

        every { mockUser.uid } returns testUID
        val mockAuthResult = mockk<AuthResult>()
        every { mockAuthResult.user } returns mockUser
        val mockSignupTask = mockTask(mockAuthResult) // Usamos mockTask para éxito de Auth

        every { mockAuth.createUserWithEmailAndPassword("fail@db.com", "password") } returns mockSignupTask

        // Arrange 2: Simular el fallo de guardado en Firestore
        val mockCollection = mockk<CollectionReference>()
        val mockDocument = mockk<DocumentReference>()

        // Usamos mockTaskFailure para simular que la Task<Void> de set() falla
        val mockDbTaskFailure = mockTaskFailure<Void>(Exception("DB Write Failed"))

        every { mockFirestore.collection("users") } returns mockCollection
        every { mockCollection.document(testUID) } returns mockDocument
        // Cuando se llama a set(), devolvemos nuestra Task simulada de fallo
        every { mockDocument.set(any(UserModel::class)) } returns mockDbTaskFailure

        var actualSuccess: Boolean? = null
        var actualError: String? = null

        // Act
        viewModel.signup("fail@db.com", "Test User", "password") { success, error ->
            actualSuccess = success
            actualError = error
        }

        // Assert
        assertFalse(actualSuccess!!)
        // Verificamos que se devuelve el mensaje de error específico del ViewModel
        assertEquals(expectedDbError, actualError)

        // Opcional: Verificar que el método set() fue llamado una vez antes de fallar
        verify(exactly = 1) { mockDocument.set(any(UserModel::class)) }
    }
}
package com.example.huertohogarmvapp

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.*
import com.google.firebase.Firebase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import com.google.firebase.firestore.firestore

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], manifest = Config.NONE)
class AppUtilTest {

    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var mockAuth: FirebaseAuth
    private lateinit var mockUser: FirebaseUser
    private lateinit var mockUserDoc: DocumentReference
    private lateinit var mockCollection: CollectionReference
    private lateinit var context: Context

    private val testUID = "test-user-id"
    private val testProductId = "P001"

    private fun <T> createSuccessTask(result: T): Task<T> {
        val task = mockk<Task<T>>(relaxed = true)
        every { task.isSuccessful } returns true
        every { task.result } returns result
        every { task.exception } returns null
        every { task.addOnCompleteListener(any()) } answers {
            val listener = firstArg<OnCompleteListener<T>>()
            listener.onComplete(task)
            task
        }
        return task
    }

    private fun <T> createFailureTask(): Task<T> {
        val task = mockk<Task<T>>(relaxed = true)
        every { task.isSuccessful } returns false
        every { task.result } throws Exception("Task failed")
        every { task.exception } returns Exception("Task failed")
        every { task.addOnCompleteListener(any()) } answers {
            val listener = firstArg<OnCompleteListener<T>>()
            listener.onComplete(task)
            task
        }
        return task
    }

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()

        // Create mocks
        mockFirestore = mockk(relaxed = true)
        mockAuth = mockk(relaxed = true)
        mockUser = mockk(relaxed = true)
        mockUserDoc = mockk(relaxed = true)
        mockCollection = mockk(relaxed = true)

        // Mock FirebaseAuth
        mockkStatic(FirebaseAuth::class)
        every { FirebaseAuth.getInstance() } returns mockAuth
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns testUID

        // Mock la extension property Firebase.firestore
        mockkStatic("com.google.firebase.firestore.FirestoreKt")
        every { Firebase.firestore } returns mockFirestore

        // Setup Firestore chain
        every { mockFirestore.collection("users") } returns mockCollection
        every { mockCollection.document(testUID) } returns mockUserDoc
    }

    @Test
    fun `addItemToCart should successfully increase quantity`() {
        val initialCart: Map<String, Long> = mapOf(testProductId to 5L, "P002" to 2L)
        val mockSnapshot = mockk<DocumentSnapshot>(relaxed = true)
        every { mockSnapshot.exists() } returns true
        every { mockSnapshot.get("cartItems") } returns initialCart

        val mockGetTask = createSuccessTask(mockSnapshot)
        every { mockUserDoc.get() } returns mockGetTask

        val mockUpdateTask = createSuccessTask<Void?>(null)
        every { mockUserDoc.update(any<Map<String, Any>>()) } returns mockUpdateTask

        AppUtil.addItemToCart(testProductId, context)

        verify(exactly = 1) {
            mockUserDoc.update(match { map: Map<String, Any> ->
                map.containsKey("cartItems.$testProductId") &&
                        map["cartItems.$testProductId"] == 6L
            })
        }
    }

    @Test
    fun `removeFromCart should remove item field when final quantity is zero or less`() {
        val initialCart: Map<String, Long> = mapOf(testProductId to 1L)
        val mockSnapshot = mockk<DocumentSnapshot>(relaxed = true)
        every { mockSnapshot.exists() } returns true
        every { mockSnapshot.get("cartItems") } returns initialCart

        val mockGetTask = createSuccessTask(mockSnapshot)
        every { mockUserDoc.get() } returns mockGetTask

        mockkStatic(FieldValue::class)
        val mockDeleteValue = mockk<FieldValue>()
        every { FieldValue.delete() } returns mockDeleteValue

        val mockUpdateTask = createSuccessTask<Void?>(null)
        every { mockUserDoc.update(any<Map<String, Any>>()) } returns mockUpdateTask

        AppUtil.removeFromCart(testProductId, context)

        verify(exactly = 1) {
            mockUserDoc.update(match { map: Map<String, Any> ->
                map.containsKey("cartItems.$testProductId")
            })
        }
    }

    @Test
    fun `removeFromCart should decrease quantity when more than one item exists`() {
        val initialCart: Map<String, Long> = mapOf(testProductId to 3L)
        val mockSnapshot = mockk<DocumentSnapshot>(relaxed = true)
        every { mockSnapshot.exists() } returns true
        every { mockSnapshot.get("cartItems") } returns initialCart

        val mockGetTask = createSuccessTask(mockSnapshot)
        every { mockUserDoc.get() } returns mockGetTask

        val mockUpdateTask = createSuccessTask<Void?>(null)
        every { mockUserDoc.update(any<Map<String, Any>>()) } returns mockUpdateTask

        AppUtil.removeFromCart(testProductId, context)

        verify(exactly = 1) {
            mockUserDoc.update(match { map: Map<String, Any> ->
                map.containsKey("cartItems.$testProductId") &&
                        map["cartItems.$testProductId"] == 2L
            })
        }
    }

    @Test
    fun `addItemToCart should handle error when fetching cart fails`() {
        val mockGetTask = createFailureTask<DocumentSnapshot>()
        every { mockUserDoc.get() } returns mockGetTask

        AppUtil.addItemToCart(testProductId, context)

        verify(exactly = 0) { mockUserDoc.update(any<Map<String, Any>>()) }
    }
}
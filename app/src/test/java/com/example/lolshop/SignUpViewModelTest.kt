package com.example.lolshop

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.lolshop.repository.UserRepository
import com.example.lolshop.utils.Resource
import com.example.lolshop.viewmodel.authentication.SignUpViewModel
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito


@ExperimentalCoroutinesApi
class SignUpViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()  // This rule allows LiveData to work on the main thread for unit testing.

    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var userRepository: UserRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        userRepository = mockk()  // Mocking UserRepository
        signUpViewModel = SignUpViewModel(userRepository)
    }

    @Test
    fun `test signUp with empty name`() = runTest {
        // Act: Call the checkEmpty function with empty name
        val result = signUpViewModel.checkEmpty("", "john@example.com", "password", "1234567890", "Address")

        // Assert: Verify that the validation fails and error state is set
        assertFalse(result)
        val errorState = signUpViewModel.signUpState.first()
        assertTrue(errorState is Resource.Error)
        assertEquals("Name is required", (errorState as Resource.Error).message)
    }

    @Test
    fun `test signUp with empty email`() = runTest {
        // Act: Call the checkEmpty function with empty email
        val result = signUpViewModel.checkEmpty("John Doe", "", "password", "1234567890", "Address")

        // Assert: Verify that the validation fails and error state is set
        assertFalse(result)
        val errorState = signUpViewModel.signUpState.first()
        assertTrue(errorState is Resource.Error)
        assertEquals("Email is required", (errorState as Resource.Error).message)
    }

    @Test
    fun `test clearError resets the state`() = runTest {
        // Arrange: Set an error state
        signUpViewModel._signUpState.value = Resource.Error("Some error occurred")

        // Act: Call clearError
        signUpViewModel.clearError()

        // Assert: Verify that the state is reset to Empty
        val result = signUpViewModel.signUpState.first()
        assertTrue(result is Resource.Empty)
    }
}

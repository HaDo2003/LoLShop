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
    fun `test signUp success`() = runTest {
        // Arrange: Set up repository to return a successful result
        Mockito.`when`(userRepository.signUpUser("John Doe", "john@example.com", "password", "1234567890", "Address"))
            .thenReturn(Result.success(Unit))

        // Act: Call the signUp function
        signUpViewModel.signUp("John Doe", "john@example.com", "password", "1234567890", "Address")

        // Assert: Verify that the result is Success and verify that the state is updated
        val result = signUpViewModel.signUpState.first()
        assertTrue(result is Resource.Success)
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
    fun `test signUp with repository failure`() = runTest {
        // Arrange: Set up repository to return a failure result
        Mockito.`when`(userRepository.signUpUser("John Doe", "john@example.com", "password", "1234567890", "Address"))
            .thenReturn(Result.failure(Exception("Sign up failed")))

        // Act: Call the signUp function
        signUpViewModel.signUp("John Doe", "john@example.com", "password", "1234567890", "Address")

        // Assert: Verify that the result is Error and error message is correct
        val result = signUpViewModel.signUpState.first()
        assertTrue(result is Resource.Error)
        assertEquals("Sign up failed", (result as Resource.Error).message)
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

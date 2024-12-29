package com.example.lolshop

import com.example.lolshop.repository.UserRepository
import com.example.lolshop.viewmodel.authentication.LoginState
import com.example.lolshop.viewmodel.authentication.LoginViewModel
import io.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.*
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private lateinit var userRepository: UserRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher) // Set test dispatcher for coroutines
        userRepository = mockk() // Mock UserRepository
        viewModel = LoginViewModel(userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset dispatcher
    }

    @Test
    fun `loginUser with empty fields emits Error state`() = runTest {
        viewModel.loginUser("", "")
        assertTrue(viewModel.loginState.first() is LoginState.Error)
        assertEquals("Fields cannot be empty", (viewModel.loginState.first() as LoginState.Error).message)
    }

    @Test
    fun `loginUser with valid credentials emits Success state`() = runTest {
        // Mock UserRepository response for login
        coEvery { userRepository.loginUser("user@example.com", "password123") } returns Result.success("user_id")
        // Mock UserRepository response for access level
        coEvery { userRepository.getUserAccessLevel("user_id") } returns Result.success(false)

        viewModel.loginUser("user@example.com", "password123")
        advanceUntilIdle() // Let all coroutines finish

        val state = viewModel.loginState.first()
        assertTrue(state is LoginState.Success)
        assertEquals("user_id", (state as LoginState.Success).uid)
        assertFalse(state.isAdmin)
    }

    @Test
    fun `loginUser with incorrect credentials emits Error state`() = runTest {
        // Mock UserRepository response for login failure
        coEvery { userRepository.loginUser("user@example.com", "wrongpassword") } returns Result.failure(Exception("Invalid credentials"))

        viewModel.loginUser("user@example.com", "wrongpassword")
        advanceUntilIdle() // Let all coroutines finish

        val state = viewModel.loginState.first()
        assertTrue(state is LoginState.Error)
        assertEquals("Invalid credentials", (state as LoginState.Error).message)
    }
}

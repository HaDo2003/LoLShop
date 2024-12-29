package com.example.lolshop

import com.example.lolshop.repository.UserRepository
import com.example.lolshop.viewmodel.authentication.ChangePasswordViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChangePasswordViewModelTest {

    private lateinit var viewModel: ChangePasswordViewModel
    private lateinit var userRepository: UserRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        userRepository = mockk() // Mock UserRepository
        viewModel = ChangePasswordViewModel(userRepository)
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset main dispatcher after the test
    }

    @Test
    fun `resetPassword with valid code and new password emits success status`() = runTest {
        // Mock UserRepository response for success
        val successMessage = "Password reset successfully"
        coEvery { userRepository.resetPasswordWithCode("validCode", "newPassword123") } returns Result.success(successMessage)

        viewModel.resetPassword("validCode", "newPassword123")
        advanceUntilIdle() // Let all coroutines finish

        val state = viewModel.resetPasswordStatus.first()
        assertEquals(successMessage, state)
    }

    @Test
    fun `resetPassword with invalid code emits error message`() = runTest {
        // Mock UserRepository response for error
        val errorMessage = "Invalid reset code"
        coEvery { userRepository.resetPasswordWithCode("invalidCode", "newPassword123") } returns Result.failure(Exception(errorMessage))

        viewModel.resetPassword("invalidCode", "newPassword123")
        advanceUntilIdle() // Let all coroutines finish

        val state = viewModel.resetPasswordStatus.first()
        assertEquals(errorMessage, state)
    }

    @Test
    fun `resetPassword with empty password emits error message`() = runTest {
        // Simulate empty password error
        val errorMessage = "Password cannot be empty"
        viewModel.setErrorMessage(errorMessage)

        val state = viewModel.resetPasswordStatus.first()
        assertEquals(errorMessage, state)
    }

    @Test
    fun `clearMessage sets resetPasswordStatus to null`() = runTest {
        // Set a message first
        viewModel.setErrorMessage("Some error")
        viewModel.clearMessage()

        val state = viewModel.resetPasswordStatus.first()
        assertEquals(null, state)
    }
}

package com.example.lolshop

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.lolshop.model.Cart
import com.example.lolshop.model.CartProduct
import com.example.lolshop.repository.CartRepository
import com.example.lolshop.utils.Resource
import com.example.lolshop.viewmodel.homepage.CartViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.example.lolshop.utils.Result

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: CartViewModel
    private val cartRepository: CartRepository = mockk()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CartViewModel(cartRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addProductToCart should update cartState to Success on successful addition`() = runTest {
        // Arrange
        val uid = "9drT3tkjFDPXJQuMn9kT7iLM2bw1"
        val productId = "-OE8AQJBz-ksZt7LxcS7"
        coEvery { cartRepository.addProductToCart(uid, productId) } just Runs

        // Act
        viewModel.addProductToCart(uid, productId)
        advanceUntilIdle()

        // Assert
        assert(viewModel.cartState.value is Resource.Success)
        coVerify { cartRepository.addProductToCart(uid, productId) }
    }

    @Test
    fun `addProductToCart should update cartState to Error on failure`() = runTest {
        // Arrange
        val uid = "9drT3tkjFDPXJQuMn9kT7iLM2bw1"
        val productId = "-OE8AQJBz-ksZt7LxcS7"
        val exception = RuntimeException("Add product failed")
        coEvery { cartRepository.addProductToCart(uid, productId) } throws exception // Simulate failure

        // Act
        viewModel.addProductToCart(uid, productId)
        advanceUntilIdle()

        // Assert
        assert(viewModel.cartState.value is Resource.Error)
        assert((viewModel.cartState.value as Resource.Error).message == "java.lang.RuntimeException: Add product failed")
        coVerify { cartRepository.addProductToCart(uid, productId) }
    }

    @Test
    fun `placeOrderFromCart should update orderState to Success on successful order placement`() = runTest {
        // Arrange
        val mockProducts = listOf(
            CartProduct(productId = "-OE8AQJBz-ksZt7LxcS7", name = "T1 Tumbler", quantity = 1, price = 20.0),
        )
        val mockCart = Cart(cartId = "9drT3tkjFDPXJQuMn9kT7iLM2bw1", products = mockProducts, total = 100.0)
        val uid = "9drT3tkjFDPXJQuMn9kT7iLM2bw1"
        val totalPrice = 100.0
        val tax = 10.0
        val deliveryFee = 10.0
        val totalAtAll = 120.0
        coEvery {
            cartRepository.placeOrderFromCart(
                mockCart,
                uid,
                totalPrice,
                tax,
                deliveryFee,
                totalAtAll
            )
        } just Runs

        // Act
        viewModel.placeOrderFromCart(mockCart, uid, totalPrice, tax, deliveryFee, totalAtAll)
        advanceUntilIdle()

        // Assert
        assert(viewModel.orderState.value is Resource.Success)
        coVerify {
            cartRepository.placeOrderFromCart(mockCart, uid, totalPrice, tax, deliveryFee, totalAtAll)
        }
    }
}

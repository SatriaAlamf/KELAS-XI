package com.jetpackcompose.foodorderinguser.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.jetpackcompose.foodorderinguser.adapters.CartAdapter
import com.jetpackcompose.foodorderinguser.databinding.FragmentCartBinding
import com.jetpackcompose.foodorderinguser.models.Cart
import com.jetpackcompose.foodorderinguser.repository.CartRepository
import com.jetpackcompose.foodorderinguser.utils.FormatUtils
import com.jetpackcompose.foodorderinguser.utils.SharedPrefsManager
import kotlinx.coroutines.launch

class CartFragment : Fragment() {
    
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var cartRepository: CartRepository
    private lateinit var sharedPrefsManager: SharedPrefsManager
    private lateinit var cartAdapter: CartAdapter
    
    private var currentCart: Cart? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        cartRepository = CartRepository()
        sharedPrefsManager = SharedPrefsManager(requireContext())
        
        setupRecyclerView()
        setupClickListeners()
        loadCart()
    }
    
    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onQuantityChanged = { cartItem, newQuantity ->
                updateItemQuantity(cartItem.id, newQuantity)
            },
            onItemRemoved = { cartItem ->
                removeItem(cartItem.id)
            }
        )
        
        binding.rvCartItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.btnCheckout.setOnClickListener {
            if (currentCart?.items?.isNotEmpty() == true) {
                proceedToCheckout()
            } else {
                Toast.makeText(requireContext(), "Your cart is empty", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.btnContinueShopping.setOnClickListener {
            // Navigate back to home fragment
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, HomeFragment())
                .addToBackStack(null)
                .commit()
        }
    }
    
    private fun loadCart() {
        val userId = sharedPrefsManager.getUserId()
        if (userId.isEmpty()) {
            showEmptyCart()
            return
        }
        
        lifecycleScope.launch {
            cartRepository.getCart(userId)
                .onSuccess { cart ->
                    currentCart = cart
                    if (cart == null || cart.items.isEmpty()) {
                        showEmptyCart()
                    } else {
                        showCartItems(cart)
                    }
                }
                .onFailure { error ->
                    Toast.makeText(requireContext(), "Error loading cart: ${error.message}", Toast.LENGTH_SHORT).show()
                    showEmptyCart()
                }
        }
    }
    
    private fun showEmptyCart() {
        binding.layoutEmptyCart.visibility = View.VISIBLE
        binding.layoutCartContent.visibility = View.GONE
    }
    
    private fun showCartItems(cart: Cart) {
        binding.layoutEmptyCart.visibility = View.GONE
        binding.layoutCartContent.visibility = View.VISIBLE
        
        cartAdapter.submitList(cart.items)
        updateCartSummary(cart)
    }
    
    private fun updateCartSummary(cart: Cart) {
        val calculatedCart = cart.calculateTotals()
        
        binding.tvSubtotal.text = FormatUtils.formatPrice(calculatedCart.subtotal)
        binding.tvDeliveryFee.text = FormatUtils.formatPrice(calculatedCart.deliveryFee)
        binding.tvTax.text = FormatUtils.formatPrice(calculatedCart.tax)
        binding.tvDiscount.text = FormatUtils.formatPrice(calculatedCart.discount)
        binding.tvTotal.text = FormatUtils.formatPrice(calculatedCart.total)
        
        // Show/hide discount row
        binding.layoutDiscount.visibility = if (calculatedCart.discount > 0) View.VISIBLE else View.GONE
    }
    
    private fun updateItemQuantity(cartItemId: String, newQuantity: Int) {
        val userId = sharedPrefsManager.getUserId()
        
        lifecycleScope.launch {
            cartRepository.updateItemQuantity(userId, cartItemId, newQuantity)
                .onSuccess {
                    loadCart() // Reload cart to get updated totals
                }
                .onFailure { error ->
                    Toast.makeText(requireContext(), "Error updating quantity: ${error.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
    
    private fun removeItem(cartItemId: String) {
        val userId = sharedPrefsManager.getUserId()
        
        lifecycleScope.launch {
            cartRepository.removeItemFromCart(userId, cartItemId)
                .onSuccess {
                    Toast.makeText(requireContext(), "Item removed from cart", Toast.LENGTH_SHORT).show()
                    loadCart()
                }
                .onFailure { error ->
                    Toast.makeText(requireContext(), "Error removing item: ${error.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
    
    private fun proceedToCheckout() {
        // TODO: Implement checkout functionality
        Toast.makeText(requireContext(), "Checkout functionality coming soon!", Toast.LENGTH_SHORT).show()
    }
    
    override fun onResume() {
        super.onResume()
        loadCart() // Refresh cart when fragment becomes visible
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
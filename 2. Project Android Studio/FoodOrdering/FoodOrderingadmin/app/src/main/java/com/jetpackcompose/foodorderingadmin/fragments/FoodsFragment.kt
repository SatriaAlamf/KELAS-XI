package com.jetpackcompose.foodorderingadmin.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.jetpackcompose.foodorderingadmin.activities.AddEditFoodActivity
import com.jetpackcompose.foodorderingadmin.adapters.FoodAdapter
import com.jetpackcompose.foodorderingadmin.databinding.FragmentFoodsBinding
import com.jetpackcompose.foodorderingadmin.models.Food
import com.jetpackcompose.foodorderingadmin.repository.FoodRepository
import com.jetpackcompose.foodorderingadmin.utils.Constants
import kotlinx.coroutines.launch

class FoodsFragment : Fragment() {

    private var _binding: FragmentFoodsBinding? = null
    private val binding get() = _binding!!
    
    private val foodRepository = FoodRepository()
    private lateinit var foodAdapter: FoodAdapter
    private var allFoods = listOf<Food>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupSwipeRefresh()
        setupSearch()
        setupClickListeners()
        loadFoods()
    }

    private fun setupRecyclerView() {
        foodAdapter = FoodAdapter(
            onEditClick = { food ->
                openAddEditFood(food)
            },
            onDeleteClick = { food ->
                showDeleteConfirmation(food)
            },
            onAvailabilityChanged = { food, isAvailable ->
                updateFoodAvailability(food.id, isAvailable)
            }
        )
        
        binding.rvFoods.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = foodAdapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            loadFoods()
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterFoods(s.toString())
            }
        })
    }

    private fun setupClickListeners() {
        binding.btnAddFood.setOnClickListener {
            openAddEditFood(null)
        }
    }

    private fun loadFoods() {
        binding.swipeRefresh.isRefreshing = true
        
        lifecycleScope.launch {
            val result = foodRepository.getAllFoods()
            
            result.onSuccess { foods ->
                allFoods = foods
                foodAdapter.submitList(foods)
            }.onFailure { exception ->
                Toast.makeText(
                    requireContext(),
                    "Failed to load foods: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun filterFoods(query: String) {
        val filteredFoods = if (query.isEmpty()) {
            allFoods
        } else {
            allFoods.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true) ||
                it.categoryName.contains(query, ignoreCase = true)
            }
        }
        foodAdapter.submitList(filteredFoods)
    }

    private fun openAddEditFood(food: Food?) {
        val intent = Intent(requireContext(), AddEditFoodActivity::class.java).apply {
            food?.let {
                putExtra(Constants.EXTRA_FOOD_ID, it.id)
                putExtra(Constants.EXTRA_IS_EDIT_MODE, true)
            }
        }
        startActivity(intent)
    }

    private fun showDeleteConfirmation(food: Food) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Food")
            .setMessage("Are you sure you want to delete ${food.name}?")
            .setPositiveButton("Delete") { _, _ ->
                deleteFood(food.id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteFood(foodId: String) {
        lifecycleScope.launch {
            val result = foodRepository.deleteFood(foodId)
            
            result.onSuccess {
                Toast.makeText(
                    requireContext(),
                    "Food deleted successfully",
                    Toast.LENGTH_SHORT
                ).show()
                loadFoods()
            }.onFailure { exception ->
                Toast.makeText(
                    requireContext(),
                    "Failed to delete food: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateFoodAvailability(foodId: String, isAvailable: Boolean) {
        lifecycleScope.launch {
            val result = foodRepository.toggleFoodAvailability(foodId, isAvailable)
            
            result.onFailure { exception ->
                Toast.makeText(
                    requireContext(),
                    "Failed to update availability: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
                loadFoods() // Reload to reset the switch
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadFoods() // Reload when returning from add/edit activity
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

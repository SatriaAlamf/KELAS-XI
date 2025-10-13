package com.jetpackcompose.foodorderinguser.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.jetpackcompose.foodorderinguser.activities.CategoryFoodsActivity
import com.jetpackcompose.foodorderinguser.activities.FoodDetailsActivity
import com.jetpackcompose.foodorderinguser.adapters.CategoryAdapter
import com.jetpackcompose.foodorderinguser.adapters.FoodAdapter
import com.jetpackcompose.foodorderinguser.databinding.FragmentHomeBinding
import com.jetpackcompose.foodorderinguser.models.Category
import com.jetpackcompose.foodorderinguser.models.Food
import com.jetpackcompose.foodorderinguser.repository.FoodRepository
import com.jetpackcompose.foodorderinguser.utils.Constants
import com.jetpackcompose.foodorderinguser.utils.SharedPrefsManager
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var foodRepository: FoodRepository
    private lateinit var sharedPrefsManager: SharedPrefsManager
    
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var popularFoodAdapter: FoodAdapter
    private lateinit var discountedFoodAdapter: FoodAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        foodRepository = FoodRepository()
        sharedPrefsManager = SharedPrefsManager(requireContext())
        
        setupUI()
        setupRecyclerViews()
        loadData()
    }
    
    private fun setupUI() {
        binding.tvUserName.text = "Hello, ${sharedPrefsManager.getUserName()}"
        
        // Search functionality (basic implementation)
        binding.etSearch.setOnEditorActionListener { _, _, _ ->
            val query = binding.etSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                searchFoods(query)
            }
            true
        }
    }
    
    private fun setupRecyclerViews() {
        // Categories RecyclerView
        categoryAdapter = CategoryAdapter { category ->
            openCategoryFoods(category)
        }
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }
        
        // Popular Foods RecyclerView
        popularFoodAdapter = FoodAdapter { food ->
            openFoodDetails(food)
        }
        binding.rvPopularFoods.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = popularFoodAdapter
        }
        
        // Discounted Foods RecyclerView
        discountedFoodAdapter = FoodAdapter { food ->
            openFoodDetails(food)
        }
        binding.rvDiscountedFoods.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = discountedFoodAdapter
        }
    }
    
    private fun loadData() {
        loadCategories()
        loadPopularFoods()
        loadDiscountedFoods()
    }
    
    private fun loadCategories() {
        lifecycleScope.launch {
            foodRepository.getAllCategories()
                .onSuccess { categories ->
                    categoryAdapter.submitList(categories)
                }
                .onFailure { error ->
                    Toast.makeText(requireContext(), "Error loading categories: ${error.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
    
    private fun loadPopularFoods() {
        lifecycleScope.launch {
            foodRepository.getPopularFoods()
                .onSuccess { foods ->
                    popularFoodAdapter.submitList(foods)
                    binding.tvPopularFoodsEmpty.visibility = if (foods.isEmpty()) View.VISIBLE else View.GONE
                }
                .onFailure { error ->
                    binding.tvPopularFoodsEmpty.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Error loading popular foods", Toast.LENGTH_SHORT).show()
                }
        }
    }
    
    private fun loadDiscountedFoods() {
        lifecycleScope.launch {
            foodRepository.getDiscountedFoods()
                .onSuccess { foods ->
                    discountedFoodAdapter.submitList(foods)
                    binding.tvDiscountedFoodsEmpty.visibility = if (foods.isEmpty()) View.VISIBLE else View.GONE
                }
                .onFailure { error ->
                    binding.tvDiscountedFoodsEmpty.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Error loading discounted foods", Toast.LENGTH_SHORT).show()
                }
        }
    }
    
    private fun searchFoods(query: String) {
        lifecycleScope.launch {
            foodRepository.searchFoods(query)
                .onSuccess { foods ->
                    // Handle search results (for now, just show toast)
                    Toast.makeText(requireContext(), "Found ${foods.size} items", Toast.LENGTH_SHORT).show()
                }
                .onFailure { error ->
                    Toast.makeText(requireContext(), "Search failed: ${error.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
    
    private fun openCategoryFoods(category: Category) {
        val intent = Intent(requireContext(), CategoryFoodsActivity::class.java)
        intent.putExtra(Constants.EXTRA_CATEGORY_ID, category.id)
        intent.putExtra(Constants.EXTRA_CATEGORY_NAME, category.name)
        startActivity(intent)
    }
    
    private fun openFoodDetails(food: Food) {
        val intent = Intent(requireContext(), FoodDetailsActivity::class.java)
        intent.putExtra(Constants.EXTRA_FOOD_ID, food.id)
        startActivity(intent)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
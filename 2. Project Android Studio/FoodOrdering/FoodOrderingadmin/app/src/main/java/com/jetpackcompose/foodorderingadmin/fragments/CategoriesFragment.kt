package com.jetpackcompose.foodorderingadmin.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.jetpackcompose.foodorderingadmin.adapters.CategoryAdapter
import com.jetpackcompose.foodorderingadmin.databinding.FragmentCategoriesBinding
import com.jetpackcompose.foodorderingadmin.models.Category
import com.jetpackcompose.foodorderingadmin.repository.CategoryRepository
import com.jetpackcompose.foodorderingadmin.utils.ValidationUtils
import kotlinx.coroutines.launch

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    
    private val categoryRepository = CategoryRepository()
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupSwipeRefresh()
        setupClickListeners()
        loadCategories()
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter(
            onEditClick = { category ->
                showAddEditCategoryDialog(category)
            },
            onDeleteClick = { category ->
                showDeleteConfirmation(category)
            },
            onActiveChanged = { category, isActive ->
                updateCategoryStatus(category.id, isActive)
            }
        )
        
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            loadCategories()
        }
    }

    private fun setupClickListeners() {
        binding.btnAddCategory.setOnClickListener {
            showAddEditCategoryDialog(null)
        }
    }

    private fun loadCategories() {
        binding.swipeRefresh.isRefreshing = true
        
        lifecycleScope.launch {
            val result = categoryRepository.getAllCategories()
            
            result.onSuccess { categories ->
                categoryAdapter.submitList(categories)
            }.onFailure { exception ->
                Toast.makeText(
                    requireContext(),
                    "Failed to load categories: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun showAddEditCategoryDialog(category: Category?) {
        val isEditMode = category != null
        val dialogView = LayoutInflater.from(requireContext()).inflate(
            android.R.layout.simple_list_item_2, null
        )
        
        val etName = EditText(requireContext()).apply {
            hint = "Category Name"
            setText(category?.name ?: "")
        }
        
        val etImageUrl = EditText(requireContext()).apply {
            hint = "Image URL (Firebase link)"
            setText(category?.imageUrl ?: "")
        }
        
        val layout = android.widget.LinearLayout(requireContext()).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 20, 50, 20)
            addView(etName)
            addView(etImageUrl)
        }
        
        AlertDialog.Builder(requireContext())
            .setTitle(if (isEditMode) "Edit Category" else "Add Category")
            .setView(layout)
            .setPositiveButton(if (isEditMode) "Update" else "Add") { _, _ ->
                val name = etName.text.toString().trim()
                val imageUrl = etImageUrl.text.toString().trim()
                
                if (ValidationUtils.isValidName(name)) {
                    if (isEditMode && category != null) {
                        updateCategory(category.id, name, imageUrl)
                    } else {
                        addCategory(name, imageUrl)
                    }
                } else {
                    Toast.makeText(requireContext(), "Please enter a valid name", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addCategory(name: String, imageUrl: String) {
        lifecycleScope.launch {
            val category = Category(
                name = name,
                imageUrl = imageUrl,
                sortOrder = 0
            )
            
            val result = categoryRepository.addCategory(category)
            
            result.onSuccess {
                Toast.makeText(requireContext(), "Category added successfully", Toast.LENGTH_SHORT).show()
                loadCategories()
            }.onFailure { exception ->
                Toast.makeText(
                    requireContext(),
                    "Failed to add category: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateCategory(categoryId: String, name: String, imageUrl: String) {
        lifecycleScope.launch {
            // First get the existing category
            val result = categoryRepository.getCategoryById(categoryId)
            
            result.onSuccess { existingCategory ->
                val updatedCategory = existingCategory.copy(
                    name = name,
                    imageUrl = imageUrl
                )
                
                categoryRepository.updateCategory(categoryId, updatedCategory).onSuccess {
                    Toast.makeText(requireContext(), "Category updated successfully", Toast.LENGTH_SHORT).show()
                    loadCategories()
                }.onFailure { exception ->
                    Toast.makeText(
                        requireContext(),
                        "Failed to update category: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showDeleteConfirmation(category: Category) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Category")
            .setMessage("Are you sure you want to delete ${category.name}?")
            .setPositiveButton("Delete") { _, _ ->
                deleteCategory(category.id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteCategory(categoryId: String) {
        lifecycleScope.launch {
            val result = categoryRepository.deleteCategory(categoryId)
            
            result.onSuccess {
                Toast.makeText(requireContext(), "Category deleted successfully", Toast.LENGTH_SHORT).show()
                loadCategories()
            }.onFailure { exception ->
                Toast.makeText(
                    requireContext(),
                    "Failed to delete category: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateCategoryStatus(categoryId: String, isActive: Boolean) {
        lifecycleScope.launch {
            val result = categoryRepository.toggleCategoryStatus(categoryId, isActive)
            
            result.onFailure { exception ->
                Toast.makeText(
                    requireContext(),
                    "Failed to update status: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
                loadCategories()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.jetpackcompose.foodorderingadmin.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.jetpackcompose.foodorderingadmin.R
import com.jetpackcompose.foodorderingadmin.databinding.ActivityAddEditFoodBinding
import com.jetpackcompose.foodorderingadmin.models.Category
import com.jetpackcompose.foodorderingadmin.models.Food
import com.jetpackcompose.foodorderingadmin.repository.CategoryRepository
import com.jetpackcompose.foodorderingadmin.repository.FoodRepository
import com.jetpackcompose.foodorderingadmin.repository.StorageRepository
import com.jetpackcompose.foodorderingadmin.utils.Constants
import com.jetpackcompose.foodorderingadmin.utils.ImageLoader
import com.jetpackcompose.foodorderingadmin.utils.ValidationUtils
import kotlinx.coroutines.launch

class AddEditFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditFoodBinding
    private val foodRepository = FoodRepository()
    private val categoryRepository = CategoryRepository()
    private val storageRepository = StorageRepository()
    
    private var isEditMode = false
    private var foodId: String? = null
    private var currentFood: Food? = null
    private var selectedImageUri: Uri? = null
    private var uploadedImageUrl: String = ""
    
    private var categories = listOf<Category>()
    private var selectedCategory: Category? = null

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.ivFoodImage.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Check if edit mode
        foodId = intent.getStringExtra(Constants.EXTRA_FOOD_ID)
        isEditMode = intent.getBooleanExtra(Constants.EXTRA_IS_EDIT_MODE, false)

        supportActionBar?.title = if (isEditMode) "Edit Food" else "Add Food"

        setupClickListeners()
        loadCategories()

        if (isEditMode && foodId != null) {
            loadFoodData(foodId!!)
        }
    }

    private fun setupClickListeners() {
        binding.fabPickImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            validateAndSave()
        }
    }

    private fun loadCategories() {
        lifecycleScope.launch {
            val result = categoryRepository.getAllCategories()
            
            result.onSuccess { categoriesList ->
                categories = categoriesList
                setupCategoryDropdown()
            }.onFailure {
                Toast.makeText(this@AddEditFoodActivity, "Failed to load categories", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupCategoryDropdown() {
        val categoryNames = categories.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categoryNames)
        binding.actvCategory.setAdapter(adapter)
        
        binding.actvCategory.setOnItemClickListener { _, _, position, _ ->
            selectedCategory = categories[position]
        }
    }

    private fun loadFoodData(foodId: String) {
        showLoading(true)
        
        lifecycleScope.launch {
            val result = foodRepository.getFoodById(foodId)
            
            result.onSuccess { food ->
                currentFood = food
                populateFields(food)
            }.onFailure { exception ->
                Toast.makeText(
                    this@AddEditFoodActivity,
                    "Failed to load food: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
            
            showLoading(false)
        }
    }

    private fun populateFields(food: Food) {
        binding.apply {
            etName.setText(food.name)
            etDescription.setText(food.description)
            etPrice.setText(food.price.toString())
            etDiscount.setText(food.discount.toString())
            etPrepTime.setText(food.preparationTime.toString())
            etIngredients.setText(food.ingredients.joinToString(", "))
            etImageUrl.setText(food.imageUrl)
            
            cbVegetarian.isChecked = food.isVegetarian
            cbSpicy.isChecked = food.isSpicy
            cbAvailable.isChecked = food.isAvailable
            
            uploadedImageUrl = food.imageUrl
            
            if (food.imageUrl.isNotEmpty()) {
                ImageLoader.loadImage(this@AddEditFoodActivity, food.imageUrl, ivFoodImage)
            }
            
            // Set category
            val categoryIndex = categories.indexOfFirst { it.id == food.categoryId }
            if (categoryIndex >= 0) {
                actvCategory.setText(categories[categoryIndex].name, false)
                selectedCategory = categories[categoryIndex]
            }
        }
    }

    private fun validateAndSave() {
        binding.apply {
            val name = etName.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val priceStr = etPrice.text.toString().trim()
            val discountStr = etDiscount.text.toString().trim()
            val prepTimeStr = etPrepTime.text.toString().trim()
            val imageUrlFromField = etImageUrl.text.toString().trim()
            val ingredientsStr = etIngredients.text.toString().trim()

            // Reset errors
            tilName.error = null
            tilDescription.error = null
            tilPrice.error = null
            tilCategory.error = null

            // Validation
            if (!ValidationUtils.isValidName(name)) {
                tilName.error = "Please enter a valid name"
                return
            }

            if (description.isEmpty()) {
                tilDescription.error = "Please enter description"
                return
            }

            if (!ValidationUtils.isValidPrice(priceStr)) {
                tilPrice.error = "Please enter a valid price"
                return
            }

            if (selectedCategory == null) {
                tilCategory.error = "Please select a category"
                return
            }

            val price = priceStr.toDouble()
            val discount = discountStr.toIntOrNull() ?: 0
            val prepTime = prepTimeStr.toIntOrNull() ?: 30
            
            val ingredients = if (ingredientsStr.isNotEmpty()) {
                ingredientsStr.split(",").map { it.trim() }
            } else {
                emptyList()
            }

            // Determine final image URL
            val finalImageUrl = when {
                imageUrlFromField.isNotEmpty() -> imageUrlFromField
                selectedImageUri != null -> {
                    // Will be uploaded
                    ""
                }
                else -> uploadedImageUrl
            }

            if (selectedImageUri != null && imageUrlFromField.isEmpty()) {
                uploadImageAndSave(
                    name, description, price, discount, prepTime,
                    ingredients, cbVegetarian.isChecked, cbSpicy.isChecked, cbAvailable.isChecked
                )
            } else {
                saveFood(
                    name, description, price, discount, prepTime,
                    ingredients, finalImageUrl, cbVegetarian.isChecked, cbSpicy.isChecked, cbAvailable.isChecked
                )
            }
        }
    }

    private fun uploadImageAndSave(
        name: String, description: String, price: Double, discount: Int, prepTime: Int,
        ingredients: List<String>, isVeg: Boolean, isSpicy: Boolean, isAvailable: Boolean
    ) {
        showLoading(true)
        
        lifecycleScope.launch {
            val result = storageRepository.uploadFoodImage(selectedImageUri!!, "${System.currentTimeMillis()}.jpg")
            
            result.onSuccess { imageUrl ->
                saveFood(name, description, price, discount, prepTime, ingredients, imageUrl, isVeg, isSpicy, isAvailable)
            }.onFailure { exception ->
                showLoading(false)
                Toast.makeText(
                    this@AddEditFoodActivity,
                    "Failed to upload image: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun saveFood(
        name: String, description: String, price: Double, discount: Int, prepTime: Int,
        ingredients: List<String>, imageUrl: String, isVeg: Boolean, isSpicy: Boolean, isAvailable: Boolean
    ) {
        showLoading(true)
        
        val food = if (isEditMode && currentFood != null) {
            currentFood!!.copy(
                name = name,
                description = description,
                price = price,
                discount = discount,
                preparationTime = prepTime,
                imageUrl = imageUrl,
                categoryId = selectedCategory!!.id,
                categoryName = selectedCategory!!.name,
                ingredients = ingredients,
                isVegetarian = isVeg,
                isSpicy = isSpicy,
                isAvailable = isAvailable
            )
        } else {
            Food(
                name = name,
                description = description,
                price = price,
                discount = discount,
                preparationTime = prepTime,
                imageUrl = imageUrl,
                categoryId = selectedCategory!!.id,
                categoryName = selectedCategory!!.name,
                ingredients = ingredients,
                isVegetarian = isVeg,
                isSpicy = isSpicy,
                isAvailable = isAvailable
            )
        }

        lifecycleScope.launch {
            val result = if (isEditMode && foodId != null) {
                foodRepository.updateFood(foodId!!, food)
            } else {
                foodRepository.addFood(food)
            }

            showLoading(false)

            result.onSuccess {
                Toast.makeText(
                    this@AddEditFoodActivity,
                    if (isEditMode) "Food updated successfully" else "Food added successfully",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }.onFailure { exception ->
                Toast.makeText(
                    this@AddEditFoodActivity,
                    "Failed to save food: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnSave.isEnabled = !show
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

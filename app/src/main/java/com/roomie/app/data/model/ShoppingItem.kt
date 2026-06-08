package com.roomie.app.data.model

data class ShoppingItem(
    val id: String = "",
    val name: String = "",
    val quantity: Int = 1,
    val category: String = ShoppingCategory.GROCERIES.name,
    val notes: String = "",
    val completed: Boolean = false,
    val addedBy: String = "",
    val householdId: String = ""
) {
    fun shoppingCategory(): ShoppingCategory = runCatching { ShoppingCategory.valueOf(category) }.getOrDefault(ShoppingCategory.GROCERIES)
}

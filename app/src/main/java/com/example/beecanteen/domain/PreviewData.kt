package com.example.beecanteen.domain

import com.example.beecanteen.domain.model.user.Beverages
import com.example.beecanteen.domain.model.user.Categories

val categories = listOf(
    Categories(
        id = "cat_1",
        name = "Hot Drinks",
        icon = "arrow",
        startTime = 0,
        endTime = 0
    ),
    Categories(
        id = "cat_2",
        name = "Cold Drinks",
        icon = "arrow",
        startTime = 0,
        endTime = 0
    ),
    Categories(
        id = "cat_3",
        name = "Juices",
        icon = "arrow",
        startTime = 0,
        endTime = 0
    ),
    Categories(
        id = "cat_4",
        name = "Smoothies",
        icon = "arrow",
        startTime = 0,
        endTime = 0
    ),
    Categories(
        id = "cat_5",
        name = "Sodas",
        icon = "arrow",
        startTime = 0,
        endTime = 0
    ),
    Categories(
        id = "cat_6",
        name = "Alcoholic",
        icon = "arrow",
        startTime = 0,
        endTime = 0
    )
)

val beverages = listOf(
    // Hot Drinks
    Beverages(id = "bev_1", name = "Espresso", categoryId = "cat_1"),
    Beverages(id = "bev_2", name = "Cappuccino", categoryId = "cat_1"),
    Beverages(id = "bev_3", name = "Latte", categoryId = "cat_1"),
    Beverages(id = "bev_4", name = "Chai Tea", categoryId = "cat_1"),
    Beverages(id = "bev_5", name = "Hot Chocolate", categoryId = "cat_1"),
    // Cold Drinks
    Beverages(id = "bev_6", name = "Iced Coffee", categoryId = "cat_2"),
    Beverages(id = "bev_7", name = "Cold Brew", categoryId = "cat_2"),
    Beverages(id = "bev_8", name = "Iced Matcha", categoryId = "cat_2"),
    Beverages(id = "bev_9", name = "Lemonade", categoryId = "cat_2"),
    // Juices
    Beverages(id = "bev_10", name = "Orange Juice", categoryId = "cat_3"),
    Beverages(id = "bev_11", name = "Apple Juice", categoryId = "cat_3"),
    Beverages(id = "bev_12", name = "Mango Juice", categoryId = "cat_3"),
    Beverages(id = "bev_13", name = "Grape Juice", categoryId = "cat_3"),
    // Smoothies
    Beverages(id = "bev_14", name = "Strawberry Smoothie", categoryId = "cat_4"),
    Beverages(id = "bev_15", name = "Banana Smoothie", categoryId = "cat_4"),
    Beverages(id = "bev_16", name = "Green Smoothie", categoryId = "cat_4"),
    // Sodas
    Beverages(id = "bev_17", name = "Cola", categoryId = "cat_5"),
    Beverages(id = "bev_18", name = "Ginger Ale", categoryId = "cat_5"),
    Beverages(id = "bev_19", name = "Root Beer", categoryId = "cat_5"),
    Beverages(id = "bev_20", name = "Club Soda", categoryId = "cat_5"),
    // Alcoholic
    Beverages(id = "bev_21", name = "Beer", categoryId = "cat_6"),
    Beverages(id = "bev_22", name = "Wine", categoryId = "cat_6"),
    Beverages(id = "bev_23", name = "Mojito", categoryId = "cat_6"),
    Beverages(id = "bev_24", name = "Margarita", categoryId = "cat_6")
)

val beverageMenu: Map<Categories, List<Beverages>> =
    categories.associateWith { category ->
        beverages.filter { it.categoryId == category.id }
    }

package com.example.beecanteen.domain

import com.example.beecanteen.domain.model.user.Beverage
import com.example.beecanteen.domain.model.user.Category

val categories = listOf(
    Category(
        id = "cat_1",
        name = "Hot Drinks",
        startTime = 0,
        endTime = 0,
        totalVoteCount = 0,
    ),
    Category(
        id = "cat_2",
        name = "Cold Drinks",
        startTime = 0,
        endTime = 0,
        totalVoteCount = 0,
    ),
    Category(
        id = "cat_3",
        name = "Juices",
        startTime = 0,
        endTime = 0,
        totalVoteCount = 0,
    ),
    Category(
        id = "cat_4",
        name = "Smoothies",
        startTime = 0,
        endTime = 0,
        totalVoteCount = 0,
    ),
    Category(
        id = "cat_5",
        name = "Sodas",
        startTime = 0,
        endTime = 0,
        totalVoteCount = 0,
    ),
    Category(
        id = "cat_6",
        name = "Alcoholic",
        startTime = 0,
        endTime = 0,
        totalVoteCount = 0,
    )
)

val beverages = listOf(
    // Hot Drinks
    Beverage(id = "bev_1", name = "Espresso", voteCount = 0),
    Beverage(id = "bev_2", name = "Cappuccino", voteCount = 0),
    Beverage(id = "bev_3", name = "Latte", voteCount = 0),
    Beverage(id = "bev_4", name = "Chai Tea", voteCount = 0),
    Beverage(id = "bev_5", name = "Hot Chocolate", voteCount = 0),
    // Cold Drinks
//    Beverage(id = "bev_6", name = "Iced Coffee"),
//    Beverage(id = "bev_7", name = "Cold Brew"),
//    Beverage(id = "bev_8", name = "Iced Matcha"),
//    Beverage(id = "bev_9", name = "Lemonade"),
//    // Juices
//    Beverage(id = "bev_10", name = "Orange Juice"),
//    Beverage(id = "bev_11", name = "Apple Juice"),
//    Beverage(id = "bev_12", name = "Mango Juice"),
//    Beverage(id = "bev_13", name = "Grape Juice"),
//    // Smoothies
//    Beverage(id = "bev_14", name = "Strawberry Smoothie",,
//    Beverage(id = "bev_15", name = "Banana Smoothie"),
//    Beverage(id = "bev_16", name = "Green Smoothie"),
//    // Sodas
//    Beverage(id = "bev_17", name = "Cola"),
//    Beverage(id = "bev_18", name = "Ginger Ale"),
//    Beverage(id = "bev_19", name = "Root Beer"),
//    Beverage(id = "bev_20", name = "Club Soda"),
//    // Alcoholic
//    Beverage(id = "bev_21", name = "Beer"),
//    Beverage(id = "bev_22", name = "Wine"),
//    Beverage(id = "bev_23", name = "Mojito"),
//    Beverage(id = "bev_24", name = "Margarita")
)

//val beverageMenu: Map<Category, List<Beverage>> =
//    categories.associateWith { category ->
//        beverages.filter { it.categoryId == category.id }
//    }

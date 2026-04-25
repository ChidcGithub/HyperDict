package com.hyperdict.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Metro UI style shapes - mostly sharp corners with slight rounding
// Metro design emphasizes clean, geometric shapes
val MetroShapes = Shapes(
    extraSmall = RoundedCornerShape(2.dp),  // Nearly sharp
    small = RoundedCornerShape(4.dp),       // Minimal rounding
    medium = RoundedCornerShape(4.dp),      // Minimal rounding for cards
    large = RoundedCornerShape(4.dp),       // Slight rounding for large surfaces
    extraLarge = RoundedCornerShape(8.dp)   // Moderate rounding for very large surfaces
)

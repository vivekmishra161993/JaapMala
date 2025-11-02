package com.mtt.presentation.ui.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun AnimatedBottomBar(
    tabs: List<BottomTabItem>,
    currentRoute: String?,
    onTabSelected: (BottomTabItem) -> Unit
) {
    NavigationBar(
        tonalElevation = 4.dp
    ) {
        tabs.forEach { tab ->
            val selected = currentRoute == tab.route
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(tab) },
                icon = { AnimatedTabIcon(tab, selected) },
                label = {
                    AnimatedVisibility(
                        visible = selected,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) { Text(tab.title) }
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}

@Composable
private fun AnimatedTabIcon(tab: BottomTabItem, selected: Boolean) {
    when (tab.route) {
        "jaaps_tab" -> SwingingIcon(tab.icon, selected)
        "progress_tab" -> ProgressPulseIcon(tab.icon, selected)
        "goals_tab" -> BouncingIcon(tab.icon, selected)
        else -> Icon(tab.icon, contentDescription = tab.title)
    }
}

@Composable
private fun SwingingIcon(icon: ImageVector, selected: Boolean) {
    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(selected) {
        if (selected) {
            scope.launch {
                rotation.animateTo(15f, tween(120))
                rotation.animateTo(-15f, tween(120))
                rotation.animateTo(8f, tween(100))
                rotation.animateTo(0f, tween(100))
            }
        }
    }

    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier
            .size(26.dp)
            .rotate(rotation.value)
    )
}

@Composable
private fun ProgressPulseIcon(icon: ImageVector, selected: Boolean) {
    val scaleY = remember { Animatable(1f) }

    LaunchedEffect(selected) {
        if (selected) {
            scaleY.snapTo(1f)
            scaleY.animateTo(
                1.25f,
                animationSpec = tween(durationMillis = 220, easing = LinearOutSlowInEasing)
            )
            scaleY.animateTo(1f, tween(durationMillis = 220))
        }
    }

    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier
            .size(26.dp)
            .graphicsLayer {
                // use the Animatable value directly
                this.scaleY = scaleY.value
            },
        tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    )
}
@Composable
private fun BouncingIcon(icon: ImageVector, selected: Boolean) {
    val scale = remember { Animatable(1f) }

    LaunchedEffect(selected) {
        if (selected) {
            scale.animateTo(1.3f, spring(stiffness = Spring.StiffnessMedium))
            scale.animateTo(1f, spring(stiffness = Spring.StiffnessLow))
        }
    }

    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier
            .size(26.dp)
            .scale(scale.value)
    )
}

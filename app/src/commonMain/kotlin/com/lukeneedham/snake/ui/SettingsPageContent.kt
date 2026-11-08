package com.lukeneedham.snake.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukeneedham.snake.game.SprintSide
import com.lukeneedham.snake.generated.resources.Res
import com.lukeneedham.snake.generated.resources.settings_title
import com.lukeneedham.snake.generated.resources.sprint_side_left
import com.lukeneedham.snake.generated.resources.sprint_side_right
import com.lukeneedham.snake.generated.resources.sprint_side_title
import com.lukeneedham.snake.ui.theme.AppTheme
import com.lukeneedham.common.ui.DebugButton
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/** All of the page's UI. `internal`: the screenshots of the app use it too. */
@Composable
internal fun SettingsPageContent(
    state: SettingsUiState,
    onSprintSide: (SprintSide) -> Unit,
    onOpenDebug: () -> Unit,
) {
    val theme = AppTheme.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.background)
            .safeDrawingPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        BasicText(
            text = stringResource(Res.string.settings_title),
            style = TextStyle(
                color = theme.onBackground,
                fontFamily = theme.fontFamily,
                fontSize = 24.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.SemiBold,
            ),
        )
        SprintSideSetting(selected = state.sprintSide, onSelect = onSprintSide)
        // Draws nothing unless debugging is on, so it stays in the layout for release builds
        if (state.isDebugEnabled) DebugButton(onClick = onOpenDebug)
    }
}

/** The setting of which bottom corner of the screen sprints: a choice of the left or the right one. */
@Composable
private fun SprintSideSetting(selected: SprintSide, onSelect: (SprintSide) -> Unit) {
    val theme = AppTheme.current
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        BasicText(
            text = stringResource(Res.string.sprint_side_title),
            style = TextStyle(color = theme.onBackground, fontFamily = theme.fontFamily, fontSize = 16.sp, lineHeight = 24.sp, fontWeight = FontWeight.Medium),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            for (side in SprintSide.entries) {
                val isSelected = side == selected
                BasicText(
                    text = stringResource(if (side == SprintSide.Left) Res.string.sprint_side_left else Res.string.sprint_side_right),
                    style = TextStyle(
                        color = if (isSelected) theme.onPrimary else theme.onSurface,
                        fontFamily = theme.fontFamily,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) theme.primary else theme.surface)
                        .selectable(selected = isSelected, role = Role.RadioButton, onClick = { onSelect(side) })
                        .padding(horizontal = 24.dp, vertical = 14.dp),
                )
            }
        }
    }
}

/** For Android Studio while building the page: debugging on. */
@Preview
@Composable
private fun Preview() {
    AppTheme {
        SettingsPageContent(state = SettingsUiState(isDebugEnabled = true), onSprintSide = {}, onOpenDebug = {})
    }
}
